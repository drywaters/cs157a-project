package project.group.cs157a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProjectMain {

	public static final int NUMBER_OF_FILES = 10;

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
		List<HashMap<String, Double>> tokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		Callable<HashMap<String, Double>> tokenizers[] = new Tokenizer[NUMBER_OF_FILES];
		List<Future<HashMap<String, Double>>> futureValues = new ArrayList<>(NUMBER_OF_FILES);

		// Create a new Callable for each file name and execute
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			tokenizers[i] = new Tokenizer("Data(" + (i + 1) + ").txt");
			futureValues.add(executor.submit(tokenizers[i]));
		}

		// When thread finishes and returns a value, assign to ArrayList of HashMaps<String, Double>
		// that contain each token frequency for each document
		for (Future<HashMap<String, Double>> tokens : futureValues) {
			try {
				tokenFreq.add(tokens.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		List<Future<HashMap<String, Double>>> futureValues2 = new ArrayList<>(NUMBER_OF_FILES);

		// Calculate DF from list of token frequencies
		HashMap<String, Double> documentFrequency = DocumentFrequency.calculateDF(tokenFreq);

		// Calculate Frequency for each Document
		List<HashMap<String, Double>> finalTokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		Callable<HashMap<String, Double>> frequencyCalculators[] = new FrequencyCalculator[NUMBER_OF_FILES];
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			frequencyCalculators[i] = new FrequencyCalculator(tokenFreq.get(i), documentFrequency);
			futureValues2.add(executor.submit(frequencyCalculators[i]));
		}
		
		for (Future<HashMap<String, Double>> tokens : futureValues2) {
			try {
				finalTokenFreq.add(tokens.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// Received all threads, shutdown
		executor.shutdown();
		
		// Past document frequency hashmap and finalTokeFreq to database connector
		
	}

}
