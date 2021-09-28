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
package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.util.OurECLogger;
import org.apache.hadoop.io.erasurecode.coder.util.tracerepair.DualBasisTable96;
import org.apache.hadoop.io.erasurecode.coder.util.tracerepair.RecoveryTable96;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * A raw decoder of the Trace Repair code scheme in pure Java.
 *
 */

@InterfaceAudience.Private
public class TRRawDecoder extends RawErasureDecoder {
    private static OurECLogger ourlog = OurECLogger.getLogger(TRRawDecoder.class);
    public TRRawDecoder(ErasureCoderOptions coderOptions) {
        super(coderOptions);
    }

    /** to access entries of the Recovery table */
    private final RecoveryTable96 recTable = new RecoveryTable96();

    /** to access entries of the Dual basis table */
    private final DualBasisTable96 dBTable = new DualBasisTable96();

    /** length */
    int t = 8; //same as l

    /** to store column traces from the helper nodes */
    HashMap<Integer, ArrayList<Boolean>> columnTraces = new HashMap<>();

    /** boolean array to store the 't' target traces */
    boolean[] targetTraces = new boolean[t];

    @Override
    protected void doDecode(ByteBufferDecodingState decodingState) {
        CoderUtil.resetOutputBuffers(decodingState.outputs,
                decodingState.decodeLength);
        ByteBuffer output=decodingState.outputs[0];
        ourlog.write("In ByteBufferDecodingState ....decodingState.outputs[0] length: "+output.capacity());
        int erasedIdx=decodingState.erasedIndexes[0];

        // ourlog.write("Helpers: "+(decodingState.inputs.length-1));
        // ourlog.write("Erased Index: "+erasedIdx);
        // column trace computation for the trace repair process
        prepareColumnTracesByteBuffer(decodingState.inputs, erasedIdx);


        // Compute 't' target traces from the column traces
        // And recover the lost data and write to output buffer
        computeTargetTracesAndRecoverByteBuffer(decodingState.inputs, erasedIdx, output);


    }



    @Override
    protected void doDecode(ByteArrayDecodingState decodingState) {
        byte[] output = decodingState.outputs[0];
        ourlog.write("In ByteArrayDecodingState .... decodingState.outputs[0] length: "+output.length);

        ByteBuffer bufOutput = null;
        bufOutput.put(output, 0, output.length);

        int dataLen = decodingState.decodeLength;
        CoderUtil.resetOutputBuffers(decodingState.outputs,
                decodingState.outputOffsets, dataLen);
        int erasedIdx = decodingState.erasedIndexes[0];


        ByteBufferDecodingState bbDecodingState = decodingState.convertToByteBufferState();



        // column trace computation for the trace repair process
        prepareColumnTracesByteBuffer(bbDecodingState.inputs, erasedIdx);


        // Compute 't' target traces from the column traces
        // And recover the lost data and write to output buffer
        computeTargetTracesAndRecoverByteBuffer(bbDecodingState.inputs, erasedIdx, bufOutput);

    }



    //Function to calculate the log base 2 of a non-negative integer
    public int log2(int N)
    {

        // calculate log2 N indirectly using log() method
        int result = (int)(Math.log(N) / Math.log(2));

        return result;
    }

    /**
     * Compute the t-bit binary representation of the non-negative integer m
     * @param t the number of bits required in the representation
     * @param m the non-negative integer for which we find the t-bit representation
     * @return boolean array of the t-bits computed
     */

    public boolean[] binaryRep(int t, int m){
        /*if((m < 0) || (m > Math.pow(q, t-1)))
            System.out.println("Number not in range [0..(q^t-1)]"); */
        boolean[] bin = new boolean[t];
        Arrays.fill(bin, Boolean.FALSE);

      /*  for(int i = 0; i < bin.length; i++) {
            System.out.println(bin[i]);
        } */

        while (m > 1) {
            int log = log2(m);
            int pos = (t - log)-1;
            if(pos < 0)
                pos = 0;
            bin[pos] = true;
            m = (int) (m - Math.pow(2, log));
        }
        if (m == 1)
            bin[t-1] = true;

       /* System.out.println("After binaryRep, the binary array is");
        for(int i = 0; i < bin.length; i++) {
            System.out.println(bin[i]);
        } */
        return bin;

    }

    /**
     * Convert a byte array into a boolean array
     *  @param bits a byte array of boolean values
     *  @param significantBits the number of total bits in the byte array, and
     *  therefore the length of the returned boolean array
     *  @return a boolean[] containing the same boolean values as the byte[] array
     *  adapted from https://sakai.rutgers.edu/wiki/site/e07619c5-a492-4ebe-8771-179dfe450ae4/bit-to-boolean%20conversion.html
     */
    public static boolean[] convertByteToBoolean(byte[] bits, int significantBits) {
        boolean[] retVal = new boolean[significantBits];
        int boolIndex = 0;
        for (int byteIndex = 0; byteIndex < bits.length; ++byteIndex) {
            for (int bitIndex = 7; bitIndex >= 0; --bitIndex) {
                if (boolIndex >= significantBits) {
                    return retVal;
                }

                retVal[boolIndex++] = (bits[byteIndex] >> bitIndex & 0x01) == 1 ? true
                        : false;
            }
        }
        return retVal;
    }


    /**
     * Perform trace repair from the ByteBuffer traces received and recover the lost block into outputs
     * @param inputs input buffers of the helper nodes to read the data from
     * @param erasedIdx indexes of erased unit in the inputs array
     */
    protected void prepareColumnTracesByteBuffer(ByteBuffer[] inputs, int erasedIdx) {


        int k=0;
        for (int i=0; i < inputs.length; i++) { //iterate through all helpers

            if (i == erasedIdx) {
                continue;
            }

            ourlog.write("Inside prepareColumnTracesByteBuffer... Value of i: "+i);
            ourlog.write("Erased Index :"+ erasedIdx);
            Object element=recTable.getElement(i, erasedIdx);

            String st=element.toString();
            String[] elements=st.split(",");
            int traceBandwidth=Integer.parseInt(elements[0]);


            //Get helper trace elements from helper i's inputs byte buffer into a byte array
            ByteBuffer helperTraceByteBuffer = inputs[i];
            ourlog.write("Size of input ByteBuffer from the helper is"+helperTraceByteBuffer.capacity());

            helperTraceByteBuffer.flip();  // Sets limit to current write position.
            int n = helperTraceByteBuffer.limit();
            ourlog.write("Buffer size at helper "+i+" is: "+ n);


            helperTraceByteBuffer.rewind(); // Already done by flip I think.
            byte[] helperTraceByteArray = new byte[n];
            helperTraceByteBuffer.get(helperTraceByteArray);


            // [SAFE ASSUMPTIONS] : 5120 is the number of data bytes in a packet (excluding header, TR ignores checksum)
            int originalBytesInInput = 5120;

            //Create a boolean array containing all the trace bits in the input byte array by calling convertByteToBoolean().
            //The 2nd argument specifies the the total number of trace bits (#bytes in the buffer * traceBandwidth) in this input buffer
            boolean[] helperTraceBooleanArray=convertByteToBoolean(helperTraceByteArray, originalBytesInInput*traceBandwidth);


            //boolean arraylist to store the column traces from this helper node
            ArrayList<Boolean> ar=new ArrayList<>();

            //We need to process only traceBandwidth bits at a time from helper trace data
            for (int traceBitsIndex=0; traceBitsIndex < helperTraceBooleanArray.length; traceBitsIndex=traceBitsIndex + traceBandwidth) {
                //Returns a new boolean array composed of bits from helperTraceBooleanArray from fromIndex (inclusive) to toIndex (exclusive).
                boolean[] helperTraceBits=Arrays.copyOfRange(helperTraceBooleanArray, traceBitsIndex, traceBitsIndex + traceBandwidth);

                //Store the trace bits into an ArrayList
                ArrayList<Boolean> helperTraceArray=new ArrayList<>();
                for (int h=0; h < helperTraceBits.length; h++)
                    helperTraceArray.add(helperTraceBits[h]);

                //Get helper trace elements computed into a Vector
                Vector<Boolean> helperTraceVector=new Vector<Boolean>(helperTraceArray);

                for (int s=1; s <= t; s++) {
                    String repairString=elements[s].toString();
                    Integer repairInt=Integer.parseInt(repairString.trim());
                    boolean bin[]=binaryRep(traceBandwidth, repairInt);


                    //Store the binary rep as a Vector
                    Vector<Boolean> binVec=new Vector<Boolean>(bin.length);
                    for (int m=0; m < bin.length; m++) {
                        // System.out.println(bin[m]);
                        binVec.add(bin[m]);
                    }


                    //Boolean array to store the bit-wise & of binRep and helperTrace
                    boolean[] res=new boolean[traceBandwidth];

                    //Computing column traces from this set of trace bits
                    for (int l=0; l < traceBandwidth; l++) {
                        boolean a=Boolean.TRUE.equals(binVec.get(l));
                        boolean b=Boolean.TRUE.equals(helperTraceVector.get(l));
                        res[l]=a & b;

                    }

                    //boolean to compute the XOR of all bits of res
                    boolean output=false;
                    for (int l=0; l < res.length; l++) {
                        output^=res[l];
                    }
                    //ArrayList to store the output bit
                    ar.add(output);
                }

            }
            //Store this as the column trace of helper node k
            columnTraces.put(k, ar);
            k++;
        }

    }




    /**
     * Compute t target traces from the column traces
     * @param erasedIdx indexes of erased unit in the inputs array
     * @param output the buffer to store the recovered bytes of the lost block
     * */
    private void computeTargetTracesAndRecoverByteBuffer(ByteBuffer[] inputs, int erasedIdx, ByteBuffer output) {

        //Retrieve the dual basis element and keep it converted as byte
        Object dBTableElement = dBTable.getElement(erasedIdx);
        String st = dBTableElement.toString();
        //System.out.println("Dual basis elements are: "+st);
        String[] dBTableElements = st.split(",");

        Integer[] dualBasisInt = new Integer[t];
        byte[] dualBasisByte = new byte[t];

        for(int m=0;m<dBTableElements.length; m++){
            String dualBasisString = dBTableElements[m].toString();
            dualBasisInt[m] = Integer.parseInt(dualBasisString.trim());
            dualBasisByte[m] = dualBasisInt[m].byteValue();
        }


        for(int tr=0, oIdx = output.position(); tr<columnTraces.size(); tr=tr+t, oIdx++) {
            for (int s=0; s < inputs.length-1; s++) {
                for (int j=tr; j < tr + t; j++) {
                    boolean RHS=false;
                    boolean colTraceBool=columnTraces.get(j).get(s);
                    RHS^=colTraceBool;
                    targetTraces[s]=RHS;

                }
            }

            //Now use this set of target traces to compute the byte of lost block
            byte recoveredValue=(byte) 0;

            for (int s=0; s < t; s++) {

                byte dualBByte=dualBasisByte[s]; //take the sth byte from dual basis array
                if (targetTraces[s]) {
                    recoveredValue^=dualBByte;
                }

            }
            output.put(oIdx, recoveredValue);
        }


    }

}
