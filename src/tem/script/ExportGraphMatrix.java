package tem.script;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tem.com.FileUtil;
import tem.conf.PathConfig;

public class ExportGraphMatrix {
	private static int[][] QAGraph;
	private static int userNum;
	private static ArrayList<String> indexToUserIDMap;
	private static Map<String, String> userIDToIndexMap; 

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		final DBConnection db = new DBConnection();
		String minPostNum = "80";
		db.getConn();
		String sql = "";
		String userIDFile = PathConfig.originalDataPath + "USER" + minPostNum + "/user.IDs";
		String postFolder = PathConfig.originalDataPath + "USER" + minPostNum + "/posts/";
		String askerFolder = PathConfig.originalDataPath + "USER" + minPostNum + "/askers/";
		String graphDataFile = PathConfig.originalDataPath + "USER" + minPostNum + "/userVoteWeighted.QAgraph";
		ArrayList<String> postLines = new ArrayList<String>();
		
		ArrayList<String> userIDs = new ArrayList<String>();
		FileUtil.readLines(userIDFile, userIDs);
		buildIndexUserID(userIDs);
		QAGraph = new int[userNum][userNum];
		ArrayList<String> askerLines = new ArrayList<String>();
		
		for(int i = 0; i < userNum; i++){
			System.out.println("i = " + i);
			String postFile = postFolder + userIDs.get(i) + ".posts";
			//String askerFile = askerFolder + userIDs.get(i) + ".askers";
			//if(new File(askerFile).exists()){
				//System.out.println(askerFile + "is exists!");
				//continue;
			//}
			postLines.clear();
			FileUtil.readLines(postFile, postLines);
			
			askerLines.clear();
			//System.out.println("after clear, askerLines size: " + askerLines.size());
			for(String postLine : postLines){
				String [] postTokens = postLine.split("\t");
				if(postTokens[1].equals("2")){
					String parentID = postTokens[2];
					String askerID = getAuthorIDbyPostID(parentID, db);
					String vote = postTokens[5];
					//System.out.println("vote " + vote);
					//askerLines.add(askerID);
					//System.out.println("add, askerLines size: " + askerLines.size());
					
					//Answer count weighted graph
					if(userIDToIndexMap.containsKey(askerID)){
						QAGraph[Integer.valueOf(userIDToIndexMap.get(askerID))][Integer.valueOf(userIDToIndexMap.get(userIDs.get(i)))] += Integer.valueOf(vote);
					}
				} else {
					//askerLines.add("self");
				}
			}
			//FileUtil.writeLines(askerFile, askerLines);
			//System.out.println("before clear, askerLines size: " + askerLines.size());
		
			//System.out.println("after clear, askerLines size: " + askerLines.size());
		}
		printQAGraph(graphDataFile);
		db.close();
	}

	private static void printQAGraph(String graphDataFile) {
		// TODO Auto-generated method stub
		ArrayList<String> QAGLines = new ArrayList<String>();
		for(int i = 0; i < QAGraph.length; i++){
			String line = "";
			for(int j = 0; j < QAGraph[i].length; j++){
				line += QAGraph[i][j] + "\t";
			}
			QAGLines.add(line);
		}
		FileUtil.writeLines(graphDataFile, QAGLines);
	}

	private static String getAuthorIDbyPostID(String postID, DBConnection db) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "select * from posts where id = "+ postID;
		ResultSet rs = db.executeQuery(sql);
		String authorID = "";
		while(rs.next()){ 
			authorID = rs.getString(9);
		}
		rs.close();
		return authorID;
	}

	private static void buildIndexUserID(ArrayList<String> userIDs) {
		// TODO Auto-generated method stub
		indexToUserIDMap = new ArrayList<String>();
		userIDToIndexMap = new HashMap<String, String>();
		
		for(int i = 0; i < userIDs.size(); i++){
			indexToUserIDMap.add(userIDs.get(i));
			userIDToIndexMap.put(userIDs.get(i), String.valueOf(i));
		}
		userNum = userIDs.size();
	}
}
