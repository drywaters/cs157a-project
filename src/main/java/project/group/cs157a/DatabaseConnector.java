package project.group.cs157a;

import java.math.BigDecimal;
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
	private static final String PASSWORD = "password";
	private static final String DB_URL = "jdbc:mysql://localhost?rewriteBatchedStatements=true";
	private static final int BATCH_SIZE = 1000;

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
			String table = "CREATE TABLE project " + "(doc_id INTEGER, " + "token VARCHAR(255) BINARY, "
					+ "tfidf DECIMAL(20, 15), " + "PRIMARY KEY (doc_id, token))";

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

	private void insertData(HashMap<String, Double> freq) {
		// Insert the calculated table data into the correct column
		// Should look like ex.
		// DocId | Token | TFiDF #
		// -------------------------
		// 1 | token1 | 0.223
		// 1 | token2 | 1.230
		// 2 | token1 | 0.234
		int documentId = (int) Math.floor(freq.get("DOCUMENT NUMBER"));
		int count = 0;
		freq.remove("DOCUMENT NUMBER");
		
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO project VALUES (?, ?, ?)");
			for (Map.Entry<String, Double> entry : freq.entrySet()) {
				ps.setInt(1,  documentId);
				ps.setString(2, entry.getKey());
				ps.setDouble(3,  entry.getValue());
				ps.addBatch();
				if(++count % BATCH_SIZE == 0){
					ps.executeBatch();
				}
			}
			ps.executeBatch();
		} catch (SQLException e) {
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					System.out.println("Unable to close statement with error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void getTable() {
		// Prints out the table
		try {
			String select = "SELECT * FROM project";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(select);
			while (rs.next()) {
				int id = rs.getInt("doc_id");
				String strToken = rs.getString("token");
				double tfidf = rs.getInt("tfidf");
				System.out.format("%s, %s, %s\n", id, strToken, tfidf);
			}
		} catch (SQLException e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
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
				String tfidf = rs.getString("tfidf");
				System.out.format("%s, %s, %s\n", id, strToken, tfidf);
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

	public void saveData(List<HashMap<String, Double>> tokenFreq) {
		createDatabase();
		createTable();
		for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {
			insertData(tokenFreq.get(i));
		}
	}
}