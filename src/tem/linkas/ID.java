package tem.linkas;

/**InDegree Algorithm for expert finding (CIKM 12 & KDD08)
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import Jama.Matrix;  

import tem.com.FileUtil;
import tem.com.MatrixUtil;
import tem.conf.PathConfig;
import tem.main.TEMModel;

public class ID {

	private static int NODENUM; // Node number
	private static Matrix U; // Matrix with all 1
	private static Matrix graphAdjM; //Adjancy matrix of graph
	private static Matrix transM;//Transition probability matrix

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String minPostNum = "80";
		String modelName = "ID";
		int K = 15;
		//Meature user interests and expertise by in degree of user node
		//i.e. The total number of answers the user provides or total number of votes the user gets
		//Compute the sum of each column 
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
		
		double[] nodeScoreArray = new double[NODENUM];
		for(int i = 0; i < NODENUM; i++){
			for(int j = 0; j < NODENUM; j++){
				nodeScoreArray[i] += graphAdjM.get(j, i);
			}
		}
		MatrixUtil.norm1(nodeScoreArray);
		
		for(int z = 0; z < K; z++){
			System.out.println("now topic = " + z);
			
			Matrix pageRank = new Matrix(nodeScoreArray, 1);
		
			System.out.println("Final PageRank is :");
			pageRank.print(4, 4);
			
			String PRLine = "";
			for(int i = 0; i < NODENUM; i++){
				PRLine += pageRank.get(0, i) + "\t";
			}
			finalPRallLines.add(PRLine);
		}
		FileUtil.writeLines(finalPRALLFile, finalPRallLines);
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
}
