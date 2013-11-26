package edu.smu.data;
//To make the program run fast, we don't use this class right now!
public class Lattice {
	public Lattice(int row, int col, int numLabels, Node[][] net, double[][] cost){
		this.row = row;
		this.col = col;
		this.numLabels = numLabels;
		this.net = net;
		this.cost = cost;
	}
	public double getCostOf(int x, int y){
		return cost[x][y];
	}
	public void setBestLabel(int x, int y, int l ){
		net[x][y].setLabel(l);
	}
	public int getPrevNodeOf(int x, int y){
		return net[x][y].getPrevNode();
	}
	public void readPrevProbsOf(int x, int y, double[] probs){
		net[x][y].getPrevProbs(probs);
	}
	public void setCurProbsOf(int x, int y, double[] probs){
		net[x][y].setCurProbs(probs);
	}
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
	public int getNumFeatures(){
		return numLabels;
	}
	private double[][] cost;
	private int row;
	private int col;
	private int numLabels;
	private Node[][] net;
}
