package tem.script;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tem.com.FileUtil;
import tem.conf.PathConfig;

/**Export Test Data for Rank answers/experts
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class ExportTestDataForRank {

	 /* @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		final DBConnection db = new DBConnection();
		String minPostNum = "80";
		db.getConn();
		String sql = "";
		String userIDFile = PathConfig.scriptDataPath + "USERID" + minPostNum;
		ArrayList<String> userIDs = new ArrayList<String>();
		FileUtil.readLines(userIDFile, userIDs);
		String testDataFolder = PathConfig.testDataPath;
		ArrayList<String> questionLines = new ArrayList<String>();
		ArrayList<String> answerLines = new ArrayList<String>();
		ArrayList<String> questionIDs = new ArrayList<String>();
		String questionFile = testDataFolder + "testData.questions";
		String questionIDFile = testDataFolder + "testDataQuestions.id";
		FileUtil.readLines(questionIDFile, questionIDs);

		for(String questionIDLine : questionIDs){
			String questionID = questionIDLine.split("\t")[1];
			sql = "select * from posts where id = "+ questionID;
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){ 
				questionLines.add(rs.getInt("ID") + "\t" + rs.getInt("POSTTYPEID") 
						+ "\t" + rs.getInt("PARENTID") + "\t" + rs.getInt("ACCEPTEDANSWERID") + "\t" + rs.getString("CREATIONDATE")
						+ "\t" + rs.getInt("SCORE") + "\t" + rs.getInt("VIEWCOUNT") + "\t" + (rs.getString("BODY") == null ? "null": rs.getString("BODY").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("OWNERUSERID")
						+ "\t" + rs.getInt("LASTEDITORUSERID") + "\t" + rs.getString("LASTEDITORDISPLAYNAME") + "\t" + rs.getString("LASTEDITDATE")
						+ "\t" + rs.getString("LASTACTIVITYDATE") + "\t" + rs.getString("COMMUNITYOWNEDDATE") + "\t" + rs.getString("CLOSEDDATE")
						+ "\t" + (rs.getString("TITLE") == null?"null":rs.getString("TITLE".replaceAll("[\n-\r-\t]", " ")))+ "\t" + rs.getString("TAGS") + "\t" + rs.getInt("ANSWERCOUNT")
						+ "\t" + rs.getInt("COMMENTCOUNT") + "\t" + rs.getInt("FAVORITECOUNT"));
			}
			
			/*for(String userID : userIDs){
			sql = "select * from posts where posts.creationdate > '2009-08-01 00:00:00'" +
					" and posts.creationdate < '2009-11-01 00:00:00' and posts.posttypeid = 1 " +
					"and answercount > 5 and owneruserid = " + userID;
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){ 
			System.out.println("userID: " + userID + 
						" question id: " + rs.getInt(1) +
						" answercount: " + rs.getInt(18) + 
						"question tag: " + rs.getString(17) +
						" question title: " + rs.getString(16) );
				questionLines.add(rs.getInt("ID") + "\t" + rs.getInt("POSTTYPEID") 
						+ "\t" + rs.getInt("PARENTID") + "\t" + rs.getInt("ACCEPTEDANSWERID") + "\t" + rs.getString("CREATIONDATE")
						+ "\t" + rs.getInt("SCORE") + "\t" + rs.getInt("VIEWCOUNT") + "\t" + (rs.getString("BODY") == null ? "null": rs.getString("BODY").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("OWNERUSERID")
						+ "\t" + rs.getInt("LASTEDITORUSERID") + "\t" + rs.getString("LASTEDITORDISPLAYNAME") + "\t" + rs.getString("LASTEDITDATE")
						+ "\t" + rs.getString("LASTACTIVITYDATE") + "\t" + rs.getString("COMMUNITYOWNEDDATE") + "\t" + rs.getString("CLOSEDDATE")
						+ "\t" + (rs.getString("TITLE") == null?"null":rs.getString("TITLE".replaceAll("[\n-\r-\t]", " ")))+ "\t" + rs.getString("TAGS") + "\t" + rs.getInt("ANSWERCOUNT")
						+ "\t" + rs.getInt("COMMENTCOUNT") + "\t" + rs.getInt("FAVORITECOUNT"));*/
				System.out.println("questionID " + questionID);
				String answerFile = testDataFolder + questionID + ".answers";
				sql = "select * from posts where posts.posttypeid = 2 and parentid = "+ questionID;
				ResultSet rs2 = db.executeQuery(sql);
				while(rs2.next()){ 
					answerLines.add(rs2.getInt("ID") + "\t" + rs2.getInt("POSTTYPEID") 
							+ "\t" + rs2.getInt("PARENTID") + "\t" + rs2.getInt("ACCEPTEDANSWERID") + "\t" + rs2.getString("CREATIONDATE")
							+ "\t" + rs2.getInt("SCORE") + "\t" + rs2.getInt("VIEWCOUNT") + "\t" + (rs2.getString("BODY") == null ? "null": rs2.getString("BODY").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs2.getInt("OWNERUSERID")
							+ "\t" + rs2.getInt("LASTEDITORUSERID") + "\t" + rs2.getString("LASTEDITORDISPLAYNAME") + "\t" + rs2.getString("LASTEDITDATE")
							+ "\t" + rs2.getString("LASTACTIVITYDATE") + "\t" + rs2.getString("COMMUNITYOWNEDDATE") + "\t" + rs2.getString("CLOSEDDATE")
							+ "\t" + (rs2.getString("TITLE") == null?"null":rs2.getString("TITLE".replaceAll("[\n-\r-\t]", " ")))+ "\t" + rs2.getString("TAGS") + "\t" + rs2.getInt("ANSWERCOUNT")
							+ "\t" + rs2.getInt("COMMENTCOUNT") + "\t" + rs2.getInt("FAVORITECOUNT"));
				}
				FileUtil.writeLines(answerFile, answerLines);
				answerLines.clear();
			}
		FileUtil.writeLines(questionFile, questionLines);
		db.close();
	}
}
