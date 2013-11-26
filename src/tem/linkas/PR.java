package tem.linkas;

/**Standard PageRank Algorithm (CIKM 12 PR for Expert finding)
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Jama.Matrix;  

import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.main.TEMModel;

public class PR {
	private static double LAMBDA = 0.2;
	private static double THRESHOLD = 0.0000001;
	
	private static int NODENUM; // Node number
	private static Matrix U; // Matrix with all 1
	private static Matrix graphAdjM; //Adjancy matrix of graph
	private static Matrix transM;//Transition probability matrix

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String minPostNum = "80";
		String modelName = "PR";
		int K = 15;
		String graphDataFile = PathConfig.originalDataPath + "USER" + minPostNum + "/userAnswerNumWeighted.QAgraph";
		//String graphDataFile = PathConfig.originalDataPath + "USER" + minPostNum + "/userVoteWeighted.QAgraph";
		//String graphDataFile = PathConfig.originalDataPath + "USER" + minPostNum + "/test.QAgraph";
		//String TransMFile = PathConfig.modelResPath + "USER" + minPostNum + "/" + modelName + ".transM";
		String finalPRALLFile = PathConfig.modelResPath + "USER" + minPostNum + "/" + modelName  +  ".finalPRAll";
		ArrayList<String> finalPRallLines = new ArrayList<String>();
		
		PrintWriter pw;
		readQAGraph(graphDataFile);
		NODENUM = graphAdjM.getRowDimension();
		System.out.println("NODENUM : " + NODENUM);
		//1. Init PR state vector and Matrix U
		//Both randomly initialise or set all 1 are OK
		Matrix PR0 = initPRStateVector();
		initU();
		System.out.println("Initial state vector PR0 is:");
		PR0.print(4, 4);
		
		
		for(int z = 0; z < K; z++){
			System.out.println("now topic = " + z);
			//String newPRFile = PathConfig.modelResPath + "USER" + minPostNum + "/" + modelName + ".T" + z + ".newPR";
			//String finalPRFile = PathConfig.modelResPath + "USER" + minPostNum + "/" + modelName  + ".T" + z +  ".finalPR";
			
			//2. Compute transition probability matrix
			computeTransM(z);
			//pw = new PrintWriter(new FileWriter(TransMFile));
			//transM.print(pw, 4, 4);
			
			//3. Compute newPR update matrix
			Matrix newPR = computeNewPR(z);
			//Normalized newPR matrix
			normal(newPR);
			//pw = new PrintWriter(new FileWriter(newPRFile));
			//newPR.print(pw, 4, 4);
			
			//4. Iteratively update PR state vector
			Matrix pageRank = calPageRank(PR0, newPR);
			
			//5. Print final PageRank score
			System.out.println("Final PageRank is :");
			pageRank.print(4, 4);
			
			String PRLine = "";
			for(int i = 0; i < NODENUM; i++){
				PRLine += pageRank.get(0, i) + "\t";
			}
			finalPRallLines.add(PRLine);
			//saveFinalPR(finalPRFile, pageRank);
		}
		FileUtil.writeLines(finalPRALLFile, finalPRallLines);
	}
	
	private static void normal(Matrix newPR) {
		// TODO Auto-generated method stub
		for(int i = 0; i < NODENUM; i++){
			double sum = 0;
			for(int j = 0; j < NODENUM; j++){
				sum += newPR.get(i, j);
			}
			if(sum != 0){
				for(int j = 0; j < NODENUM; j++){
					newPR.set(i, j, newPR.get(i, j) / sum);
				}
			}
		}
	}

	private static void saveFinalPR(String finalPRFile, Matrix pageRank) {
		// TODO Auto-generated method stub
		ArrayList<String> lines = new ArrayList<String>();
		for(int i = 0; i < pageRank.getRowDimension(); i++){
			String line = "";
			for(int j = 0; j < pageRank.getColumnDimension(); j++){
				line += pageRank.get(i, j) + "\t";
			}
			lines.add(line);
		}
		FileUtil.writeLines(finalPRFile, lines);
	}

	//Matrix with all 1
	private static void initU() {
		// TODO Auto-generated method stub
		double[][] u = new double[NODENUM][NODENUM];
		for(int i = 0; i < NODENUM; i++){
			for(int j = 0; j < NODENUM; j++){
				u[i][j] = 1;
			}
		}
		U = new Matrix(u);
	}

	//Compute transition matrix
	private static void computeTransM(int z) {
		// TODO Auto-generated method stub
		double[][] transm = new double[NODENUM][NODENUM];
		for(int i = 0; i < NODENUM; i++){
			double rowSum = 0;
			for(int j = 0; j < NODENUM; j++){
				rowSum += graphAdjM.get(i, j);
			}
			if(rowSum == 0){
				for(int j = 0; j < NODENUM; j++){
					transm [i][j] = 0;
				}
			} else {
				for(int j = 0; j < NODENUM; j++){
					double norWeight = graphAdjM.get(i, j) /
										rowSum;
					transm [i][j] = norWeight;
				}
			}
		}
		transM = new Matrix(transm);
	}

	private static double sim(float f, float g) {
		// TODO Auto-generated method stub
		return 1 - Math.abs(f - g);
	}

	//compute pagerank
	public static Matrix calPageRank(Matrix PR0, Matrix newPR) {
		Matrix PR;
		while (true) {
			PR = PR0.times(newPR);
			double dis = calDistance(PR, PR0);//PR0 store PR vector after last iteration
			System.out.println("distance:" + dis);
			if (dis <= THRESHOLD) {
				System.out.println("PR:");
				PR.print(4, 4);
				break;
			}
			PR0 = PR;
		}
		return PR;
	}
	
	private static Matrix initPRStateVector() {
		// TODO Auto-generated method stub
		double[] pr0M = new double[NODENUM];
		for(int i = 0; i < NODENUM; i++){
			pr0M[i] = 1;
		}
		return new Matrix(pr0M, 1);
	}

	private static void readQAGraph(
			String graphDataFile) {
		// TODO Auto-generated method stub
		ArrayList<String> graphLines = new ArrayList<String>();
		FileUtil.readLines(graphDataFile, graphLines);
		double[][] graphMatrix = new double[graphLines.size()][];
		double minNumber = 1000;
		for(int i = 0; i < graphLines.size(); i++){
			String[] glineTokens = graphLines.get(i).split("\t");
			graphMatrix[i] = new double[glineTokens.length];
			for(int j = 0; j < glineTokens.length; j++){
				double d = Double.valueOf(glineTokens[j]);
				if(d < 0){
					graphMatrix[i][j] = 0;
				} else {
					graphMatrix[i][j] = d;
				}
				if(minNumber > graphMatrix[i][j]) {
					minNumber = graphMatrix[i][j];
				}
			}
		}
		//If there is negative number is matrix, find the min one x. Then all number add |x|
		System.out.println("minNumber " + minNumber);
		/*if (minNumber < 0){
			for(int i = 0; i < graphMatrix.length; i++){
				for(int j = 0; j < graphMatrix[i].length; j++){
					graphMatrix[i][j] += (0 - minNumber);
				}
			}
		}*/
		graphAdjM = new Matrix(graphMatrix);
	}

	public static void printMatrix(List<List<Double>> m) {
		for (int i = 0; i < m.size(); i++) {
			for (int j = 0; j < m.get(i).size(); j++) {
				System.out.print(m.get(i).get(j) + "\t");
			}
			System.out.println();
		}
	}

	public static void printVec(List<Double> v) {
		for (int i = 0; i < v.size(); i++) {
			System.out.print(v.get(i) + "\t");
		}
		System.out.println();
	}

	/**
	 * Randomly Initialise state vector PR0
	 * 
	 * @param n
	 *            dimension of vector PR0
	 * @return A random vector, each dimension is 0-5
	 */
	public static List<Double> randomInitPR0(int n) {
		Random random = new Random();
		List<Double> q = new ArrayList<Double>();
		for (int i = 0; i < n; i++) {
			q.add(new Double(5 * random.nextDouble()));
		}
		return q;
	}

	/**
	 * Compute Euclidean Distance
	 * 
	 * @param q1
	 *           
	 * @param q2
	 *           
	 * @return distance
	 */
	public static double calDistance(Matrix q1, Matrix q2) {
		double sum = 0;

		if (q1.getColumnDimension() != q2.getColumnDimension() ) {
			return -1;
		}

		for (int i = 0; i < q1.getColumnDimension() ; i++) {
			sum += Math.pow(q1.get(0, i) - q2.get(0, i),
					2);
		}
		return Math.sqrt(sum);
	}

	/**
	 * compute NEWPR matrix
	 *            
	 * @return NEWPR matrix
	 */
	public static Matrix computeNewPR(int z) {
		Matrix add1 = transM.times(LAMBDA);
		//In new PR matrix, the larger values are in the c-th column, the larger score node c tends to get.
		/*double [][] newU = new double[NODENUM][NODENUM];
		for(int i = 0; i < NODENUM; i++){
			double userPreference = model.theta[i][z];
			double tspr = userPreference;
			for(int k = 0; k < NODENUM; k++){
				newU[k][i] = tspr;
			}
		}
		U = new Matrix(newU);*/
		
		Matrix add2 = U.times( (1 - LAMBDA) / NODENUM);
		Matrix newPR = add1.plus(add2);
		return newPR;
	}
}
