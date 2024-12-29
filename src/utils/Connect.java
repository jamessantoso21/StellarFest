package utils;

import java.sql.*; 

public class Connect {
	
	private final String USERNAME = "root"; 
	private final String PASSWORD = ""; 
	private final String DATABASE = "stellarfest"; 
	private final String HOST = "localhost: 3306"; 
	private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE); 
	
	public ResultSet rs; 
	public ResultSetMetaData rsm; 
	private Connection con; 
	private Statement st; 
	
	private Connect() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD); 
			st = con.createStatement(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private static Connect connect = null; 
	public static Connect getInstance() {
		if (connect == null) {
			connect = new Connect(); 
		}
		return connect; 
	}
	
//	5. SELECT DATA -> disimpen di variable rs 
	public ResultSet execQuery(String Query) {
		try {
			rs = st.executeQuery(Query);
			rsm = rs.getMetaData(); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return rs; 
	}
	
	public int execUpdate(String query) {
	    int rowsAffected = 0;
	    try {
	        rowsAffected = st.executeUpdate(query);  // Execute the update and get the number of affected rows
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return rowsAffected;  // Return the number of rows affected
	}

	
	public PreparedStatement preparedStatement(String query) {
		PreparedStatement ps = null; 
		
		try {
			ps = con.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return ps; 
	}
	
	
	
}
