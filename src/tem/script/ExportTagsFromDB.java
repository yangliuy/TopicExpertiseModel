package tem.script;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tem.com.FileUtil;
import tem.conf.PathConfig;
import tem.script.DBConnection;

/**Export tags for each post from stackoverflow database
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */
public class ExportTagsFromDB {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String[] minPostNums = {"30"};
		final DBConnection db = new DBConnection();
		//ResultSet rs;
		db.getConn();
		String sql = "";
		StringBuffer sb = new StringBuffer();
		for(String minPostNum : minPostNums){
			String userIDFile = PathConfig.scriptDataPath + "USERID" + minPostNum;
			sql = "";
			ArrayList<String> userIDs = new ArrayList<String>();
			FileUtil.readLines(userIDFile, userIDs);
			String oriDataFolder = PathConfig.originalDataPath + "USER" + minPostNum;
			String postFolder = oriDataFolder + "/posts";
			String tagFolder = oriDataFolder + "/tags";
			ArrayList<String> postsLines = new ArrayList<String>();
			ArrayList<String> tagsLines = new ArrayList<String>();
			if(!new File(tagFolder).exists()){
				new File(tagFolder).mkdir();
			}
			
			for(String userID : userIDs){
				String userTagsFile = tagFolder + "/" + userID + ".tags";
				System.out.println("Now tag file is: " + userTagsFile);
				if(new File(userTagsFile).exists()){
					System.out.println(userTagsFile + "is existed!");
					continue;
				}
				
				String userPostsFile = postFolder + "/" + userID +".posts";
				FileUtil.readLines(userPostsFile, postsLines);
				for(String postLine : postsLines){
					String[] postLineTokens = postLine.split("\t");
					if(postLineTokens.length != 20){
						System.err.println("format error : " + postLine);
						tagsLines.add(postLineTokens[0] + "\t" + "null");
						continue;
					}
					String postTypeID = postLineTokens[1];
					if(postTypeID.equals("1")){
						tagsLines.add(postLineTokens[0] + "\t" + postLineTokens[16]);
					} else {
						String parentID = postLineTokens[2];
						//Use StringBuffer instead of add Strings
						sb.delete(0, sb.length());
						sb.append("select * from posts where id = '");
						sb.append(parentID);
						sb.append("';");
						sql = sb.toString();
						//System.out.println("sql builder: " + sql);
						ResultSet rs = db.executeQuery(sql);
						while(rs.next()){
							tagsLines.add(postLineTokens[0] + "\t" + rs.getString("TAGS"));
						}
						rs.close();
					}
				}
				
				FileUtil.writeLines(userTagsFile, tagsLines);
				postsLines.clear();
				tagsLines.clear();
			}
		}
		db.close();
	}
}
