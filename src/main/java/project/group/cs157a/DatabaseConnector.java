package project.group.cs157a;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			conn.setAutoCommit(false);
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
			String useDatabase = "USE " + DATABASE_NAME;
			String drop = "DROP TABLE IF EXISTS project";
			String table = "CREATE TABLE project (doc_id INTEGER, token VARCHAR(255) BINARY,"
					+ " tfidf DECIMAL(20, 15) ," + "PRIMARY KEY (doc_id, token))";

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

	private void insertData(List<HashMap<String, Double>> freq) {
		// Insert the calculated table data into the correct column
		// Should look like ex.
		// doc_id | token | tf | idf | tfidf
		// ----------------------------------
		// 1 | token1 | 1.23 | 2.23| 0.223
		// 1 | token2 | 1.23 | 2.23| 0.223
		// 2 | token1 | 1.23 | 2.23| 0.223

		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO project VALUES (?, ?, ?)")) {
			int count = 0;

			for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {

				int docId = freq.get(i).get("DOCUMENT NUMBER").intValue();
				freq.get(i).remove("DOCUMENT NUMBER");
				for (Map.Entry<String, Double> entry : freq.get(i).entrySet()) {
					ps.setInt(1, docId);
					ps.setString(2, entry.getKey());
					ps.setDouble(3, entry.getValue());

					ps.addBatch();

					if (count++ == BATCH_SIZE) {
						ps.executeBatch();
						ps.clearBatch();
						count = 0;
					}
				}
			}

			if (count > 0) {
				ps.executeBatch();
			}

			conn.commit();

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

	public void saveData(List<HashMap<String, Double>> tokenFreq) {
		createDatabase();
		createTable();
		insertData(tokenFreq);
	}

	// Generates a list of keywords by querying the project table. Anything not
	// in the list is considered a stopword.
	public ArrayList<Token> generateKeywordList(double gap) {
		// Array list of Keywords
		ArrayList<Token> keywords = new ArrayList<Token>();

		// Obtain the keywords that have a tfidf higher than the gap. All words
		// under it are considered stop words
		try {
			String select = "SELECT * FROM project WHERE tfidf > " + gap + " ORDER BY tfidf DESC";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(select);

			while (rs.next()) {
				Integer id = Integer.parseInt(rs.getString("doc_id"));
				String strToken = rs.getString("token");

				Token token = new Token(id, strToken);
				keywords.add(token);
			}

		} catch (SQLException e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		}

		return keywords;
	}

	// Create the 1-Concept table
	private void create1ConceptTable(ArrayList<Token> keywords) {
		// Create the 1 Concepts table
		// Drops 1-concept table, if it already existed
		Statement st = null;
		try {
			st = conn.createStatement();
			String useDatabase = "USE " + DATABASE_NAME;
			String drop = "DROP TABLE IF EXISTS 1Concepts";

			Set<String> uniqueKeywords = new HashSet<>();

			String table = "CREATE TABLE 1Concepts (doc_id INT, ";
			for (Token keyword : keywords) {
				if (!uniqueKeywords.contains(keyword.getWord())) {
					table += "`" + keyword.getWord() + "` INT DEFAULT 0, ";
					uniqueKeywords.add(keyword.getWord());
				}
			}
			table += "PRIMARY KEY (doc_id))";

			st.executeUpdate(useDatabase);
			st.executeUpdate(drop);
			st.executeUpdate(table);

			System.out.println("Successfully created table (1 Concepts)");
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

	// Inserts the data into the 1 Concept table
	// Should look like ex.
	// doc_id | token | token 2| token 3 | token 4
	// ---------------------------------------------
	// 1 | 1 | 0 | 1 | 0
	// 2 | 1 | 1 | 1 | 1
	// 3 | 1 | 1 | 0 | 0
	private void insert1Concepts(ArrayList<Token> keywords, double gap) {
		try {
			Statement statement = conn.createStatement();
			for (int i = 0; i < keywords.size(); i++) {
				int docId = keywords.get(i).getDocid();
				String keyword = keywords.get(i).getWord();
				statement.execute("INSERT INTO 1Concepts (doc_id, `" + keyword + "`) VALUES (" + docId
						+ ", 1) ON DUPLICATE KEY UPDATE `" + keyword + "` = 1");
			}

			conn.commit();
		} catch (SQLException e) {
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
		}
	}

	public void save1Concepts(double gap, ArrayList<Token> keywords) {
		create1ConceptTable(keywords);
		insert1Concepts(keywords, gap);
	}

	// Counts pairs of keywords found in the 1 Concept table and returns a hashmap of them
	private HashMap<String, Integer> get2Concepts(ArrayList<Token> keywords) {
		HashMap<String, Integer> pairFrequencies = new HashMap<>();

		try {
			String select = "SELECT * FROM 1Concepts";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(select);
			while (rs.next()) {
				String value;
				String keyword;
				HashSet<String> keywordsInDocument = new HashSet<>();
				
				for (int i = 0; i < keywords.size(); i++) {
					keyword = keywords.get(i).getWord();
					value = rs.getString(keyword);
					if(Integer.parseInt(value) == 1){
						keywordsInDocument.add(keyword);
					}
				}

				for(String word1: keywordsInDocument){
					for(String word2: keywordsInDocument){
						if(!word1.equals(word2)){
							String pair = word1 + "," + word2;
							if (pairFrequencies.containsKey(pair)) {
								pairFrequencies.put(pair, pairFrequencies.get(pair) + 1);
							} else {
								pairFrequencies.put(pair, 1);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An error occured when attempting to select the table: " + e.getMessage());
			e.printStackTrace();
		}

		return pairFrequencies;
	}

	// Create the 2-Concept table
	private void create2ConceptTable() {
		// Drops 2-Concept table, if it already existed
		Statement st = null;
		try {
			st = conn.createStatement();
			String useDatabase = "USE " + DATABASE_NAME;
			String drop = "DROP TABLE IF EXISTS 2Concepts";

			String table = "CREATE TABLE 2Concepts (pair VARCHAR(255), frequency INT, PRIMARY KEY (pair))";

			st.executeUpdate(useDatabase);
			st.executeUpdate(drop);
			st.executeUpdate(table);

			System.out.println("Successfully created table (2 Concepts)");
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

	// Uses the hashmap generated from get2Concepts() to insert data into the 2Concepts table
	private void insert2Concepts(HashMap<String, Integer> pairs) {
		try (PreparedStatement ps = conn.prepareStatement("INSERT INTO 2Concepts VALUES (?, ?)")) {
			int count = 0;

			for (Map.Entry<String, Integer> entry : pairs.entrySet()) {
				ps.setString(1, entry.getKey());
				ps.setInt(2, entry.getValue());

				ps.addBatch();

				if (count++ == BATCH_SIZE) {
					ps.executeBatch();
					ps.clearBatch();
					count = 0;
				}
			}

			if (count > 0) {
				ps.executeBatch();
			}

			conn.commit();

		} catch (SQLException e) {
			System.out.println("Ran into an unexpected error when inserting Data: " + e.getMessage());
		}
	}
	
	public void save2Concepts(ArrayList<Token> keywords){
		create2ConceptTable();
		HashMap<String, Integer> pairs = get2Concepts(keywords);
		insert2Concepts(pairs);
	}
}