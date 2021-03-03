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
    ByteBuffer output=decodingState.outputs[0];
    int erasedIdx=decodingState.erasedIndexes[0];


    // column trace computation for the trace repair process
    prepareColumnTraces(decodingState.inputs, erasedIdx);


    // Compute 't' target traces from the column traces
    // And recover the lost data and write to output buffer
    computeTargetTracesAndRecoverBytes(erasedIdx, output);


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


      /*for (iIdx = decodingState.inputs[i].position(), oIdx = output.position();
           iIdx < decodingState.inputs[i].limit();
           iIdx++, oIdx++) {
       // ourlog.write("\n Decode symbols for input block = "+i+" is: "+decodingState.inputs[i].get(iIdx));
        output.put(oIdx, (byte) (output.get(oIdx) ^
                decodingState.inputs[i].get(iIdx)));
      }*/

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
   * Perform trace repair from the traces received and recover the lost block into outputs
   * @param inputs input buffers of the helper nodes to read the data from
   * @param erasedIdx indexes of erased unit in the inputs array
   */
  protected void prepareColumnTraces(ByteBuffer[] inputs, int erasedIdx) {


     int k=0;
     for (int i=0; i < inputs.length; i++) { //iterate through all helpers

       if (i == erasedIdx) {
         continue;
       }
       Object element=recTable.getElement(i, erasedIdx);
       String st=element.toString();
       String[] elements=st.split(",");
       int traceBandwidth=Integer.parseInt(elements[0]);


       //Get helper trace elements from helper i's inputs byte buffer into a byte array
       byte[] helperTraceByteArray=inputs[i].array();



       int originalBytesInInput = inputs[i].array().length; //FIX after checking with Rakesh and Uma

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
  private void computeTargetTracesAndRecoverBytes(int erasedIdx, ByteBuffer output) {

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


    //check tr limit with Hoang, discuss with Rakesh and Uma too
    for(int tr=0, oIdx = output.position(); tr<columnTraces.size(); tr=tr+t, oIdx++) {
          for (int s=0; s < t; s++) {
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
