package edu.smu.data;

import java.util.ArrayList;
import java.util.Iterator;

public class SequenceList  {
	
	public SequenceList(Alphabet labelSet){
		this.labelSet = labelSet;
		
		dataSet = new ArrayList<Sequence>();
		iter = dataSet.iterator();
	}
	
	public SequenceList(Alphabet labelSet, ArrayList<Sequence> arr ){
		//System.out.println(arr.size());
		this.labelSet = labelSet;
		this.dataSet = new ArrayList<Sequence>(arr);
		iter = dataSet.iterator();
		//System.out.println("->" + dataSet.size());
		length = this.dataSet.size();
	}
	
	public void addSequence(Sequence seq){
		dataSet.add(seq);
		length = dataSet.size();
	}
	public int size() {
		return length;
	}
	
	public Sequence getSequence(int idx){
		assert(idx >= 0 && idx < length);
		return dataSet.get(idx);
	}
	
	public void getArrayList(ArrayList<Sequence> arrSeq){
		for(int i = 0; i < length; i++ ){
			arrSeq.add(dataSet.get(i));
		}
	}
	public void getInstanceList(ArrayList<Instance> arrSeq){
		for(int i = 0; i < length; i++ ){
			Sequence seq = dataSet.get(i);
			for(int j = 0; j < seq.size(); j++ ){
				arrSeq.add(seq.get(j));
			}
		}
	}
	/*public SequenceList deepClone () {
		ArrayList<Sequence> ret = new ArrayList<Sequence>( dataSet );
		return ret;
	}*/
	public Iterator<Sequence> iterator() {
		return iter;
	}
	public InstanceList[] splitByPreviousLabel(){
		//Iterator<Sequence> iterSequence = this.iterator();
		//while( iterSequence.hasNext() ){
			//Sequence seq = iterSequence.next();
			/*Iterator<Instance> iterInst = seq.iterator();
			int prev = -1;
			while( iterInst.hasNext() ){
				Instance inst = iterInst.next();
				if( prev != -1 ){
					instList[prev].add(inst);
				}
				prev = inst.getLabel();
			}*/
		instList = new InstanceList[labelSet.size()];
		for(int i = 0; i < labelSet.size(); i++){
			instList[i] = new InstanceList();
		}
		for(int s = 0; s < length; s++ ){
			Sequence seq = dataSet.get(s);
			//dataSet.r
			int prev = -1;
			for(int i = 0; i < seq.size(); i++ ){
				Instance inst = seq.get(i);
				if( prev != -1 ){
					//System.out.println("prev=" + prev);
					instList[prev].add(inst);
				}
				prev = inst.getLabel();
			}
		}
		//System.out.println( labelSet.size() );
		return instList;
	}
	
	public void display(){
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		//System.out.println(dataSet.size());
		for(int i = 0; i < dataSet.size(); i++ ){
			dataSet.get(i).display();
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	protected Iterator<Sequence> iter;
	//Storing the instance lists
	protected ArrayList<Sequence> dataSet;
	//The size of dataset
	protected int length;
	protected Alphabet labelSet;
	protected Alphabet featSet;
	private InstanceList[] instList;
}
