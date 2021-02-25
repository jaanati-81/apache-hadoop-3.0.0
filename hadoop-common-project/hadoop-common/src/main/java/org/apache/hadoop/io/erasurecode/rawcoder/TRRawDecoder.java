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
//import org.apache.hadoop.io.erasurecode.rawcoder.RecoveryTable96;
//import org.apache.hadoop.io.erasurecode.rawcoder.DualBasisTable96;

//import java.io.IOException;
import java.nio.ByteBuffer;
//import java.time.chrono.Era;
import java.util.*;

/**
 * A raw decoder in Trace Repair code scheme in pure Java.
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
    ByteBuffer output = decodingState.outputs[0];
    int erasedIdx = decodingState.erasedIndexes[0];
    //ourlog.write("\n ByteBuffer decode");

    // column trace computation for the trace repair process
    prepareColumnTraces(decodingState.inputs, erasedIdx);

    //compute 't' target traces from the column traces
    computeTargetTraces();

    //Now, recover the lost data and write to output buffer

    //initialize recovered value as a 0 byte
    byte recoveredValue = (byte)0;

    //get the appropriate row element from the dual basis table
    Object element = dBTable.getElement(erasedIdx);
    String st = element.toString();
    String[] elements = st.split(",");
    Integer[] dualBasisInt = new Integer[t];

    //convert the elements into bytes
    byte[] dualBasisByte = new byte[t];
    for(int m=0;m<elements.length; m++){
      String dualBasisString = elements[m].toString();
      dualBasisInt[m] = Integer.parseInt(dualBasisString.trim());
      dualBasisByte[m] = dualBasisInt[m].byteValue();
    }


    for (int i = 0; i < decodingState.inputs.length; i++) {
      if (i == erasedIdx) {
        continue;
      }

      byte dualBByte = dualBasisByte[i];
      if(targetTraces[i] == true) {
        recoveredValue ^= dualBByte;
      }

      output.put(recoveredValue);
      /*for (iIdx = decodingState.inputs[i].position(), oIdx = output.position();
           iIdx < decodingState.inputs[i].limit();
           iIdx++, oIdx++) {
       // ourlog.write("\n Decode symbols for input block = "+i+" is: "+decodingState.inputs[i].get(iIdx));
        output.put(oIdx, (byte) (output.get(oIdx) ^
                decodingState.inputs[i].get(iIdx)));
      }*/
    }


      // Skip the erased location.
      //method prepareColTraces() called from doDecode()
      //1. Read the element R[i,erasedIndex] (returns a row of 9 elements)
      //2. Represent the elements (2 to 9) as t-bit binary numbers based on first element(t) in the row (bandwidth)
      //3. Bitwise AND the trace stored at node input[i] and the t-bit representations
      //4. XOR all the bits
      //5. column traces of dimension[(m*8)*(n-1)] where m is the number of traces
      //within each i, we need to have another loop that specifies the traces from each input[i]
      //method computeTargetTraces() (same logic as in the standalone version)
      //method getRecoveredByte() (same logic as in the standalone version)

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
   * Compute column traces for trace repair
   * @param inputs input buffers of the helper nodes to read the data from
   * @param erasedIdx indexes of erased unit in the inputs array
    */
  private void prepareColumnTraces(ByteBuffer[] inputs, int erasedIdx) {

    for (int i=0; i < inputs.length; i++) {
      int k=0;
      if (i == erasedIdx) {
        continue;
      }
      Object element=recTable.getElement(i, erasedIdx);
      String st=element.toString();
      String[] elements=st.split(",");
      int traceBandwidth=Integer.parseInt(elements[0]);
      ArrayList<Boolean> ar=new ArrayList<>();

      for (int s=1; s <= t; s++) {
        String repairString=elements[s].toString();
        Integer repairInt=Integer.parseInt(repairString.trim());
        boolean bin[]=binaryRep(traceBandwidth, repairInt);

        //Store the binary rep as a Vector
        Vector<Boolean> binVec=new Vector<Boolean>(bin.length);
        for (int m=0; m < bin.length; m++) {
          System.out.println(bin[m]);
          binVec.add(bin[m]);
        }

        //Get helper trace elements from helper i's inputs byte buffer into a byte array
        byte[] helperTraceArrray=inputs[i].array();
        //Create a BitSet containing all the bits in the given byte array.
        BitSet helperTraceBits=BitSet.valueOf(helperTraceArrray);

        //Now iterate through all bits, processing trace bit sets of size traceBandwidth at a time
        for (int traceIndex=0; traceIndex < helperTraceArrray.length; traceIndex=traceIndex + traceBandwidth) {
          //Returns a new BitSet composed of bits from helperTraceBits from fromIndex (inclusive) to toIndex (exclusive).
          BitSet helperTrace=helperTraceBits.get(traceIndex, traceIndex + traceBandwidth);

          //Store the trace bits into an ArrayList
          ArrayList<Boolean> helperTraceArray=new ArrayList<>();
          for (int h=0; h < helperTrace.length(); h++)
            helperTraceArray.add(helperTrace.get(h));
          //Get helper trace elements computed earlier into a Vector
          Vector<Boolean> helperTraceVector=new Vector<Boolean>(helperTraceArray);

          //Boolean array to store the bit-wise & of binRep and helperTrace
          boolean[] res=new boolean[traceBandwidth];

          //Computing column traces ...
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
          //Store this as the column trace of node k
          columnTraces.put(k, ar);
        }

      }
      k++;
    }
  }



  /**
   * Compute t target traces from the column traces
   * */
  private void computeTargetTraces() {

    for(int s=0; s<t; s++){
      boolean RHS = false;
      for(int j=0; j<columnTraces.size(); j++) {

        boolean colTraceBool=columnTraces.get(j).get(s);
        RHS ^= colTraceBool;
       }
      targetTraces[s] = RHS;
    }
  }




  @Override
  protected void doDecode(ByteArrayDecodingState decodingState) {
    byte[] output = decodingState.outputs[0];
    int dataLen = decodingState.decodeLength;
    CoderUtil.resetOutputBuffers(decodingState.outputs,
            decodingState.outputOffsets, dataLen);
    int erasedIdx = decodingState.erasedIndexes[0];
   ourlog.write("\n ByteArray decode");
    // Process the inputs.
    int iIdx, oIdx;
    for (int i = 0; i < decodingState.inputs.length; i++) {
      // Skip the erased location.
      if (i == erasedIdx) {
        continue;
      }

      for (iIdx = decodingState.inputOffsets[i],
                   oIdx = decodingState.outputOffsets[0];
           iIdx < decodingState.inputOffsets[i] + dataLen; iIdx++, oIdx++) {
       ourlog.write("\n Decode symbols for input block = "+i+" is: "+decodingState.inputs[i][iIdx]);
        output[oIdx] ^= decodingState.inputs[i][iIdx];
      }
    }
  }

}
