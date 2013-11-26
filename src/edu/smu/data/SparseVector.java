package edu.smu.data;

import java.util.*;
import java.io.*;

/**
 * A SparseVector object represents a sparse vector. It stores the indices of
 * the features that have non-zero values and their corresponding feature
 * values.
 */

// To be completed!

public class SparseVector {  

  // The constructor can be changed to take in different types of parameters.
	/**
	 * Note that right now SparseVector doesn't support add or remove operations which will be time-consuming in current position. 
	 */
  public SparseVector(int[] indices, double[] values) {
	  assert( indices.length == values.length );
	  
	  capacity = indices.length + 1;
	  this.indices = new int[capacity];
	  this.values = new double[capacity];
	  //id2Pos = new HashMap<Integer,Integer>(); 
	  
	  int cnt = 0;
	  for(int i = 0; i < indices.length; i++ ){
		  if( values[i] != 0.0 ){ //&& !id2Pos.containsKey(indices[i])){
			  this.indices[cnt] = indices[i];
			  this.values[cnt] = values[i];
			  //id2Pos.put(indices[i], cnt);
			  cnt++;
		  }
	  }
	  length = cnt;
  }

  /**
   * Returns the number of entries (non-zero features) stored in this 
   * SparseVector object.
   * @return The number of entries in this SparseVector.
   */
  public int numEntries() {
    return length;
  }

  /**
   * Returns the index of the i'th feature stored in this SparseVector. For
   * example, suppose a SparseVector has the following feature indices and 
   * feature values:
   * <p>
   *   2 1.5
   *   5 0.5
   *   9 1.0
   * <p>
   * Then calling getFeatureIndexAt(0) returns 2 and calling 
   * getFeatureIndexAt(2) returns 9.
   * @param i The location of the entry from which a feature index is to be 
   *          returned.
   * @return The feature index stored in the specified entry.
   */
  public int getFeatureIndexAt(int i) {
	assert(i >= 0 && i < length);
    return indices[i];
  }
  /**
   * Returns the value of the i'th feature stored in this SparseVector. For
   * example, suppose a SparseVector has the following feature indices and 
   * feature values:
   * <p>
   *   2 1.5
   *   5 0.5
   *   9 1.0
   * <p>
   * Then calling getFeatureValueAt(0) returns 1.5 and calling 
   * getFeatureValueAt(2) returns 1.0.
   * @param i The location of the entry from which a feature value is to be 
   *          returned.
   * @return The feature value stored in the specified entry.
   */
  public double getFeatureValueAt(int i) {
	assert(i >= 0 && i < length);
    return values[i];
  }

  /**
   * Different from getFeatureValueAt, this function supporting locate entry by feature id
   * @param feature id
   * @return corresponding feature position in this SparseVector, if not return -1
   */
  /*public int getFeaturePositionOf(int ind){
	  if(!id2Pos.containsKey(ind)){
		  return -1;
	  }
	  return id2Pos.get(ind);
  }*/
  
  /**
   * This function supporting locate entry by feature id, then return its value
   * @param feature id
   * @return corresponding feature value or Double.MAX_VALUE
   */
  /*public double getFeatureValueOf(int ind){
	  if(!id2Pos.containsKey(ind)){
		  return Double.MAX_VALUE;
	  }
	  return values[id2Pos.get(ind)];
  }*/
  
  public int size(){
	return length;
  }
  /**
   * Just output the elements into screen
   */
  public void display(){
	  for(int i = 0; i < length; i++ ){
		  System.out.print( "(" + new Integer(indices[i]) + "," + new Double(values[i]) + ")" );
		  if( i != length-1 )
			  System.out.print(" , ");
		  else System.out.println();
	  }
  }
  // The following attributes are possible ways to implement this class but
  // other data structures are also possible.

  private int[] indices;  // The indices of features that have non-zero values.
                          // If there are no features with none-zero values,
                          // then "indices" is set to null.
  private double[] values;  // The values corresponding to the features 
                            // specified in "indices" or null if all features
                            // are binary.
  private int length;
  private int capacity;
  //private HashMap<Integer,Integer> id2Pos;
}