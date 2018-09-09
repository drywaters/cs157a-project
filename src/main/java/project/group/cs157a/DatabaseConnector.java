package project.group.cs157a;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

	private Connection conn = null;
	private static final String DATABASE_NAME = "cs157a_project";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "password";
	private static final String DB_URL = "jdbc:mysql://localhost";

	// Create a connection
	DatabaseConnector() {
		try {
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// Create Database with name DATABASE_NAME
	public void createDatabase() {
		Statement st = null; 
		
		// Delete old Database (if needed)
		try {
			st = conn.createStatement();
			
			// See if DB named DATABASE_NAME exists
			ResultSet rs = conn.getMetaData().getCatalogs();
			while (rs.next()) {
				String databaseNames = rs.getString(1);
			
				// if already exists, drop it
				if (databaseNames.contains(DATABASE_NAME)) {
					st.execute("DROP DATABASE " + DATABASE_NAME);
				}
			}
		} catch (SQLException e) {
			System.out.println("Unable to get database list from server, with error: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Create the Database
		try {
			st.executeUpdate("CREATE DATABASE " + DATABASE_NAME);
		} catch (SQLException e) {
			System.out.println("Unable to create database with name " + DATABASE_NAME + " with error: " + e.getMessage());
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					System.out.println("Unable to close statement with error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private void createTable() {
		// Create Table using Set of column names (HashSet)
	}

	private void insertData() {
		// Insert the calculated table data into the correct column
		// Should look like ex.
		// DocId | Token1 | Token2 | Token 3 | ...
		// ----------------------------------------------------------------
		// 1 | TFiDF# | TFiDF# | TFiDF# | ...
		// 2 | TFiDF# | TFiDF# | TFiDF# | ...
		// ... | ... | ... | ... | ...

	}

	public void killConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
