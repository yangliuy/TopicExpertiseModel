package tem.script;

import java.sql.*;

public class DBConnection {
	private final String DBDRIVER = "com.mysql.jdbc.Driver";
	private final String DBURL = "jdbc:mysql://localhost:3306/stackoverflow201106";
	private final String DBUSER = "root";
	private final String DBPASSWORD = "root";
	private Connection conn = null;

	public Connection getConn() {
		try {
			Class.forName(DBDRIVER).newInstance();
			conn = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);
		} catch (Exception e) {
			System.out.println("connect failed:" + e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}

	public void close() {
		try {
			this.conn.close();
		} catch (Exception e) {
			System.out.println("close failed" + e.getMessage());
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		try {
			//System.out.println(sql);
			rs = null;
			Connection conn = this.conn;
			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			System.out.println("executeQuery error" +  ex.getMessage());
			ex.printStackTrace();
		}
		return rs;
	}

	public boolean executeUpdate(String strSQL) {
		try {
			Connection conn = this.conn;
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(strSQL);
		} catch (SQLException ex) {
			System.err.println("executeUpdate error£º" + ex.getMessage());
			ex.printStackTrace();
		}
		return true;
	}

}


