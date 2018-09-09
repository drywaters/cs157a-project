package project.group.cs157a;

import java.util.ArrayList;

public class ProjectMain {
	
	private static final int NUMBER_OF_FILES = 10;

	public static void main(String[] args) {
		
		
//		DatabaseConnector dc = new DatabaseConnector();
//		dc.createDatabase();
//		dc.killConnection();
//		
		Tokenizer[] tokenizers = new Tokenizer[NUMBER_OF_FILES];
		ArrayList<String[]> tokens = new ArrayList<>(10);
		
		for (int i = 1; i < NUMBER_OF_FILES; i++) {
			tokenizers[i] = new Tokenizer("Data(" + i + ").txt");
			try {
				tokens.add(tokenizers[i].call());
				System.out.println(tokens);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
