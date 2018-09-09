package project.group.cs157a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProjectMain {

	private static final int NUMBER_OF_FILES = 10;

	public static void main(String[] args) {

		// Commented out DB connection for testing concurrent token loading
//		DatabaseConnector dc = new DatabaseConnector();
//		dc.createDatabase();
//		dc.killConnection();
//		

		// Currently uses the number of files to set the number of threads open.  This
		// should change later as number of files increases
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_FILES);
		
		// Create a list to hold tokens returned, 10 threads that load files and token strings
		// and 10 future objects to hold the return values from the Callable (Tokenizer)
		List<String[]> tokensList = new ArrayList<>(NUMBER_OF_FILES);
		Callable<String[]>[] tokenizers = new Tokenizer[NUMBER_OF_FILES];
		List<Future<String[]>> futureValues = new ArrayList<>(NUMBER_OF_FILES);

		// Create a new Callable for each file name and execute
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			tokenizers[i] = new Tokenizer("Data(" + (i + 1) + ").txt");
			futureValues.add(i, executor.submit(tokenizers[i]));
		}

		// When thread finishes and returns a value, assign to ArrayList of tokens
		// (String[])
		for (Future<String[]> tokens : futureValues) {
			try {
				tokensList.add(tokens.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// Received all threads, shutdown
		executor.shutdown();

		// Print all tokens to screen.  Need to add calculate TFiDF here. (New class?)
		for (String[] tokens : tokensList) {
			System.out.println(Arrays.toString(tokens));
		}

	}

}
