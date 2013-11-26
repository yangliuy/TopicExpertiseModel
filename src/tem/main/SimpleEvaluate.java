package tem.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.main.Documents.Document;

/**Simple evaluation for TEM result 
 * Compute utopics and kuExpertiseScore file
 * kuExpertiseScore Matrix is also used in 
 * Topic Expertise PageRank
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */
public class SimpleEvaluate {

	static int K = 15;
	static int E = 4;
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		String minPostNum = "80";
		String trainDocfile = PathConfig.modelResPath + "USER" + minPostNum + "/USER" + minPostNum + ".data";
		Documents trainDocSet = new Documents();
		trainDocSet = FileUtil.loadClass(trainDocSet, trainDocfile);
		System.out.println("train terms: " + trainDocSet.termCountMap.size());
		
		String testDataFolder = PathConfig.testDataPath;
		Documents testDocSet = new Documents();
		
		
		testDocSet.readQATestDocs(testDataFolder, trainDocSet);
		String testDocfile = testDataFolder + "QATest.data";
		FileUtil.saveClass(testDocSet, testDocfile);
		
		//Document questionDoc = testDocSet.docs.get(0);
	
		System.out.println(testDocSet.termCountMap.size());
		System.out.println(testDocSet.tagCountMap.size());
		System.out.println(testDocSet.voteCountMap.size());
		System.out.println(testDocSet.docs.size());
		
		//trainDocSet.copyTrainDocVocals(testDocSet);
		//FileUtil.saveClass(trainDocSet, trainDocfile);
		
		/*String userIDFile = PathConfig.scriptDataPath + "USERID" + minPostNum;
		String resPath = PathConfig.modelResPath + "USER" + minPostNum + "/model_" + 400; 
		String resultPath = PathConfig.modelResPath + "USER" + minPostNum + "/";
		ArrayList<String> userIDs = new ArrayList<String>();
		FileUtil.readLines(userIDFile, userIDs);
		Documents docSet = new Documents();
		String docfile = resultPath + "USER" + minPostNum + ".data";
		docSet = FileUtil.loadClass(docSet, docfile);
		int U = userIDs.size();
		float [][] theta = new float[U][K];
		float [][][] phi = new float[K][U][E];
		float [][] tau = new float[E][docSet.voteToIndexMap.size()];
		float [] expertiseMean = new float[E];
		readTheta(theta, resPath + ".theta");
		readPhi(phi, resPath + ".phi");
		readTau(tau, resPath + ".tau");
		SimpleEvaluate se = new SimpleEvaluate();
		se.printUtopics(theta, U,  resPath, userIDs);
		se.computeExpertiseMean(tau, docSet, expertiseMean);
		se.printKUExpertiseScore(phi, expertiseMean, U, resPath);*/
	}
	
	private void computeExpertiseMean(float[][] tau, Documents docSet,
			float[] expertiseMean) {
		// TODO Auto-generated method stub
		for(int i = 0; i < E; i++){
			float mean = 0;
			for(int j = 0; j < docSet.indexToVoteMap.size(); j++){
				mean += Float.valueOf(docSet.indexToVoteMap.get(j)) * tau[i][j];
			}
			expertiseMean[i] = mean;
			System.out.println("expertise " + i + " mean : " + mean);
		}
	}
	
	private void printKUExpertiseScore(float[][][] phi, float[] expertiseMean, int U, String resPath) {
		// TODO Auto-generated method stub
		ArrayList<String> KUEMeanLines = new ArrayList<String>();
		for(int k = 0; k < K; k++){
			String line = "";
			for(int u = 0; u < U; u++){
				float expertiseScore = 0;
				for(int e = 0; e < E; e++){
					expertiseScore += expertiseMean[e] * phi[k][u][e];
				}
				line += expertiseScore + "\t";
			}
			KUEMeanLines.add(line);
		}
		FileUtil.writeLines(resPath + ".KUexpertiseScore", KUEMeanLines);
	}

	private void printUtopics(float[][] theta, int U, String resPath, ArrayList<String> userIDs) {
		// TODO Auto-generated method stub
		//model.utopics
		ArrayList<String> utopicsLines = new ArrayList<String>();
		for(int i = 0; i < U; i++){
			List<Integer> tWordsIndexArray = new ArrayList<Integer>(); 
			for(int t = 0; t < K; t++){
				tWordsIndexArray.add(new Integer(t));
			}
			Collections.sort(tWordsIndexArray, new SimpleEvaluate.TwordsComparable(theta[i])); 
			String line = "UserID = " + userIDs.get(i) + "\t";
			for(int t = 0; t < K; t++){
				line += tWordsIndexArray.get(t) + "\t";
			}
			utopicsLines.add(line);
		}
		FileUtil.writeLines(resPath + ".utopics", utopicsLines);
	}

	private static void readTau(float[][] tau, String file) {
		// TODO Auto-generated method stub
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(file, lines);
		for(int i = 0; i < tau.length; i++){
			String[] tokens = lines.get(i).split("\t");
			for(int j = 0 ; j < tau[i].length; j++){
				tau[i][j] = Float.valueOf(tokens[j]);
			}
		}
	}

	private static void readPhi(float[][][] phi, String file) {
		// TODO Auto-generated method stub
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(file, lines);
		for(String line : lines){
			String[] tokens = line.split("\t");
			int i = Integer.valueOf(tokens[0]);
			int j = Integer.valueOf(tokens[1]);
			int k = Integer.valueOf(tokens[2]);
			phi[i][j][k] = Float.valueOf(tokens[3]);
		}
	}

	private static void readTheta(float[][] theta, String file) {
		// TODO Auto-generated method stub
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(file, lines);
		for(int i = 0; i < theta.length; i++){
			String[] tokens = lines.get(i).split("\t");
			for(int j = 0 ; j < theta[i].length; j++){
				theta[i][j] = Float.valueOf(tokens[j]);
			}
		}
	}
	
	public class TwordsComparable implements Comparator<Integer> {
		public float [] sortProb; // Store probability of each word in topic k
		
		public TwordsComparable (float[] sortProb){
			this.sortProb = sortProb;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			// TODO Auto-generated method stub
			//Sort topic word index according to the probability of each word in topic k
			if(sortProb[o1] > sortProb[o2]) return -1;
			else if(sortProb[o1] < sortProb[o2]) return 1;
			else return 0;
		}
	}
}
