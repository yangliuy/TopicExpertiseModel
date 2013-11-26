package edu.smu.data;

import java.util.*;
import java.io.*;

/**
 * An Instance object stores a sparse vector that represents an observation
 * together with a label for this observation. 
 */
public class Instance {

  public Instance(SparseVector featVec, int label) {
    this.featVec = featVec;
    this.label = label;
    id = "UNKNOWN";
    predictLabel = -1;
  }

  public Instance(SparseVector featVec, int label, String id) {
    this.featVec = featVec;
    this.label = label;
    this.id = id;
    predictLabel = -1;
  }

  public void setFeaVector( SparseVector featVec ){
	  this.featVec = featVec; 
  }
  
  public void setLabel(int label){
	  this.label = label;
  }
  
  
  public SparseVector getFeatureVector() {
    return featVec;
  }

  public int getLabel() {
    return label;
  }

  public int getPredictLabel(){
	return predictLabel;
  }
  
  public void setPredictLabel(int l){
	predictLabel = l;
  }
  
  public String getID() {
    return id;
  }

  public void display() {
	  System.out.println("--------------------------------------------");
	  System.out.println("Id=" + id );
	  System.out.println("Label="+ label );
	  featVec.display();
	  System.out.println("--------------------------------------------");
  }
  // The feature vector that represents this Instance object.
  protected SparseVector featVec;
  
  // The class label of this Instance object. The label ranges from 0 to (C-1) 
  // where C is the total number of classes. If label is set to -1, it means 
  // this Instance in unlabeled.
  protected int label;

  protected int predictLabel;
  // A String that can be used to identify this Instance if needed. E.g. if the
  // Instance object is a document, the id can be the document ID. It is not
  // necessary to set this id.
  protected String id;

}