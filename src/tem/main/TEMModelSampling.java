package tem.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import tem.com.ComUtil;
import tem.com.FileUtil;
import tem.com.JC;
import tem.com.MatrixUtil;
import tem.conf.ConstantConfig;
import tem.conf.PathConfig;
import tem.main.Documents;
import tem.main.Documents.Document;
import tem.main.TEMModel;

/**
 * Gibbs Sampling of Topic Expertise Model
 * 
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class TEMModelSampling {

	public static class modelparameters {
		float alpha = 0.5f;// usual value is 50 / K
		float beta = 0.01f;
		float gamma = 0.01f;
		float eta = 0.1f;// usual value is 0.1
		float xi = 0.01f;
		int topicNum = 20;
		int expertiseNum = 3;

		int iteration = 300;
		int saveStep = 20;
		int beginSaveIters = 5;
	}

	/**
	 * Get parameters from configuring file. If the configuring file has value
	 * in it, use the value. Else the default value in program will be used
	 * 
	 * @param ldaparameters
	 * @param parameterFile
	 * @return void
	 */
	private static void getParametersFromFile(modelparameters ldaparameters,
			String parameterFile) {
		// TODO Auto-generated method stub
		ArrayList<String> paramLines = new ArrayList<String>();
		FileUtil.readLines(parameterFile, paramLines);
		for (String line : paramLines) {
			String[] lineParts = line.split("\t");
			switch (parameters.valueOf(lineParts[0])) {
			case alpha:
				ldaparameters.alpha = Float.valueOf(lineParts[1]);
				break;
			case beta:
				ldaparameters.beta = Float.valueOf(lineParts[1]);
				break;
			case gamma:
				ldaparameters.gamma = Float.valueOf(lineParts[1]);
				break;
			case eta:
				ldaparameters.eta = Float.valueOf(lineParts[1]);
				break;
			case xi:
				ldaparameters.xi = Float.valueOf(lineParts[1]);
				break;
			case topicNum:
				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
				break;
			case expertiseNum:
				ldaparameters.expertiseNum = Integer.valueOf(lineParts[1]);
				break;
			case iteration:
				ldaparameters.iteration = Integer.valueOf(lineParts[1]);
				break;
			case saveStep:
				ldaparameters.saveStep = Integer.valueOf(lineParts[1]);
				break;
			case beginSaveIters:
				ldaparameters.beginSaveIters = Integer.valueOf(lineParts[1]);
				break;
			}
		}
	}

	public enum parameters {
		alpha, beta, gamma, eta, xi, topicNum, expertiseNum, iteration, saveStep, beginSaveIters;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		/*boolean local = true; // run on local machine
		//local = !local; // run on server

		new JC();
		String[] descrp = { "ParamsPath", "ResPath", "modelOutPath",
				"minPostNum" };
		String[] directory = { "data/modelParams/", "data/modelRes/ThreeM09/",
				"data/modelRes/ThreeM09/TMM3/", "80" };
		char[] options = { 'p', 'i', 'o', 'n' };
		if (local)
			JC.setInputOptions(descrp, directory, options, args, "1111", 0);
		else
			JC.setInputOptions(descrp, directory, options, args, "0000", 1);
		PathConfig.modelParamsPath = JC.getARG(0);
		PathConfig.modelResPath = JC.getARG(1);
		PathConfig.modelOutPath = JC.getARG(2);
		PathConfig.minPostNum = JC.getARG(3);
		JC.close();*/

		String minPostNum = PathConfig.minPostNum;
		// data/originalData/USER80/posts/
		String originalDocsPath = PathConfig.originalDataPath + "USER"
				+ minPostNum + "/posts/";
		//data/modelRes/ThreeM09/USER80
		String resultPath = PathConfig.modelResPath + "USER" + minPostNum + "/";
		String parameterFile = ConstantConfig.LDAPARAMETERFILE;

		modelparameters modelparam = new modelparameters();
		getParametersFromFile(modelparam, parameterFile);
		Documents docSet = new Documents();
		
		String docfile = resultPath + "USER" + minPostNum + ".data";
		// docSet.readDocs(originalDocsPath, minPostNum);

		// Save Serialized data
		docSet = FileUtil.loadClass(docSet, docfile);
		// FileUtil.saveClass(docSet, docfile);
		// Delete terms that appear only n times
		// docSet.deleteRareTerms(3);
		System.out.println("indexToTermMap size : "
				+ docSet.indexToTermMap.size());
		// System.out.println("indexToTermMap : " + docSet.indexToTermMap);
		System.out.println("indexToTagMap size : "
				+ docSet.indexToTagMap.size());
		System.out.println("indexToVoteMap size : "
				+ docSet.indexToVoteMap.size());

		// // test();
		// testGMM();
		//if (local)
			//removeData(docSet, 10);

		// try {
		// getVotes(docSet, PathConfig.votePath);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// for (int d = 0; d < 1; d++) {
		// Document doc = docSet.docs.get(d);
		// System.out.println(doc.docName);
		// // System.out.println("tags" + doc.tags);
		// System.out.println("title" + doc.title);
		// for (int n = 0; n < docSet.docs.get(d).docWords.length; n++) {
		// System.out.println("post vote: "
		// + docSet.indexToVoteMap.get(doc.votes[n]));
		// System.out.println("post tag: "
		// + docSet.indexToTagMap.get(doc.tags[n]));
		// for (int l = 0; l < docSet.docs.get(d).docWords[n].length; l++) {
		// System.out.print(doc.docWords[n][l] + " ");
		// // System.out.print("vote_" +
		// // docSet.indexToVoteMap.get(doc.votes[n])
		// // + " ");
		// // System.out.print("tag_" +
		// // docSet.indexToTagMap.get(doc.tags[n]) +
		// // " ");
		// }
		// System.out.println();
		// }
		// }

		// System.out.println("indexToTagMap" + docSet.indexToTagMap);
		// System.out.println("indexToVoteMap" + docSet.indexToVoteMap);
		// System.out.println("indexToTermMap" + docSet.indexToTermMap);
		// System.out.println("tagCountMap");
		// // tagCountMap
		// for (String tag : docSet.tagCountMap.keySet()) {
		// System.out.println(tag + "\t" + docSet.tagCountMap.get(tag));
		// }
		//
		// System.out.println("voteCountMap");
		// // voteCountMap
		// for (String vote : docSet.voteCountMap.keySet()) {
		// System.out.println(vote + "\t" + docSet.voteCountMap.get(vote));
		// }

		//Count quesions and answers
		int questionCount = 0;
		int answerCount = 0;
		 for (int d = 0; d < docSet.docs.size(); d++) {
			Document doc = docSet.docs.get(d);
			for(int n = 0; n < doc.docWords.length; n++){
				if(doc.postTypeID[n] == 1){
					questionCount++;
				} else {
					answerCount++;
				}
			}
		 }
		System.out.println("quesionsCount: " + questionCount);
		System.out.println("answerCount: " + answerCount);
		 
		TEMModel model = new TEMModel(modelparam);
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet, minPostNum);
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(modelparam.iteration, docSet, minPostNum);

		// save model in serialized data 
		String modelName = "E_" + model.ENum + "_T_" + model.K;
		FileUtil.saveClass(model, PathConfig.modelResPath + "USER" + minPostNum
				+ "/" + modelName + ".model");
		System.out.println("Done!");
	}

	private static void testGMM() {
		String testGMM = "data/modelRes/testGMM.txt";

		double alpha = 10;

		float[][] GMMData = null;
		GMMData = FileUtil.readArray(testGMM);
		FGMM fgmm = new FGMM(); //
		int ksize = 4;
		int[] clusterids = new int[GMMData.length];
		// random assign clusterID
		for (int n = 0; n < GMMData.length; n++) {
			int id = (int) (Math.floor(Math.random() * ksize));
			clusterids[n] = id;
		}
		fgmm.init2(GMMData, ksize, clusterids);
		// fgmm.learn2(GMMData, 500);// get GMM data index

		for (int iter = 0; iter < 500; iter++) {
			if (iter % 10 == 0) {
				System.out.print("Iteration " + iter + "\t");
				for (int i = 0; i < ksize; i++)
					System.out.print(fgmm.clusterDataIndex.get(i).size() + " ");
				System.out.println();
				System.out.println("lambda:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(fgmm.p_lambda[k], " ", "\n");
				System.out.println("mu:");
				for (int k = 0; k < ksize; k++)
					ComUtil.print(fgmm.p_mu[k], " ", "\n");
			}
			for (int n = 0; n < GMMData.length; n++) {
				double[] probsGMM = fgmm.LearnProbs(GMMData, n);
				double[] p = new double[ksize];

				for (int i = 0; i < ksize; i++) {
					p[i] = (fgmm.clusterDataIndex.get(i).size() + alpha)
							/ (fgmm.vector_n + ksize * alpha);
					p[i] *= probsGMM[i];
				}

				int newNo = ComUtil.sample(p, p.length);
				clusterids[n] = newNo;

				// update new mu and lambda
				fgmm.UpdateProbs(GMMData, n, newNo);
			}
		}
		System.out.println("done");
		System.exit(0);
	}

	private static void removeData(Documents docSet, int r) {
		for (int d = r; d < docSet.docs.size(); d++) {
			docSet.docs.remove(d);
			d--;
		}
		System.out.println("doc size: " + docSet.docs.size());
	}

	private static void test() {
		double[] set = new double[5];
		ComUtil.print(set, " ", "\n");
		changeset(set);
		ComUtil.print(set, " ", "\n");
		System.exit(0);
	}

	private static void changeset(double[] set) {
		for (int i = 0; i < set.length; i++)
			set[i] += 1;
	}

	private static void getVotes(Documents docSet, String votePath)
			throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				votePath)));

		for (int d = 0; d < docSet.docs.size(); d++) {
			Document doc = docSet.docs.get(d);
			// System.out.println(doc.docName);
			// //System.out.println("tags" + doc.tags);
			// System.out.println("title" + doc.title);
			for (int n = 0; n < docSet.docs.get(d).docWords.length; n++) {
				// System.out.println(d + "\t" + n + "\t" +
				// docSet.indexToVoteMap.get(doc.votes[n]));
				writer.write(d + "\t" + n + "\t"
						+ docSet.indexToVoteMap.get(doc.votes[n]) + "\n");
				// System.out.println("post vote: "
				// + docSet.indexToVoteMap.get(doc.votes[n]));
				// System.out.println("post tag: " +
				// docSet.indexToTagMap.get(doc.tags[n]));
				// for(int l = 0; l < docSet.docs.get(d).docWords[n].length;
				// l++){
				// System.out.print(doc.docWords[n][l] + " ");
				// //System.out.print("vote_" +
				// docSet.indexToVoteMap.get(doc.votes[n]) + " ");
				// //System.out.print("tag_" +
				// docSet.indexToTagMap.get(doc.tags[n]) + " ");
				// }
				// System.out.println();
			}
			writer.flush();
		}
		writer.close();
	}
}
