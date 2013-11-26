package edu.smu.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * A class manipulates the sequence 
 */
public class Sequence implements Iterable<Instance> , Comparable {
	
	public Sequence(){
		this.dataSet = new ArrayList<Instance>();
		length = dataSet.size();
		iter = dataSet.iterator();
	}
	
	public Sequence( ArrayList<Instance> dataSet){
		this.dataSet = new ArrayList<Instance>(dataSet);
		iter = dataSet.iterator();
	}
	
	public Instance getInstance(int idx){
		assert(idx >= 0 && idx < length);
		return dataSet.get(idx);
	}
	
	public void addInstance(Instance inst){
		dataSet.add(inst);
		length = dataSet.size();
	}
	public int size(){
		return length;// = dataSet.size();
	}
	public Iterator<Instance> iterator() {
		return iter;
	}

	public Instance get(int index){
		assert(index < length && index >= 0);
		return dataSet.get(index);
	}
	
	public InstanceList deepClone () {
		InstanceList ret = new InstanceList( dataSet );
		return ret;
	}
	
	public void display(){
		//System.out.println(dataSet.size());
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		for(int i = 0; i < length; i++ ){
			dataSet.get(i).display();
			if( i != length - 1) 
				System.out.println("=>");
		}
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}
	public int compareTo(Object o) {
		Sequence B = (Sequence)o;
		double bp = B.getProb();
		if( prob < bp )
			return 1;
		else if( prob > bp ){
			return -1;
		} else if( prob == bp ){
			return 0;
		}
		return 0;
	}
	public double getProb(){
		return prob;
	}
	public void setProb(double prob){
		this.prob = prob;
	}
	public boolean isLabelIn(int label){
		for(int i = 0; i < dataSet.size(); i++ ){
			Instance inst = dataSet.get(i);
			int id = inst.getPredictLabel();
			if( id == label ){
				return true;
			}
		}
		return false;
	}
	//
	private double prob = -1.0;
	//Iterable
	private Iterator<Instance> iter;
	//Storing the instance lists
	private ArrayList<Instance> dataSet;
	//The size of dataset
	private int length;
}
