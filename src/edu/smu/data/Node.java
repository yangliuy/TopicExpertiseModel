package edu.smu.data;
//To make the program run fast, we don't use this class right now!
import java.util.Vector;

import edu.smu.util.MatrixOps;

public class Node {
	public Node(int numLabels){
		this.numLabels = numLabels;
		prevProbs = new double[numLabels];
		curProbs = new double[numLabels];
		MatrixOps.setAll(prevProbs, 0);
		MatrixOps.setAll(curProbs, 0);
		bestLabel = -1;
	}
	public Node(int numLabels, double[] prevProbs){
		this.numLabels = numLabels;
		this.prevProbs = new double[numLabels];
		this.curProbs = new double[numLabels];
		MatrixOps.set(this.prevProbs, prevProbs);
		MatrixOps.setAll(curProbs, 0);
		bestLabel = -1;
	}
	public void getPrevProbs(double[] probs){
		MatrixOps.set(probs, prevProbs);
	}
	public void getCurProbs(double[] probs){
		MatrixOps.set(probs, curProbs);
	}
	public void setCurProbs(double[] probs){
		MatrixOps.set(curProbs, probs);
	}
	public int getBestLabel(){
		return bestLabel;
	}
	public void setLabel(int label){
		bestLabel = label;
	}
	public int getPrevNode(){
		return prevNode;
	}
	private int numLabels;
	private int prevNode;
	private int bestLabel;
	double[] prevProbs;
	double[] curProbs;
}
