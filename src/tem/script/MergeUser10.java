package tem.script;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import tem.com.FileUtil;

public class MergeUser10 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String data10Path = "data/originalData/ThreeM09/User80MergeUser10/similarQ/User10/";
		String data80Path = "data/originalData/ThreeM09/User80MergeUser10/";
		ArrayList<String> data10IDs = new ArrayList<String>();
		ArrayList<String> data80IDs = new ArrayList<String>();
		ArrayList<String> allIds = new ArrayList<String>();
		ArrayList<String> overLapIDLines = new ArrayList<String>();
		FileUtil.readLines(data10Path + "users.IDs", data10IDs);
		FileUtil.readLines(data80Path + "user.IDs", data80IDs);
		allIds.addAll(data80IDs);
		
		//Find overlap userIDs
		for(String userID10 : data10IDs){
			String data10PathPost = data10Path + "posts/" + userID10 + ".posts";
			if(data80IDs.contains(userID10.trim())){
				//overlap
				System.out.println("voerlap id: " + userID10);
				overLapIDLines.add(userID10);
			} else{
				allIds.add(userID10);
				String newData80PathPost = data80Path + "posts/" + userID10 + ".posts";
				FileUtil.copyFile(data10PathPost, newData80PathPost);
			}
		}
		FileUtil.writeLines(data80Path + "overlapIDs", overLapIDLines);
		
		FileUtil.writeLines(data80Path + "allUserIDs", allIds);
	}
}
