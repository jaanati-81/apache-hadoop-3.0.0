/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.datatransfer.PacketHeader;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.ReplicaState;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeReference;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.LengthInputStream;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.ReplicaInputStreams;
import org.apache.hadoop.hdfs.util.DataTransferThrottler;
import org.apache.hadoop.io.IOUtils;
//import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ReadaheadPool.ReadaheadRequest;
//import org.apache.hadoop.io.erasurecode.coder.TRErasureEncoder;
import org.apache.hadoop.net.SocketOutputStream;
import org.apache.hadoop.util.AutoCloseableLock;
import org.apache.hadoop.util.DataChecksum;
import org.apache.htrace.core.TraceScope;
import org.apache.hadoop.hdfs.server.datanode.erasurecode.HelperTable96;
import org.apache.hadoop.util.OurECLogger;

import static org.apache.hadoop.io.nativeio.NativeIO.POSIX.POSIX_FADV_DONTNEED;
import static org.apache.hadoop.io.nativeio.NativeIO.POSIX.POSIX_FADV_SEQUENTIAL;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;

/**
 * Reads a block from the disk and sends it to a recipient.
 *
 * Data sent from the BlockSender in the following format:
 * <br><b>Data format:</b> <pre>
 *    +--------------------------------------------------+
 *    | ChecksumHeader | Sequence of data PACKETS...     |
 *    +--------------------------------------------------+
 * </pre>
 * <b>ChecksumHeader format:</b> <pre>
 *    +--------------------------------------------------+
 *    | 1 byte CHECKSUM_TYPE | 4 byte BYTES_PER_CHECKSUM |
 *    +--------------------------------------------------+
 * </pre>
 * An empty packet is sent to mark the end of block and read completion.
 *
 * PACKET Contains a packet header, checksum and data. Amount of data
 * carried is set by BUFFER_SIZE.
 * <pre>
 *   +-----------------------------------------------------+
 *   | Variable length header. See {@link PacketHeader}    |
 *   +-----------------------------------------------------+
 *   | x byte checksum data. x is defined below            |
 *   +-----------------------------------------------------+
 *   | actual data ......                                  |
 *   +-----------------------------------------------------+
 *
 *   Data is made of Chunks. Each chunk is of length <= BYTES_PER_CHECKSUM.
 *   A checksum is calculated for each chunk.
 *
 *   x = (length of data + BYTE_PER_CHECKSUM - 1)/BYTES_PER_CHECKSUM *
 *       CHECKSUM_SIZE
 *
 *   CHECKSUM_SIZE depends on CHECKSUM_TYPE (usually, 4 for CRC32)
 *  </pre>
 *
 *  The client reads data until it receives a packet with
 *  "LastPacketInBlock" set to true or with a zero length. If there is
 *  no checksum error, it replies to DataNode with OP_STATUS_CHECKSUM_OK.
 */
class BlockTraceSender implements java.io.Closeable {
    static final Logger LOG = DataNode.LOG;
    private static OurECLogger ourlog = OurECLogger.getLogger(BlockTraceSender.class);
    static final Log ClientTraceLog = DataNode.ClientTraceLog;
    private static final boolean is32Bit =
            System.getProperty("sun.arch.data.model").equals("32");
    /**
     * Minimum buffer used while sending data to clients. Used only if
     * transferTo() is enabled. 64KB is not that large. It could be larger, but
     * not sure if there will be much more improvement.
     */
    private static final int MIN_BUFFER_WITH_TRANSFERTO = 64*1024;
    private static final int IO_FILE_BUFFER_SIZE;
    static {
        HdfsConfiguration conf = new HdfsConfiguration();
        IO_FILE_BUFFER_SIZE = DFSUtilClient.getIoFileBufferSize(conf);
    }
    private static final int TRANSFERTO_BUFFER_SIZE = Math.max(
            IO_FILE_BUFFER_SIZE, MIN_BUFFER_WITH_TRANSFERTO);

    /** the block to read from */
    private final ExtendedBlock block;

    /** InputStreams and file descriptors to read block/checksum. */
    private ReplicaInputStreams ris;
    /** updated while using transferTo() */
    private long blockInPosition = -1;
    /** Checksum utility */
    private final DataChecksum checksum;
    /** Initial position to read */
    private long initialOffset;
    /** Current position of read */
    private long offset;
    /** Position of last byte to read from block file */
    private final long endOffset;
    /** Number of bytes in chunk used for computing checksum */
    private final int chunkSize;
    /** Number bytes of checksum computed for a chunk */
    private final int checksumSize;
    /** If true, failure to read checksum is ignored */
    private final boolean corruptChecksumOk;
    /** Sequence number of packet being sent */
    private long seqno;
    /** Set to true if transferTo is allowed for sending data to the client */
    private final boolean transferToAllowed;
    /** Set to true once entire requested byte range has been sent to the client */
    private boolean sentEntireByteRange;
    /** When true, verify checksum while reading from checksum file */
    private final boolean verifyChecksum;
    /** Format used to print client trace log messages */
    private final String clientTraceFmt;
    private volatile ChunkChecksum lastChunkChecksum = null;
    private DataNode datanode;

    /** The replica of the block that is being read. */
    private final Replica replica;

    // Cache-management related fields
    private final long readaheadLength;

    private ReadaheadRequest curReadahead;

    private final boolean alwaysReadahead;

    private final boolean dropCacheBehindLargeReads;

    private final boolean dropCacheBehindAllReads;

    private long lastCacheDropOffset;
    private final FileIoProvider fileIoProvider;

    /** The total number of data blocks of this Erasure code setting*/
    private final int dataBlkNum;

    /** The total number of parity blocks of this Erasure code setting*/
    private final int parityBlkNum;

    /** The index of the lost node of the stripe in this Erasure code setting*/
    private final int lostNodeIndex;
    /** The index of this helper node of this trace repair erasure code setting*/
    private final int helperNodeIndex;

    /** to access entries of the Helper table */
    private  HelperTable96 helperTable = new HelperTable96();

    @VisibleForTesting
    static long CACHE_DROP_INTERVAL_BYTES = 1024 * 1024; // 1MB

    /**
     * See {{@link BlockTraceSender#isLongRead()}
     */
    private static final long LONG_READ_THRESHOLD_BYTES = 256 * 1024;


    /**
     * Constructor
     *
     * @param block Block that is being read
     * @param startOffset starting offset to read from
     * @param length length of data to read
     * @param corruptChecksumOk if true, corrupt checksum is okay
     * @param verifyChecksum verify checksum while reading the data
     * @param sendChecksum send checksum to client.
     * @param datanode datanode from which the block is being read
     * @param clientTraceFmt format string used to print client trace logs
     * @throws IOException
     */
    BlockTraceSender(ExtendedBlock block, long startOffset, long length,
                     boolean corruptChecksumOk, boolean verifyChecksum,
                     boolean sendChecksum, DataNode datanode, String clientTraceFmt,
                     CachingStrategy cachingStrategy, int lostNodeIndex, int helperNodeIndex, int dataBlkNum, int parityBlkNum)
            throws IOException {
        InputStream blockIn = null;
        DataInputStream checksumIn = null;
        FsVolumeReference volumeRef = null;
        this.fileIoProvider = datanode.getFileIoProvider();
        try {
            this.block = block;
            this.corruptChecksumOk = corruptChecksumOk;
            this.verifyChecksum = verifyChecksum;
            this.clientTraceFmt = clientTraceFmt;
            this.dataBlkNum = dataBlkNum;
            this.parityBlkNum = parityBlkNum;
            //this.totalBlocks = dataBlkNum + parityBlkNum;
            this.lostNodeIndex = lostNodeIndex;
            this.helperNodeIndex = helperNodeIndex;

            /*
             * If the client asked for the cache to be dropped behind all reads,
             * we honor that.  Otherwise, we use the DataNode defaults.
             * When using DataNode defaults, we use a heuristic where we only
             * drop the cache for large reads.
             */
            if (cachingStrategy.getDropBehind() == null) {
                this.dropCacheBehindAllReads = false;
                this.dropCacheBehindLargeReads =
                        datanode.getDnConf().dropCacheBehindReads;
            } else {
                this.dropCacheBehindAllReads =
                        this.dropCacheBehindLargeReads =
                                cachingStrategy.getDropBehind().booleanValue();
            }

            ourlog.write("startOffset: "+startOffset);
            ourlog.write("length: "+length);
            /*
             * Similarly, if readahead was explicitly requested, we always do it.
             * Otherwise, we read ahead based on the DataNode settings, and only
             * when the reads are large.
             */
            if (cachingStrategy.getReadahead() == null) {
                this.alwaysReadahead = false;
                this.readaheadLength = datanode.getDnConf().readaheadLength;
            } else {
                this.alwaysReadahead = true;
                this.readaheadLength = cachingStrategy.getReadahead().longValue();
            }
            this.datanode = datanode;

            if (verifyChecksum) {
                // To simplify implementation, callers may not specify verification
                // without sending.
                Preconditions.checkArgument(sendChecksum,
                        "If verifying checksum, currently must also send it.");
            }

            // if there is a append write happening right after the BlockSender
            // is constructed, the last partial checksum maybe overwritten by the
            // append, the BlockSender need to use the partial checksum before
            // the append write.
            ChunkChecksum chunkChecksum = null;
            final long replicaVisibleLength;
            try(AutoCloseableLock lock = datanode.data.acquireDatasetLock()) {
                replica = getReplica(block, datanode);

                ourlog.write("Replica State is: "+replica.getState());
                replicaVisibleLength = replica.getVisibleLength();
                ourlog.write("Replica Length is: "+replicaVisibleLength);

                if (replica instanceof FinalizedReplica) {
                    // Load last checksum in case the replica is being written
                    // concurrently
                    final FinalizedReplica frep = (FinalizedReplica) replica;
                    chunkChecksum = frep.getLastChecksumAndDataLen();
                    ourlog.write("chunkChecksum for FinalizedReplica set");
                }
            }

            if (replica.getState() == ReplicaState.RBW) {
                final ReplicaInPipeline rbw = (ReplicaInPipeline) replica;
                waitForMinLength(rbw, startOffset + length);
                chunkChecksum = rbw.getLastChecksumAndDataLen();
                ourlog.write("chunkChecksum for RBW replica set");
            }

            if (replica.getGenerationStamp() < block.getGenerationStamp()) {
                throw new IOException("Replica gen stamp < block genstamp, block="
                        + block + ", replica=" + replica);
            } else if (replica.getGenerationStamp() > block.getGenerationStamp()) {
                if (DataNode.LOG.isDebugEnabled()) {
                    DataNode.LOG.debug("Bumping up the client provided"
                            + " block's genstamp to latest " + replica.getGenerationStamp()
                            + " for block " + block);
                }
                block.setGenerationStamp(replica.getGenerationStamp());
            }
            if (replicaVisibleLength < 0) {
                throw new IOException("Replica is not readable, block="
                        + block + ", replica=" + replica);
            }
            if (DataNode.LOG.isDebugEnabled()) {
                DataNode.LOG.debug("block=" + block + ", replica=" + replica);
            }

            // transferToFully() fails on 32 bit platforms for block sizes >= 2GB,
            // use normal transfer in those cases
            this.transferToAllowed = datanode.getDnConf().transferToAllowed &&
                    (!is32Bit || length <= Integer.MAX_VALUE);

            // Obtain a reference before reading data
            volumeRef = datanode.data.getVolume(block).obtainReference();

            /*
             * (corruptChecksumOK, meta_file_exist): operation
             * True,   True: will verify checksum
             * True,  False: No verify, e.g., need to read data from a corrupted file
             * False,  True: will verify checksum
             * False, False: throws IOException file not found
             */
            DataChecksum csum = null;
            if (verifyChecksum || sendChecksum) {
                LengthInputStream metaIn = null;
                boolean keepMetaInOpen = false;
                try {
                    DataNodeFaultInjector.get().throwTooManyOpenFiles();
                    metaIn = datanode.data.getMetaDataInputStream(block);
                    if (!corruptChecksumOk || metaIn != null) {
                        if (metaIn == null) {
                            //need checksum but meta-data not found
                            throw new FileNotFoundException("Meta-data not found for " +
                                    block);
                        }

                        // The meta file will contain only the header if the NULL checksum
                        // type was used, or if the replica was written to transient storage.
                        // Also, when only header portion of a data packet was transferred
                        // and then pipeline breaks, the meta file can contain only the
                        // header and 0 byte in the block data file.
                        // Checksum verification is not performed for replicas on transient
                        // storage.  The header is important for determining the checksum
                        // type later when lazy persistence copies the block to non-transient
                        // storage and computes the checksum.
                        if (!replica.isOnTransientStorage() &&
                                metaIn.getLength() >= BlockMetadataHeader.getHeaderSize()) {
                            checksumIn = new DataInputStream(new BufferedInputStream(
                                    metaIn, IO_FILE_BUFFER_SIZE));

                            csum = BlockMetadataHeader.readDataChecksum(checksumIn, block);
                            keepMetaInOpen = true;
                        }
                    } else {
                        LOG.warn("Could not find metadata file for " + block);
                    }
                } catch (FileNotFoundException e) {
                    if ((e.getMessage() != null) && !(e.getMessage()
                            .contains("Too many open files"))) {
                        // The replica is on its volume map but not on disk
                        datanode.notifyNamenodeDeletedBlock(block, replica.getStorageUuid());
                        datanode.data.invalidate(block.getBlockPoolId(),
                                new Block[] {block.getLocalBlock()});
                    }
                    throw e;
                } finally {
                    if (!keepMetaInOpen) {
                        IOUtils.closeStream(metaIn);
                    }
                }
            }
            if (csum == null) {
                // The number of bytes per checksum here determines the alignment
                // of reads: we always start reading at a checksum chunk boundary,
                // even if the checksum type is NULL. So, choosing too big of a value
                // would risk sending too much unnecessary data. 512 (1 disk sector)
                // is likely to result in minimal extra IO.
                csum = DataChecksum.newDataChecksum(DataChecksum.Type.NULL, 512);
            }

           // ourlog.write("Checksum Type: "+csum.getChecksumType());
           // ourlog.write("Bytes Per Checksum: "+csum.getBytesPerChecksum());

            /*
             * If chunkSize is very large, then the metadata file is mostly
             * corrupted. For now just truncate bytesPerchecksum to blockLength.
             */

            //commented for trace repair, we ignore checksums
         /*   int size = csum.getBytesPerChecksum();
            if (size > 10*1024*1024 && size > replicaVisibleLength) {
                csum = DataChecksum.newDataChecksum(csum.getChecksumType(),
                        Math.max((int)replicaVisibleLength, 10*1024*1024));
                size = csum.getBytesPerChecksum();
            } */
            //chunkSize = size;

            chunkSize = csum.getBytesPerChecksum();
            ourlog.write("ChunkSize: "+chunkSize);

            //chunkSize = 0;
            checksum = csum;

            //checksumSize = checksum.getChecksumSize();
            //For trace repair, we do not care about checksums
            checksumSize = 0;
            length = length < 0 ? replicaVisibleLength : length;

            // end is either last byte on disk or the length for which we have a
            // checksum
            long end = chunkChecksum != null ? chunkChecksum.getDataLength()
                    : replica.getBytesOnDisk();
            if (startOffset < 0 || startOffset > end
                    || (length + startOffset) > end) {
                String msg = " Offset " + startOffset + " and length " + length
                        + " don't match block " + block + " ( blockLen " + end + " )";
                LOG.warn(datanode.getDNRegistrationForBP(block.getBlockPoolId()) +
                        ":sendBlock() : " + msg);
                throw new IOException(msg);
            }

        //    ourlog.write("startOffset: "+startOffset);
          //  ourlog.write("length: "+length);
            // Ensure read offset is position at the beginning of chunk
            offset = startOffset - (startOffset % chunkSize);
            if (length >= 0) {
                // Ensure endOffset points to end of chunk.
                long tmpLen = startOffset + length;
                if (tmpLen % chunkSize != 0) {
                    tmpLen += (chunkSize - tmpLen % chunkSize);
                }
                if (tmpLen < end) {
                    // will use on-disk checksum here since the end is a stable chunk
                    end = tmpLen;
                } else if (chunkChecksum != null) {
                    // last chunk is changing. flag that we need to use in-memory checksum
                    this.lastChunkChecksum = chunkChecksum;
                }
            }
            endOffset = end;

            ourlog.write("In BlockTraceSender constructor, chunkSize: "+chunkSize);
            ourlog.write("In BlockTraceSender constructor, checksumSize: "+checksumSize);
            // seek to the right offsets
            if (offset > 0 && checksumIn != null) {
                long checksumSkip = (offset / chunkSize) * checksumSize;
                // note blockInStream is seeked when created below
                if (checksumSkip > 0) {
                    // Should we use seek() for checksum file as well?
                    IOUtils.skipFully(checksumIn, checksumSkip);
                }
            }
            seqno = 0;

            if (DataNode.LOG.isDebugEnabled()) {
                DataNode.LOG.debug("replica=" + replica);
            }
            blockIn = datanode.data.getBlockInputStream(block, offset); // seek to offset
            ris = new ReplicaInputStreams(
                    blockIn, checksumIn, volumeRef, fileIoProvider);
        } catch (IOException ioe) {
            IOUtils.closeStream(this);
            org.apache.commons.io.IOUtils.closeQuietly(blockIn);
            org.apache.commons.io.IOUtils.closeQuietly(checksumIn);
            throw ioe;
        }
        ourlog.write("In BlockTraceSender constructor, lostNodeIndex: "+lostNodeIndex);
        ourlog.write("In BlockTraceSender constructor, this helperNodeIndex: "+helperNodeIndex);
        ourlog.write("In BlockTraceSender constructor, dataBlkNum: "+dataBlkNum);
        ourlog.write("In BlockTraceSender constructor, parityBlkNum: "+parityBlkNum);
    }

    /**
     * Function to return the bit at some position from a byte
     * @param b the input byte
     * @param position the position to get the bit from
     */

    private boolean getBit(byte b, int position){

        return (1 == ((b >> position) & 1));
    }


    /**
     * Function to return all set bit positions of a number
     * @param inputNumber an integer input
     */
    private List<Integer> getBitPositions(int inputNumber) {
        List<Integer> positions = new ArrayList<>();
        int index = 0; // start at bit index 0

        while (inputNumber != 0) { // If the number is 0, no bits are set

            // check if the bit at the current index 0 is set
            if ((inputNumber & 1) == 1)
                // it is, save this bit index to a List.
                positions.add(index);
            // advance to the next bit position to check
            inputNumber = inputNumber >> 1; // shift all bits one position to the right
            index = index + 1;              // we have to now look at the next index.
        }
        return positions;
    }


    /**
     * close opened files.
     */
    @Override
    public void close() throws IOException {
        if (ris.getDataInFd() != null &&
                ((dropCacheBehindAllReads) ||
                        (dropCacheBehindLargeReads && isLongRead()))) {
            try {
                ris.dropCacheBehindReads(block.getBlockName(), lastCacheDropOffset,
                        offset - lastCacheDropOffset, POSIX_FADV_DONTNEED);
            } catch (Exception e) {
                LOG.warn("Unable to drop cache on file close", e);
            }
        }
        if (curReadahead != null) {
            curReadahead.cancel();
        }

        try {
            ris.closeStreams();
        } finally {
            IOUtils.closeStream(ris);
            ris = null;
        }
    }

    private static Replica getReplica(ExtendedBlock block, DataNode datanode)
            throws ReplicaNotFoundException {
        Replica replica = datanode.data.getReplica(block.getBlockPoolId(),
                block.getBlockId());
        if (replica == null) {
            throw new ReplicaNotFoundException(block);
        }
        return replica;
    }

    /**
     * Wait for rbw replica to reach the length
     * @param rbw replica that is being written to
     * @param len minimum length to reach
     * @throws IOException on failing to reach the len in given wait time
     */
    private static void waitForMinLength(ReplicaInPipeline rbw, long len)
            throws IOException {
        // Wait for 3 seconds for rbw replica to reach the minimum length
        for (int i = 0; i < 30 && rbw.getBytesOnDisk() < len; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                throw new IOException(ie);
            }
        }
        long bytesOnDisk = rbw.getBytesOnDisk();
        if (bytesOnDisk < len) {
            throw new IOException(
                    String.format("Need %d bytes, but only %d bytes available", len,
                            bytesOnDisk));
        }
    }

    /**
     * Converts an IOExcpetion (not subclasses) to SocketException.
     * This is typically done to indicate to upper layers that the error
     * was a socket error rather than often more serious exceptions like
     * disk errors.
     */
    private static IOException ioeToSocketException(IOException ioe) {
        if (ioe.getClass().equals(IOException.class)) {
            // "se" could be a new class in stead of SocketException.
            IOException se = new SocketException("Original Exception : " + ioe);
            se.initCause(ioe);
            /* Change the stacktrace so that original trace is not truncated
             * when printed.*/
            se.setStackTrace(ioe.getStackTrace());
            return se;
        }
        // otherwise just return the same exception.
        return ioe;
    }



    /**
     * Function to compute helper traces of the bytes from this helper node
     * Runs at each of the helper node and sends to the worker node for recovery
     * @param codeSymbols the code symbol byte array corresponding to the block contents
     * @param lostBlockIndex the erased node index
     * @return the traces of all bytes from this block
     * @throws IOException
     */

    private boolean[] computeTraces(byte[] codeSymbols, int lostBlockIndex) throws IOException {

        int i = lostBlockIndex;
        ourlog.write("Lost Block Index is: "+lostBlockIndex);
        int k = 0;
        int j = helperNodeIndex;
        ourlog.write("Inside computeTraces...this helper node index: "+helperNodeIndex);


        Object element = helperTable.getElement(j,i);
        String s = element.toString();
        String[] elements = s.split(",");
        int traceBandwidth = Integer.parseInt(elements[0]);
        ourlog.write("traceBandwidth is: "+traceBandwidth);

        //The boolean array that will hold the trace bits from all the code symbols of this helper
        int sizeOfBoolArray = codeSymbols.length*traceBandwidth; //number of bytes times traceBandwidth
        ourlog.write("Size of the boolean trace array is: "+sizeOfBoolArray);
        boolean[] helperTraces = new boolean[sizeOfBoolArray];

        //Iterate though all bytes of the byte array which has the block's data
        //for each byte, find the helper trace, add it to the helperTraces bitset
        int p =0;
        for(byte codeSymbol : codeSymbols){
            for (int l = 1; l <= traceBandwidth; l++) {
                String helperString = elements[l].toString();
                Integer helperInt = Integer.parseInt(helperString.trim());

                //Find the set bit positions of the helper table element
                List<Integer> positions = getBitPositions(helperInt);

                boolean traceBitsXor = false;

                for(Integer position : positions){
                    //XOR all bits of the codeword symbol at the set positions of H table element
                    traceBitsXor = traceBitsXor ^ getBit(codeSymbol, position);
                }
                helperTraces[p] = traceBitsXor;
                //  ourlog.write("Helper trace boolean array size: "+helperTraces.length);
                // helperTraces.set(p,traceBitsXor);
                p++;
            }
            //ourlog.write("To the next byte of the codeword ...");
        }
        ourlog.write("Helper trace boolean array size, after all traces computed: "+helperTraces.length);
        return(helperTraces);


    }

    /**
     * @param datalen Length of data
     * @return number of chunks for data of given size
     */
    private int numberOfChunks(long datalen) {
        return (int) ((datalen + chunkSize - 1)/chunkSize);
    }


    /**
     * Convert a boolean array into a byte array
     *  @param bools a boolean array of boolean values
     *  @return a byte[] containing the boolean values (zero padded if boolean array is not a multiple of 8)
     *  adapted from https://sakai.rutgers.edu/wiki/site/e07619c5-a492-4ebe-8771-179dfe450ae4/bit-to-boolean%20conversion.html
     */
    public static byte[] convertBooleanToByte(boolean[] bools) {
        int length = bools.length / 8;
        int mod = bools.length % 8;
        if(mod != 0){
            ++length;
        }
        byte[] retVal = new byte[length];
        int boolIndex = 0;
        for (int byteIndex = 0; byteIndex < retVal.length; ++byteIndex) {
            for (int bitIndex = 7; bitIndex >= 0; --bitIndex) {
                if (boolIndex >= bools.length) {
                    return retVal;
                }
                if (bools[boolIndex++]) {
                    retVal[byteIndex] |= (byte) (1 << bitIndex);
                }
            }
        }

        return retVal;
    }

    /**
     * Sends a packet with up to maxChunks chunks of data.
     *
     * @param pkt buffer used for writing packet data
     * @param maxChunks maximum number of chunks to send
     * @param out stream to send data to
     * @param transferTo use transferTo to send data
     * @param throttler used for throttling data transfer bandwidth
     */
    private int sendPacket(ByteBuffer pkt, int maxChunks, OutputStream out,
                           boolean transferTo, DataTransferThrottler throttler) throws IOException {
        int dataLen = (int) Math.min(endOffset - offset,
                (chunkSize * (long) maxChunks));

        ourlog.write("In sendPacket(), dataLen: "+dataLen);

        //int numChunks = numberOfChunks(dataLen); // Number of chunks be sent in the packet
        int checksumDataLen = 0;
        int packetLen = dataLen + checksumDataLen + 4;

        boolean lastDataPacket = offset + dataLen == endOffset && dataLen > 0;

        // The packet buffer is organized as follows:
        // _______HHHHCCCCD?D?D?D?
        //        ^   ^
        //        |   \ checksumOff
        //        \ headerOff
        // _ padding, since the header is variable-length
        // H = header and length prefixes
        // C = checksums (We ignore the checksums for trace repair erasure coding)
        // D? = data, if transferTo is false.



        int headerLen = writePacketHeader(pkt, dataLen, packetLen);
        ourlog.write("In sendPacket(), headerLen after writing header to the packet: "+headerLen);

        int headerOff = pkt.position() - headerLen;

        ourlog.write("Current header offset: "+headerOff);

 /*       int checksumOff = pkt.position();


        byte[] buf = pkt.array();

      if (checksumSize > 0 && ris.getChecksumIn() != null) {
            readChecksum(buf, checksumOff, checksumDataLen);

            // write in progress that we need to use to get last checksum
            if (lastDataPacket && lastChunkChecksum != null) {
                int start = checksumOff + checksumDataLen - checksumSize;
                byte[] updatedChecksum = lastChunkChecksum.getChecksum();
                if (updatedChecksum != null) {
                    System.arraycopy(updatedChecksum, 0, buf, start, checksumSize);
                }
            }
        }


        int dataOff = checksumOff + checksumDataLen;
*/


       int dataOff = pkt.position();

        ourlog.write("After writing header, current packet position() is: "+dataOff);

        //construct a temporary byte array to read the whole block contents into and then compute trace of the bytes in the array
        byte[] tmpPktBuf = new byte[dataLen];

        ourlog.write("Size of the temporary byte array : "+tmpPktBuf.length);

        // buf The buffer to fill
        // off offset from the buffer
        // len the length of bytes to read
        ris.readDataFully(tmpPktBuf, 0, dataLen);


        ourlog.write("Temporary Packet Buffer length after the readDataFully call : "+tmpPktBuf.length);

        // TRACE COMPUTATION CALL with the read block contents and erased index
        // returns a boolean array carrying the trace bits of all the bytes in the input byte array
        boolean[] helperTraceBool = computeTraces(tmpPktBuf, lostNodeIndex);

        ourlog.write("Helper traces boolean array length: "+helperTraceBool.length);


        // convert the boolean trace array to a byte array
        byte[] helperTraceByte = convertBooleanToByte(helperTraceBool);

        ourlog.write("After convertBooleanToByte() call, length of traceByte array: "+helperTraceByte.length);

        ourlog.write("pkt ByteBuffer capacity(): "+pkt.capacity());
        ourlog.write("pkt ByteBuffer position() before writing tracebytes: "+pkt.position());

        //copy the entire contents of trace byte array array to the pkt buffer

        // this method transfers bytes into this buffer from the given source array.
        // starting at the given offset in the array and at the current position of this buffer.
        // The position of this buffer is then incremented by length.
        // args
        // src - The array from which bytes are to be read
        // offset - The offset within the array of the first byte to be read; must be non-negative and no larger than array.length
        // length - The number of bytes to be read from the given array; must be non-negative and no larger than array.length - offset
        pkt.put(helperTraceByte, 0, helperTraceByte.length);

        ourlog.write("pkt ByteBuffer position() after writing tracebytes: "+pkt.position());

        // this method returns the byte array that backs this buffer
        byte[] buf = pkt.array(); // the byte array buf copied with the contents of the pkt buffer
        ourlog.write("Byte Array buf[]'s size : "+buf.length);


        try {
            //out.write(buf, headerOff, dataOff + dataLen - headerOff); // BlockSender out (in the default case)
            // we change this in BlockTraceSender to the below args

            // this method writes len bytes from the specified byte array starting at offset off to this output stream.
            //args
            // b - the data.
            // off - the start offset in the data.
            // len - the number of bytes to write.
            out.write(buf, headerOff, buf.length-headerOff);
            ourlog.write("Inside BlockTraceSender.. wrote final buf into OutputStream");

        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                /*
                 * writing to client timed out.  This happens if the client reads
                 * part of a block and then decides not to read the rest (but leaves
                 * the socket open).
                 *
                 * Reporting of this case is done in DataXceiver#run
                 */
            } else {
                /* Exception while writing to the client. Connection closure from
                 * the other end is mostly the case and we do not care much about
                 * it. But other things can go wrong, especially in transferTo(),
                 * which we do not want to ignore.
                 *
                 * The message parsing below should not be considered as a good
                 * coding example. NEVER do it to drive a program logic. NEVER.
                 * It was done here because the NIO throws an IOException for EPIPE.
                 */
                String ioem = e.getMessage();
                if (!ioem.startsWith("Broken pipe") && !ioem.startsWith("Connection reset")) {
                    LOG.error("BlockTraceSender.sendChunks() exception: ", e);
                    datanode.getBlockScanner().markSuspectBlock(
                            ris.getVolumeRef().getVolume().getStorageID(),
                            block);
                }
            }
            throw ioeToSocketException(e);
        }

        if (throttler != null) { // rebalancing so throttle
            throttler.throttle(packetLen);
        }

        return dataLen;
    }

    /**
     * Read checksum into given buffer
     * @param buf buffer to read the checksum into
     * @param checksumOffset offset at which to write the checksum into buf
     * @param checksumLen length of checksum to write
     * @throws IOException on error
     */
    private void readChecksum(byte[] buf, final int checksumOffset,
                              final int checksumLen) throws IOException {
        if (checksumSize <= 0 && ris.getChecksumIn() == null) {
            return;
        }
        try {
            ris.readChecksumFully(buf, checksumOffset, checksumLen);
        } catch (IOException e) {
            LOG.warn(" Could not read or failed to verify checksum for data"
                    + " at offset " + offset + " for block " + block, e);
            ris.closeChecksumStream();
            if (corruptChecksumOk) {
                if (checksumOffset < checksumLen) {
                    // Just fill the array with zeros.
                    Arrays.fill(buf, checksumOffset, checksumLen, (byte) 0);
                }
            } else {
                throw e;
            }
        }
    }

    /**
     * Compute checksum for chunks and verify the checksum that is read from
     * the metadata file is correct.
     *
     * @param buf buffer that has checksum and data
     * @param dataOffset position where data is written in the buf
     * @param datalen length of data
     * @param numChunks number of chunks corresponding to data
     * @param checksumOffset offset where checksum is written in the buf
     * @throws ChecksumException on failed checksum verification
     */
    public void verifyChecksum(final byte[] buf, final int dataOffset,
                               final int datalen, final int numChunks, final int checksumOffset)
            throws ChecksumException {
        int dOff = dataOffset;
        int cOff = checksumOffset;
        int dLeft = datalen;

        for (int i = 0; i < numChunks; i++) {
            checksum.reset();
            int dLen = Math.min(dLeft, chunkSize);
            checksum.update(buf, dOff, dLen);
            if (!checksum.compare(buf, cOff)) {
                long failedPos = offset + datalen - dLeft;
                StringBuilder replicaInfoString = new StringBuilder();
                if (replica != null) {
                    replicaInfoString.append(" for replica: " + replica.toString());
                }
                throw new ChecksumException("Checksum failed at " + failedPos
                        + replicaInfoString, failedPos);
            }
            dLeft -= dLen;
            dOff += dLen;
            cOff += checksumSize;
        }
    }

    /**
     * sendBlock() is used to read block and its metadata and stream the data to
     * either a client or to another datanode.
     *
     * @param out  stream to which the block is written to
     * @param baseStream optional. if non-null, <code>out</code> is assumed to
     *        be a wrapper over this stream. This enables optimizations for
     *        sending the data, e.g.
     *        {@link SocketOutputStream#transferToFully(FileChannel,
     *        long, int)}.
     * @param throttler for sending data.
     * @return total bytes read, including checksum data.
     */
    long sendBlock(DataOutputStream out, OutputStream baseStream,
                   DataTransferThrottler throttler) throws IOException {
        final TraceScope scope = datanode.getTracer().
                newScope("sendBlockTrace_" + block.getBlockId());
        try {
            return doSendBlock(out, baseStream, throttler);

        } finally {
            scope.close();
        }
    }

    private long doSendBlock(DataOutputStream out, OutputStream baseStream,
                             DataTransferThrottler throttler) throws IOException {
        if (out == null) {
            throw new IOException( "out stream is null" );
        }
        initialOffset = offset;
        long totalRead = 0;
        OutputStream streamForSendChunks = out;

        lastCacheDropOffset = initialOffset;

        if (isLongRead() && ris.getDataInFd() != null) {
            // Advise that this file descriptor will be accessed sequentially.
            ris.dropCacheBehindReads(block.getBlockName(), 0, 0,
                    POSIX_FADV_SEQUENTIAL);
        }

        // Trigger readahead of beginning of file if configured.
        manageOsCache();

        final long startTime = ClientTraceLog.isDebugEnabled() ? System.nanoTime() : 0;
        try {
            int maxChunksPerPacket;
            int pktBufSize = PacketHeader.PKT_MAX_HEADER_LEN;
            ourlog.write("Initial pktBufSize: "+pktBufSize);
          /*  boolean transferTo = transferToAllowed && !verifyChecksum
                    && baseStream instanceof SocketOutputStream
                    && ris.getDataIn() instanceof FileInputStream; */
           /* if (transferTo) {
                FileChannel fileChannel =
                        ((FileInputStream)ris.getDataIn()).getChannel();
                blockInPosition = fileChannel.position();
                streamForSendChunks = baseStream;
                maxChunksPerPacket = numberOfChunks(TRANSFERTO_BUFFER_SIZE);

                // Smaller packet size to only hold checksum when doing transferTo
                pktBufSize += checksumSize * maxChunksPerPacket;
            } else { */
            maxChunksPerPacket = Math.max(1,
                    numberOfChunks(IO_FILE_BUFFER_SIZE));
            ourlog.write("maxChunksPerPacket: "+ maxChunksPerPacket);
            // Packet size includes both checksum and data
            pktBufSize += (chunkSize + checksumSize) * maxChunksPerPacket;
            ourlog.write("Final pktBufSize: "+pktBufSize);
            //}

            ByteBuffer pktBuf = ByteBuffer.allocate(pktBufSize);
            ourlog.write("After allocation, pktBuf size: "+pktBuf.capacity());

            while (endOffset > offset && !Thread.currentThread().isInterrupted()) {
                manageOsCache();
                long len = sendPacket(pktBuf, maxChunksPerPacket, streamForSendChunks,
                        false, throttler);
                ourlog.write("Length of pkt read is: "+len);
                offset += len;
                totalRead += len + (numberOfChunks(len) * checksumSize);
                ourlog.write("Total data read is: "+totalRead);
                seqno++;
            }
            // If this thread was interrupted, then it did not send the full block.
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    // send an empty packet to mark the end of the block
                    sendPacket(pktBuf, maxChunksPerPacket, streamForSendChunks, false,
                            throttler);
                    out.flush();
                } catch (IOException e) { //socket error
                    throw ioeToSocketException(e);
                }

                sentEntireByteRange = true;
            }
        } finally {
            if ((clientTraceFmt != null) && ClientTraceLog.isDebugEnabled()) {
                final long endTime = System.nanoTime();
                ClientTraceLog.debug(String.format(clientTraceFmt, totalRead,
                        initialOffset, endTime - startTime));
            }
            close();
        }
        return totalRead;
    }

    /**
     * Manage the OS buffer cache by performing read-ahead
     * and drop-behind.
     */
    private void manageOsCache() throws IOException {
        // We can't manage the cache for this block if we don't have a file
        // descriptor to work with.
        if (ris.getDataInFd() == null) {
            return;
        }

        // Perform readahead if necessary
        if ((readaheadLength > 0) && (datanode.readaheadPool != null) &&
                (alwaysReadahead || isLongRead())) {
            curReadahead = datanode.readaheadPool.readaheadStream(
                    clientTraceFmt, ris.getDataInFd(), offset, readaheadLength,
                    Long.MAX_VALUE, curReadahead);
        }

        // Drop what we've just read from cache, since we aren't
        // likely to need it again
        if (dropCacheBehindAllReads ||
                (dropCacheBehindLargeReads && isLongRead())) {
            long nextCacheDropOffset = lastCacheDropOffset + CACHE_DROP_INTERVAL_BYTES;
            if (offset >= nextCacheDropOffset) {
                long dropLength = offset - lastCacheDropOffset;
                ris.dropCacheBehindReads(block.getBlockName(), lastCacheDropOffset,
                        dropLength, POSIX_FADV_DONTNEED);
                lastCacheDropOffset = offset;
            }
        }
    }

    /**
     * Returns true if we have done a long enough read for this block to qualify
     * for the DataNode-wide cache management defaults.  We avoid applying the
     * cache management defaults to smaller reads because the overhead would be
     * too high.
     *
     * Note that if the client explicitly asked for dropBehind, we will do it
     * even on short reads.
     *
     * This is also used to determine when to invoke
     * posix_fadvise(POSIX_FADV_SEQUENTIAL).
     */
    private boolean isLongRead() {
        return (endOffset - initialOffset) > LONG_READ_THRESHOLD_BYTES;
    }

    /**
     * Write packet header into {@code pkt},
     * return the length of the header written.
     */
    private int writePacketHeader(ByteBuffer pkt, int dataLen, int packetLen) {
        pkt.clear();
        // both syncBlock and syncPacket are false
        PacketHeader header = new PacketHeader(packetLen, offset, seqno,
                (dataLen == 0), dataLen, false);

        int size = header.getSerializedSize();
        pkt.position(PacketHeader.PKT_MAX_HEADER_LEN - size);
        header.putInBuffer(pkt);
        return size;
    }

    boolean didSendEntireByteRange() {
        return sentEntireByteRange;
    }

    /**
     * @return the checksum type that will be used with this block transfer.
     */
    DataChecksum getChecksum() {
        return checksum;
    }

    /**
     * @return the offset into the block file where the sender is currently
     * reading.
     */
    long getOffset() {
        return offset;
    }
}

