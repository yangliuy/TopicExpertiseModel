package tem.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import tem.com.FileUtil;
import tem.conf.PathConfig;

/**
 * User Rec
 * Merge answers with the same user with one
 */

public class TEMResProUserRecMergeU {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String ModelFileVoteResFolder = PathConfig.modelResPath + "USER" + PathConfig.minPostNum + "";
		ArrayList<String> resLines = new ArrayList<String>();
		ArrayList<String> mergeLines = new ArrayList<String>();
		Set<String> QidAUseridSet = new TreeSet <String>();
		Map<String, String> IDPairScoreMap = new TreeMap<String, String>(); 
		
		for(File modelFVRfile : new File(ModelFileVoteResFolder).listFiles()){
			if(modelFVRfile.getName().contains("ModelFileVoteRes")){
				String mergeFileName = ModelFileVoteResFolder + "/MergeFiles/" + modelFVRfile.getName() + ".merge";
				System.out.println("mergeFileName " + mergeFileName);
				if(new File(mergeFileName).exists()){
					System.out.println(mergeFileName + "is existed! " );
					continue;
				}
				resLines.clear();
				QidAUseridSet.clear();
				IDPairScoreMap.clear();
				mergeLines.clear();;
				FileUtil.readLines(modelFVRfile.getAbsolutePath(), resLines);
				for(int i = 0; i < resLines.size(); i++){
					String[] tokens = resLines.get(i).split("\t");
					QidAUseridSet.add(tokens[0] + "\t" + tokens[1]);
					IDPairScoreMap.put(tokens[0] + "\t" + tokens[1], tokens[3] + "\t" + tokens[4] + "\t" + tokens[5]);
				}
				System.out.println("QidAUseridSet size: " + QidAUseridSet.size());
				for(String idPair : QidAUseridSet){
					double sum = 0;
					double count = 0;
					for(String resLine : resLines){
						String[] tokens = resLine.split("\t");
						String pairKey = tokens[0] + "\t" + tokens[1];
						if(idPair.equals(pairKey)){
							sum += Double.valueOf(tokens[2]);
							count ++;
						}
					}
					double averageVote = sum / count;
					mergeLines.add(idPair + "\t" + averageVote + "\t" + IDPairScoreMap.get(idPair)); 
				}
				FileUtil.writeLines(mergeFileName , mergeLines);
				mergeLines.clear();
			}
		}
	}
}
