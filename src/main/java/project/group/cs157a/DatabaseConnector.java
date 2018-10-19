package project.group.cs157a;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

	private Connection conn = null;
	private static final String DATABASE_NAME = "cs157a_project";
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "";
	private static final String DB_URL = "jdbc:mysql://localhost?rewriteBatchedStatements=true";
	private static final int BATCH_SIZE = 30000;

	// Create a connection
	DatabaseConnector() {
		try {
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		}
	}

	// Create Database with name DATABASE_NAME
	private void createDatabase() {
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
			System.out
					.println("Unable to create database with name " + DATABASE_NAME + " with error: " + e.getMessage());
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
		// Drops project, the table, if it already existed
		Statement st = null;
		try {
			st = conn.createStatement();
			String useDatabase = "USE cs157a_project";
			String drop = "DROP TABLE IF EXISTS project";
			String table = "CREATE TABLE project (doc_id INTEGER, token VARCHAR(255) BINARY,"
					+ "tf DECIMAL(20, 15), idf DECIMAL(20,15), tfidf DECIMAL(20, 15), "
					+ "PRIMARY KEY (doc_id, token))";

			st.executeUpdate(useDatabase);
			st.executeUpdate(drop);
			st.executeUpdate(table);

			System.out.println("Successfully created table");
		} catch (SQLException e) {
			System.out.println("Unable to create table: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unable to create table: " + e.getMessage());
			e.printStackTrace();
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

	private void insertData(List<HashMap<String, Token>> freq) {
		// Insert the calculated table data into the correct column
		// Should look like ex.
		// doc_id | token | tf | idf | tfidf
		// ----------------------------------
		// 1 | token1 | 1.23 | 2.23| 0.223
		// 1 | token2 | 1.23 | 2.23| 0.223
		// 2 | token1 | 1.23 | 2.23| 0.223

		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO project VALUES (?, ?, ?, ?, ?)")) {
			int count = 0;
			Token tempToken = null;
			
			for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {
				int documentId = freq.get(i).get("DOCUMENT NUMBER").getDocID();
				freq.get(i).remove("DOCUMENT NUMBER");
				for (Map.Entry<String, Token> entry : freq.get(i).entrySet()) {
					tempToken = entry.getValue();
					ps.setInt(1, documentId);
					ps.setString(2, tempToken.getWord());
					ps.setDouble(3, tempToken.getTf());
					ps.setDouble(4, tempToken.getIdf());
					ps.setDouble(5, tempToken.getTfidf());

					ps.addBatch();
					count++;
					
					if (count % BATCH_SIZE == 0) {
						ps.executeBatch();
						count = 0;
					}
				}
			}
			
			if (count != 0) {
				ps.executeBatch();
			}
		} catch (SQLException e) {
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
		}
	}

	public void printTFIDF() {
		// Prints out the table
		try {
			String select = "SELECT * FROM project ORDER BY tfidf DESC";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(select);
			while (rs.next()) {
				String id = rs.getString("doc_id");
				String strToken = rs.getString("token");
				String tf = rs.getString("tf");
				String idf = rs.getString("idf");
				String tfidf = rs.getString("tfidf");
				System.out.format("%s, %s, %s, %s, %s\n", id, strToken, tf, idf, tfidf);
			}
		} catch (SQLException e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void killConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			System.out.println("Unable to kill DB connection");
			e.printStackTrace();
		}
	}

	public void saveData(List<HashMap<String, Token>> tokenFreq) {
		createDatabase();
		createTable();
		insertData(tokenFreq);
	}
}