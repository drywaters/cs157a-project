package project.group.cs157a;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

	private Connection conn = null;
	private Connection DBconn = null;
	private static final String DATABASE_NAME = "cs157a_project";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "";
	private static final String DB_URL = "jdbc:mysql://localhost";

	// Create a connection
	DatabaseConnector() {
		try {
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			DBconn = DriverManager.getConnection(DB_URL + "/" + DATABASE_NAME, USER_NAME, PASSWORD);
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

	public void createTable() {
		// Create Table using Set of column names (HashSet)
		// Drops Project, the table, if it already existed
		try{
			Statement st = DBconn.createStatement();
			String drop = "DROP TABLE IF EXISTS Project";
			String table = "CREATE TABLE Project " +
				       		"(id INTEGER, " +
				       		"strToken char(255), " +
				       		"TFiDF INTEGER, " +
				       		"PRIMARY KEY (id))";
			st.executeUpdate(drop);
			st.executeUpdate(table);
			System.out.println("Successfully created table");
		}
		catch(SQLException e){
			System.out.println("Unable to create table: " + e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Unable to create table: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void insertData(int id, String strToken, int TFiDF) {
		// Insert the calculated table data into the correct column
		// Should look like ex.
		// DocId | Token1 | Token2 | Token 3 | ...
		// ----------------------------------------------------------------
		// 1 | TFiDF# | TFiDF# | TFiDF# | ...
		// 2 | TFiDF# | TFiDF# | TFiDF# | ...
		// ... | ... | ... | ... | ...
		try{
			String insert = "INSERT INTO Project" + "(id, strToken, TFiDF)" +
							"VALUES (" + id + ", '" + strToken + "', " + TFiDF + ")";
			Statement st = DBconn.createStatement();
			st.executeUpdate(insert);
		}
		catch(SQLException e){
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void getTable()	{
		// Prints out the table
		try{
			String select = "SELECT * FROM Project";
			Statement st = DBconn.createStatement();
			ResultSet rs = st.executeQuery(select);
			while (rs.next()){
				int id = rs.getInt("id");
				String strToken = rs.getString("strToken");
				int TFiDF = rs.getInt("TFiDF");
				System.out.format("%s, %s, %s\n", id, strToken, TFiDF);
			}
		}
		catch(SQLException e){
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e){
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		}
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
