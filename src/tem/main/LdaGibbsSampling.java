package tem.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import tem.com.FileUtil;
import tem.com.JC;
import tem.conf.ConstantConfig;
import tem.conf.PathConfig;
import tem.main.Documents;
import tem.main.TEMModelSampling.modelparameters;

/**Liu Yang's implementation of Gibbs Sampling of LDA
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class LdaGibbsSampling {
	
	public static class modelparameters {
		float alpha = 1f; //usual value is 50 / K
		float beta = 0.1f;//usual value is 0.1
		int topicNum = 15;
		int iteration = 500;
		int saveStep = 20;
		int beginSaveIters = 440;
	}
	
	/**Get parameters from configuring file. If the 
	 * configuring file has value in it, use the value.
	 * Else the default value in program will be used
	 * @param ldaparameters
	 * @param parameterFile
	 * @return void
	 */
	private static void getParametersFromFile(modelparameters ldaparameters,
			String parameterFile) {
		// TODO Auto-generated method stub
		ArrayList<String> paramLines = new ArrayList<String>();
		FileUtil.readLines(parameterFile, paramLines);
		for(String line : paramLines){
			String[] lineParts = line.split("\t");
			switch(parameters.valueOf(lineParts[0])){
			case alpha:
				ldaparameters.alpha = Float.valueOf(lineParts[1]);
				break;
			case beta:
				ldaparameters.beta = Float.valueOf(lineParts[1]);
				break;
			case topicNum:
				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
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
	
	public enum parameters{
		alpha, beta, topicNum, iteration, saveStep, beginSaveIters;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		String dataPath = PathConfig.modelResPath + "USER80/";
		String minPostNum = PathConfig.minPostNum;
		Documents docSet = new Documents();
		String docfile = dataPath + "USER" + minPostNum + ".data";
		docSet = FileUtil.loadClass(docSet, docfile);
		
		System.out.println("indexToTermMap size : "
				+ docSet.indexToTermMap.size());
		// System.out.println("indexToTermMap : " + docSet.indexToTermMap);
		System.out.println("indexToTagMap size : "
				+ docSet.indexToTagMap.size());
		System.out.println("indexToVoteMap size : "
				+ docSet.indexToVoteMap.size());
		
		modelparameters ldaparameters = new modelparameters();
		System.out.println("Topic Num : " + ldaparameters.topicNum);
		LdaModel model = new LdaModel(ldaparameters);
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet);
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		System.out.println("Done!");
	}
}
