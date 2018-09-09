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

		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<String[]> tokensList = new ArrayList<>(10);
		Callable<String[]>[] tokenizers = new Tokenizer[10];
		List<Future<String[]>> futureValues = new ArrayList<>(10);

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

		// Make sure all threads are there.
		for (String[] tokens : tokensList) {
			System.out.println(Arrays.toString(tokens));
		}

	}

}
