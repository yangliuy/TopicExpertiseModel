package edu.smu.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import edu.smu.util.MatrixOps;
/**
 * A class manipulates the collection of instances
 */
public class InstanceList implements Iterable<Instance> {
	
	public InstanceList(){
		this.dataSet = new ArrayList<Instance>();
		length = dataSet.size();
		iter = dataSet.iterator();
	}
	
	public InstanceList( ArrayList<Instance> dataSet){
		this.dataSet = new ArrayList<Instance>(dataSet);
		length = dataSet.size();
		iter = dataSet.iterator();
	}
	public int size(){
		return length;
	}
	public Iterator<Instance> iterator() {
		return iter;
	}

	public Instance get(int index){
		assert(index < length && index >= 0);
		return dataSet.get(index);
	}
	
	public InstanceList[] split (double[] proportions) {
		return split (new java.util.Random(System.currentTimeMillis()), proportions);
	}
	
	public InstanceList deepClone () {
		InstanceList ret = new InstanceList( dataSet );
		return ret;
	}
	public void shuffle (java.util.Random r) {
		Collections.shuffle (dataSet, r);
	}
	/**
	 * Randomly permute the specified InstanceList using the specified source of randomness. And then split it into a array of InstanceList
	 * @param r
	 * @param proportions
	 * @return
	 */
	public InstanceList[] split (java.util.Random r, double[] proportions) {
		InstanceList shuffled = this.deepClone();
		shuffled.shuffle (r);
		return shuffled.splitInOrder(proportions);
	}
	/**
	 * 
	 * @param A array of proportions to divide the whole instance list
	 * @return A array of InstanceList
	 */
	public InstanceList[] splitInOrder (double[] proportions) {
		InstanceList[] ret = new InstanceList[proportions.length];
		double maxind[] = proportions.clone();
		MatrixOps.normalize(maxind);
		for (int i = 0; i < maxind.length; i++) {
			ret[i] = new InstanceList(); 
			if (i > 0) 
				maxind[i] += maxind[i-1];
		}
		for (int i = 0; i < maxind.length; i++) { 
			// Fill maxind[] with the highest instance index to go in each corresponding returned InstanceList
			maxind[i] = Math.rint (maxind[i] * this.size());
		}
		for (int i = 0, j = 0; i < size(); i++) {
			// This gives a slight bias toward putting an extra instance in the last InstanceList.
			while (i >= maxind[j] && j < ret.length) 
				j++;
			ret[j].add(dataSet.get(i));
		}
		return ret;
	}
	/**
	 * Add an instance to current list
	 * @param an instance to be added in the instance list 
	 */
	public void add(Instance instance) {
		assert(instance != null);
		dataSet.add(instance);
		length = dataSet.size();
	}
	public void display(){
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for(int i = 0; i < length; i++ ){
			dataSet.get(i).display();
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	//Iterable
	private Iterator<Instance> iter;
	//Storing the instance lists
	private ArrayList<Instance> dataSet;
	//The size of dataset
	private int length;
}
