package tem.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import tem.com.FileUtil;
import tem.com.MatrixUtil;
import tem.conf.PathConfig;
import tem.main.Documents;
import tem.main.Documents.Document;
import tem.main.TEMModel;
import tem.uqa.UQAModelRes;

/**
 * TEM results evaluation
 * Recommend experts or answers for new questions
 */

public class TEMResPro {
	
	static String expType = "AnswerRec";
	//UserRec
	//AnswerRec

	static HashMap<Integer, Integer> userIDToTEMIndexMap = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> TEMIndexToUserIDMap = new HashMap<Integer, Integer>();
	
	static double [][] LDAtheta;
	static double [][] LDAphi;
	
	static UQAModelRes uqaRes = new UQAModelRes();
	static TEMModel model = new TEMModel();
	
	static Documents trainDocSet = new Documents();
	static Documents testDocSet = new Documents();
	
	
	/**
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		String trainData = PathConfig.modelResPath + "USER80/USER" + PathConfig.minPostNum + ".data";
		String testData = "data/modelRes/TestData/QATest.data";
		
		//Get LDA result
		String LDAThetaFile = PathConfig.modelResPath + "LDA/lda_500.theta";
		String LDAPhiFile = PathConfig.modelResPath + "LDA/lda_500.phi";
		LDAtheta = FileUtil.read2DArray(LDAThetaFile);
		LDAphi = FileUtil.read2DArray(LDAPhiFile);
		
		//Get UQA result
		String UQAPath = PathConfig.UQAPath;
		
		String dataFile = UQAPath + "UQAModelRes.data";
		uqaRes = FileUtil.loadClass(uqaRes, dataFile);
		System.out.println(uqaRes.indexToTagMap.size());
		System.out.println(uqaRes.indexToTermMap.size());
		System.out.println(uqaRes.indexToUserMap.size());
		
		//Get userlist
		
		trainDocSet = FileUtil.loadClass(trainDocSet, trainData);
		for (int u = 0; u < trainDocSet.docs.size(); u++) {
			userIDToTEMIndexMap.put(trainDocSet.docs.get(u).ownerUserID[0], u);
			TEMIndexToUserIDMap.put(u, trainDocSet.docs.get(u).ownerUserID[0]);
		}
		//System.out.println(userIDToTEMIndexMap);

		
		testDocSet = FileUtil.loadClass(testDocSet, testData);
		
		String[] modelNames = {"TEPR", "TEM", "TSPR", "PR", "ID","UQA"};
		//String[] modelNames = {"TEPR"};
		for(String modelName : modelNames){
			if(modelName.equals("TEPR") || modelName.equals("TEM")){
				//String[] ENums = {"11", "13", "14"};
				String[] TNums = {"15"};
				for(String T : TNums){
				//for(String E : ENums){
					String modelFile = PathConfig.modelResPath + "/USER" + PathConfig.minPostNum + "/Model_E10_T" + T + ".model";
					//Get TEM model result
					
					// load model
					model = FileUtil.loadClass(model, modelFile);
					System.out.println(model.K);
					
					String outputfile = PathConfig.modelResPath
							+ "/USER" + PathConfig.minPostNum + "/" + modelName + "_E10T" + T +".ModelFileVoteRes.model";
					try {
						estVotes(testDocSet, outputfile,  modelName, T);
					} catch (Exception e) {
						e.printStackTrace();
					}
						System.out.println(modelName +  " Done!");
					}
			} else {
				String modelFile = PathConfig.modelResPath + "/USER" + PathConfig.minPostNum + "/Model_E" + 8 + "_T15.model";
				//Get TEM model result
				 model = new TEMModel();
				// load model
				model = FileUtil.loadClass(model, modelFile);
				System.out.println(model.K);
				String outputfile = PathConfig.modelResPath
						+ "/USER" + PathConfig.minPostNum + "/" + modelName + ".ModelFileVoteRes.model";
				try {
					estVotes(testDocSet, outputfile,  modelName, "8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(modelName +  " Done!");
			}
		}
	}

	private static void estVotes(Documents docSet, String outputfile, String modelName, String T)
			throws Exception {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int q = 0; q < docSet.docs.get(0).postID.length; q++) {
			map.put(docSet.docs.get(0).postID[q], q);
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				outputfile)));

		Post newpost = new Post();

		for (int q = 1; q < docSet.docs.size(); q++) {
			// get question
			double[] thetaQ = null;
			int qid = docSet.docs.get(q).parentID[0];
			int parentPostPos = map.get(qid);
			newpost = new Post(docSet.docs.get(0), parentPostPos,
					docSet.indexToVoteMap, modelName, T);
			if(modelName.equals("TEM") || modelName.equals("TEPR") || modelName.equals("TSPR") || modelName.equals("UQA")){
				thetaQ = newpost.thetaD.clone();
			}
			// get answers
			Document doc = docSet.docs.get(q);
			for (int a = 0; a < docSet.docs.get(q).ownerUserID.length; a++) {
				int tqid = doc.parentID[a];
				if (tqid == qid) {
					newpost = new Post(docSet.docs.get(q), a,
							docSet.indexToVoteMap, modelName , T);
					// compute distance between new thetaA and tetaQ
					if (newpost.flag) {
						double sim = 1;
						if(modelName.equals("TEM") || modelName.equals("TEPR") || modelName.equals("TSPR") || modelName.equals("UQA")){
							if(expType.equals("UserRec")){
								sim = 1 - MatrixUtil.JS(newpost.thetaU, thetaQ);
							} else if(expType.equals("AnswerRec")) {
								sim = 1 - MatrixUtil.JS(newpost.thetaD, thetaQ);
							} else {
								System.err.println("exp type error!");
							}
							
						} else {
							sim = 1;
						}
						double auth = newpost.estAuth;
						//askerID	answerID	answerVote	topicSimilarity	authScore	topicSimilarity*authScore KLAQ KLQA	
						//Matlab only use 1 2 3 6
						//For TEM + TEPR, The only different is authScore part.
						//The authScore part should use the results of TEPR algorithm 
						//Rank answers based on the 6th column 
						//Could modify rank col in Matlab code
						
						writer.write(docSet.docs.get(0).postID[q - 1]
								+ "\t" + docSet.docs.get(q).ownerUserID[a]
								+ "\t" + newpost.vote + "\t" + sim + "\t"
								+ auth + "\t" + (sim * auth) + "\n");
								//+ MatrixUtil.KL(newpost.thetaD, thetaQ) + "\t"
								//+ MatrixUtil.KL(thetaQ, newpost.thetaD) + "\n");
					} else {
						//System.out.println(newpost.userid + " is not contained in userSet!");
					}

				} else {
					System.err.println("tqid != qid!!");
					System.exit(0);
				}
			}
			writer.flush();
		}
		writer.close();
	}

	public static class Post {

		boolean flag = true;
		public int userid;
		public double[] thetaD; //doc topic distribution
		public double[] thetaU; //user topic distribution
		public float vote;
		public float estAuth = 0f;;

		public Post(Document doc, int n, ArrayList<String> indexToVoteMap,
				String modelName, String T) {
			int tmpPostID = doc.ownerUserID[n];

			if (userIDToTEMIndexMap.containsKey(tmpPostID)) {
				userid = userIDToTEMIndexMap.get(tmpPostID);
				vote = Float.parseFloat(indexToVoteMap.get(doc.votes[n]));
				
				if(modelName.equals("TEPR") || (modelName.equals("TEM"))){
					float[] userTheta = model.theta[userid];
					thetaU = new double[userTheta.length];
					for(int i = 0; i < userTheta.length; i++){
						thetaU[i] = userTheta[i];
					}
				} else if(modelName.equals("TSPR")){
					thetaU = LDAtheta[userid];
				} else if(modelName.equals("UQA")){
					//System.out.println("userid: " + TEMIndexToUserIDMap.get(userid));
					//System.out.println(uqaRes.userToIndexMap);
					if(!uqaRes.userToIndexMap.containsKey(String.valueOf(TEMIndexToUserIDMap.get(userid)))){
						System.err.println("found not exsited userid : " + TEMIndexToUserIDMap.get(userid));
					}
					int uqaUserIndex = uqaRes.userToIndexMap.get(String.valueOf(TEMIndexToUserIDMap.get(userid)));
					thetaU = uqaRes.theta[uqaUserIndex];
				}
				
				//TEM, TEPR, TSPR, PR, ID, UQA mainly influence the authority socre and topic similarity
				//TSPR use topic information from LDA output
				//PR and ID only have authority score
				//UQA only have topic similarity score
				
				EstTheta(doc.docWords[n], doc.tags[n], modelName);
				EstAuthority(modelName, T);
			} else {
				userid = tmpPostID;
				//System.out.println("test!!!!");
				flag = false;
			}
		}

		public Post() {
		}

		private void EstTheta(int[] words, int[] tags, String modelName) {
			if(modelName == "TEM" || modelName == "TEPR"){
				double[] probs = new double[model.K];
				int[] overflow = new int[model.K];

				for (int i = 0; i < model.K; i++) {
					probs[i] = model.theta[userid][i];
					for (int w = 0; w < words.length; w++) {
						if (words[w] < model.varphi[i][0].length) {
							probs[i] *= model.varphi[i][0][words[w]];
							probs[i] = ModelComFunc.checkDoubleOverflow(probs[i],
									i, overflow);
						}
					}
					for (int t = 0; t < tags.length; t++) {
						if (tags[t] < model.psi[i].length) {
							probs[i] *= model.psi[i][tags[t]];
							probs[i] = ModelComFunc.checkDoubleOverflow(probs[i],
									i, overflow);
						}
					}
				}
				ModelComFunc.reAssignP(probs, overflow);
				thetaD = probs.clone();
				MatrixUtil.norm1(thetaD);
			} else if(modelName == "TSPR"){
				double[] probs = new double[model.K];
				int[] overflow = new int[model.K];

				for (int i = 0; i < model.K; i++) {
					probs[i] = LDAtheta[userid][i];
					for (int w = 0; w < words.length; w++) {
						if (words[w] < LDAphi[i].length) {
							probs[i] *= LDAphi[i][words[w]];
							probs[i] = ModelComFunc.checkDoubleOverflow(probs[i],
									i, overflow);
						}
					}
				}
				ModelComFunc.reAssignP(probs, overflow);
				thetaD = probs.clone();
				MatrixUtil.norm1(thetaD);
			} else if(modelName == "UQA"){ 
				System.out.println("topicNum" + model.K);
				double[] probs = new double[model.K];
				int[] overflow = new int[model.K];

				for (int i = 0; i < model.K; i++) {
					int uqaUserIndex = uqaRes.userToIndexMap.get(String.valueOf(TEMIndexToUserIDMap.get(userid)));
					probs[i] = uqaRes.theta[uqaUserIndex][i];
					for (int w = 0; w < words.length; w++) {
						if(uqaRes.termToIndexMap.containsKey((testDocSet.indexToTermMap.get(words[w])))){
							int uqaWordIndex = uqaRes.termToIndexMap.get(testDocSet.indexToTermMap.get(words[w]));
							probs[i] *= uqaRes.phi[i][uqaWordIndex];
							probs[i] = ModelComFunc.checkDoubleOverflow(probs[i],
									i, overflow);
						}
					}
					for (int t = 0; t < tags.length; t++) {
						if(uqaRes.tagToIndexMap.containsKey((testDocSet.indexToTagMap.get(tags[t])))){
							int uqaTagIndex = uqaRes.tagToIndexMap.get(testDocSet.indexToTagMap.get(tags[t]));
							probs[i] *= uqaRes.psi[i][uqaTagIndex];
							probs[i] = ModelComFunc.checkDoubleOverflow(probs[i],
									i, overflow);
						}
					}
				}
				ModelComFunc.reAssignP(probs, overflow);
				thetaD = probs.clone();
				MatrixUtil.norm1(thetaD);
			} else {
				//System.out.println("PR and ID baselines does not have topic similarity information!");
			}
		}

		public void EstAuthority(String modelName, String T) {
			// authority score based only TEM result
			// \sum thetaD[i]*\mu_i
			if(modelName.equals("TEM")){
				for (int i = 0; i < thetaU.length; i++) {
					double score = 0d;
					for (int e = 0; e < model.ENum; e++) {
						score += model.phi[i][userid][e] * model.fgmm.p_mu[e][0];
					}
					if(expType.equals("AnswerRec")) {
						estAuth += thetaD[i] * score;
					} else {
						estAuth += thetaU[i] * score;
					}
				}
			} else if(modelName.equals("TEPR")){
				String finalPRALLFile = PathConfig.modelResPath + "USER80/TEPR.E10T" + T + "finalPRAll";
				//System.out.println("In Estauthority TEPR userid = " + userid);
				double [][] tepr = FileUtil.read2DArray(finalPRALLFile);
				for (int i = 0; i < thetaU.length; i++) {
					if(expType.equals("AnswerRec")) {
						estAuth += thetaD[i] * tepr[i][userid];
					} else {
						estAuth += thetaU[i] * tepr[i][userid];
					}
					
				}
			} else if (modelName.equals("TSPR")){
				// authority score based on TSPR result
				// In TSPR, we use tspr score repalce authrity score here
				String finalPRALLFile = PathConfig.modelResPath + "USER80/TSPR.finalPRAll";
				double [][] tspr = FileUtil.read2DArray(finalPRALLFile);
				for (int i = 0; i < LDAtheta[0].length; i++) {
					estAuth += LDAtheta[userid][i] * tspr[i][userid];
				}
			} else if (modelName.equals("PR")) {
				// In PR, the authority is PR score which has no relation with topic
				String finalPRALLFile = PathConfig.modelResPath + "USER80/PR.finalPRAll";
				//System.out.println("In Estauthority TEPR userid = " + userid);
				double [][] pr = FileUtil.read2DArray(finalPRALLFile);
				estAuth = (float) pr[0][userid];
			} else if (modelName.equals("ID")) {
				String finalPRALLFile = PathConfig.modelResPath + "USER80/ID.finalPRAll";
				//System.out.println("In Estauthority TEPR userid = " + userid);
				double [][] id = FileUtil.read2DArray(finalPRALLFile);
				estAuth = (float) id[0][userid];
			} else if(modelName.equals("UQA")){ 
				estAuth = 1f;
			} else {
				System.out.println("model name error!");
			}
		}
	}
}
