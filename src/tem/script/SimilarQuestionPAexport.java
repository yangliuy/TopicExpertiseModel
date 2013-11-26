package tem.script;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.script.DBConnection;

/**Export similar questions and authors
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */
public class SimilarQuestionPAexport {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		final DBConnection db = new DBConnection();
		db.getConn();
		String path = "data/scriptData";
		String QuestionIDFileName = path + "/SimilarQ/TestQuestionLiu.txt";
		String QuestionPostFileName = path + "/SimilarQ/TestQuestionLiu.posts";
		String QuestionAskerFileName = path + "/SimilarQ/TestQuestionLiu.askers";
		ArrayList<String> qIDs = new ArrayList<String>();
		FileUtil.readLines(QuestionIDFileName, qIDs);
		ArrayList<String> postLines = new ArrayList<String>();
		ArrayList<String> askerLies = new ArrayList<String>();
		for(String qid : qIDs){
			int askerID = -100;
			String sql = "select * from posts where id = " + qid;
			ResultSet rs = db.executeQuery(sql);
			while(rs.next()){
				String postsLine = rs.getInt("ID") + "\t" + rs.getInt("POSTTYPEID") 
						+ "\t" + rs.getInt("PARENTID") + "\t" + rs.getInt("ACCEPTEDANSWERID") + "\t" + rs.getString("CREATIONDATE")
						+ "\t" + rs.getInt("SCORE") + "\t" + rs.getInt("VIEWCOUNT") + "\t" + (rs.getString("BODY") == null ? "null": rs.getString("BODY").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("OWNERUSERID")
						+ "\t" + rs.getInt("LASTEDITORUSERID") + "\t" + rs.getString("LASTEDITORDISPLAYNAME") + "\t" + rs.getString("LASTEDITDATE")
						+ "\t" + rs.getString("LASTACTIVITYDATE") + "\t" + rs.getString("COMMUNITYOWNEDDATE") + "\t" + rs.getString("CLOSEDDATE")
						+ "\t" + (rs.getString("TITLE") == null?"null":rs.getString("TITLE".replaceAll("[\n-\r-\t]", " ")))+ "\t" + rs.getString("TAGS") + "\t" + rs.getInt("ANSWERCOUNT")
						+ "\t" + rs.getInt("COMMENTCOUNT") + "\t" + rs.getInt("FAVORITECOUNT");
				postLines.add(postsLine);
				askerID = rs.getInt("OWNERUSERID");
			}
			sql = "select * from users where id = " + askerID;
			rs = db.executeQuery(sql);
			while(rs.next()){
				String userInforLine = rs.getInt("ID") + "\t" + rs.getInt("REPUTATION") + "\t" + rs.getString("CREATIONDATE") 
						+ "\t" + rs.getString("DISPLAYNAME") + "\t" + rs.getString("EMAILHASH")
						+ "\t" + rs.getString("LASTACCESSDATE") + "\t" + rs.getString("WEBSITEURL") + "\t" + rs.getString("LOCATION")
						+ "\t" + rs.getInt("AGE") + "\t" + (rs.getString("ABOUTME") == null?"null":rs.getString("ABOUTME").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("VIEWS")
						+ "\t" + rs.getInt("UPVOTES") + "\t" + rs.getInt("DOWNVOTES");
				//System.out.println("userInforLine: " + userInforLine);
				askerLies.add(qid + "\t" + userInforLine);
			}
			rs.close();
		}
		FileUtil.writeLines(QuestionAskerFileName, askerLies);
		FileUtil.writeLines(QuestionPostFileName, postLines);
		db.close();
	}
}
