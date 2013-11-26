package tem.script;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.script.DBConnection;

/**Export users and posts data from stackoverflow database
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */
public class ExportExpCorpusFromDB {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String[] minPostNums = {"30"};
		final DBConnection db = new DBConnection();
		db.getConn();
		for(String minPostNum : minPostNums){
			String userIDFile = PathConfig.scriptDataPath + "USERID" + minPostNum;
			/*String sql = "select owneruserid from (select owneruserid, count(posts.id)  as postNum from posts"
+ " where posts.creationdate > '2009-05-01 00:00:00' and posts.creationdate < '2009-08-01 00:00:00' group by owneruserid) as newt where newt.postNum > " + minPostNum + ";";
			ResultSet rs = db.executeQuery(sql);
			ArrayList<String> userIDs = new ArrayList<String>();
			while(rs.next()){
				int userID = rs.getInt("owneruserid");
				if(userID != 0){
					userIDs.add(String.valueOf(userID));
				}	
			}
			System.out.println("userIDs size : " + userIDs.size());
			FileUtil.writeLines(userIDFile, userIDs);*/
			String sql = "";
			ArrayList<String> userIDs = new ArrayList<String>();
			FileUtil.readLines(userIDFile, userIDs);
			String oriDataFolder = PathConfig.originalDataPath + "USER" + minPostNum;
			if(!new File(oriDataFolder).exists()){
				new File(oriDataFolder).mkdir();
			}
			String oriDataUserIDFile = oriDataFolder + "/user.IDs";
			FileUtil.writeLines(oriDataUserIDFile, userIDs);
			
			String oriDataUserInforFile = oriDataFolder + "/user.Infors";
			ArrayList<String> userInforLines = new ArrayList<String>();
			String postFolder = oriDataFolder + "/posts";
			ArrayList<String> postsLines = new ArrayList<String>();
			if(!new File(postFolder).exists()){
				new File(postFolder).mkdir();
			}
			
			for(String userID : userIDs){
				String userPostsFile = postFolder + "/" + userID +".posts";
				if(new File(userPostsFile).exists()){
					System.out.println(userPostsFile + " is existed! ");
					continue;
				}
				sql = "select * from posts where owneruserid = '" + userID + "' and posts.creationdate > '2009-05-01 00:00:00' and posts.creationdate < '2009-08-01 00:00:00';";
				ResultSet rs = db.executeQuery(sql);
				while(rs.next()){ 
					String postsLine = rs.getInt("ID") + "\t" + rs.getInt("POSTTYPEID") 
										+ "\t" + rs.getInt("PARENTID") + "\t" + rs.getInt("ACCEPTEDANSWERID") + "\t" + rs.getString("CREATIONDATE")
										+ "\t" + rs.getInt("SCORE") + "\t" + rs.getInt("VIEWCOUNT") + "\t" + (rs.getString("BODY") == null ? "null": rs.getString("BODY").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("OWNERUSERID")
										+ "\t" + rs.getInt("LASTEDITORUSERID") + "\t" + rs.getString("LASTEDITORDISPLAYNAME") + "\t" + rs.getString("LASTEDITDATE")
										+ "\t" + rs.getString("LASTACTIVITYDATE") + "\t" + rs.getString("COMMUNITYOWNEDDATE") + "\t" + rs.getString("CLOSEDDATE")
										+ "\t" + (rs.getString("TITLE") == null?"null":rs.getString("TITLE".replaceAll("[\n-\r-\t]", " ")))+ "\t" + rs.getString("TAGS") + "\t" + rs.getInt("ANSWERCOUNT")
										+ "\t" + rs.getInt("COMMENTCOUNT") + "\t" + rs.getInt("FAVORITECOUNT");
					postsLines.add(postsLine);
				}
				
				FileUtil.writeLines(userPostsFile, postsLines);
				postsLines.clear();
				sql = "select * from users where id = '" + userID + "';";
				rs = db.executeQuery(sql);
				while(rs.next()){ 
					String userInforLine = rs.getInt("ID") + "\t" + rs.getInt("REPUTATION") + "\t" + rs.getString("CREATIONDATE") 
										+ "\t" + rs.getString("DISPLAYNAME") + "\t" + rs.getString("EMAILHASH")
										+ "\t" + rs.getString("LASTACCESSDATE") + "\t" + rs.getString("WEBSITEURL") + "\t" + rs.getString("LOCATION")
										+ "\t" + rs.getInt("AGE") + "\t" + (rs.getString("ABOUTME") == null?"null":rs.getString("ABOUTME").replaceAll("[\n-\r-\t]", " ")) + "\t" + rs.getInt("VIEWS")
										+ "\t" + rs.getInt("UPVOTES") + "\t" + rs.getInt("DOWNVOTES");
					//System.out.println("userInforLine: " + userInforLine);
					userInforLines.add(userInforLine);
				}
				rs.close();
			}
			FileUtil.writeLines(oriDataUserInforFile, userInforLines);
		}
	}
}
