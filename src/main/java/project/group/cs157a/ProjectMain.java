package project.group.cs157a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProjectMain {

	public static final int NUMBER_OF_FILES = 7870;
//	public static final int NUMBER_OF_FILES = 50;

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		// Currently uses the number of files to set the number of threads open.  This
		// should change later as number of files increases
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		// Create a list to hold tokens returned, 10 threads that load files and token strings
		// and 10 future objects to hold the return values from the Callable (Tokenizer)
		List<HashMap<String, Integer>> tokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		Callable<HashMap<String, Integer>> tokenizers[] = new Tokenizer[NUMBER_OF_FILES];
		List<Future<HashMap<String, Integer>>> futureValues = new ArrayList<>(NUMBER_OF_FILES);

		// Create a new Callable for each file name and execute
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			tokenizers[i] = new Tokenizer(i+1);
			futureValues.add(executor.submit(tokenizers[i]));
		}

		// When thread finishes and returns a value, assign to ArrayList of HashMaps<String, Double>
		// that contain each token frequency for each document
		for (Future<HashMap<String, Integer>> tokens : futureValues) {
			try {
				tokenFreq.add(tokens.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// Future returned values for final frequency calculation
		List<Future<HashMap<String, Token>>> futureValues2 = new ArrayList<>(NUMBER_OF_FILES);

		// Calculate DF from list of token frequencies
		HashMap<String, Integer> documentFrequency = DocumentFrequency.calculateDF(tokenFreq);

		// Calculate Frequency for each Document
		List<HashMap<String, Token>> finalTokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		Callable<HashMap<String, Token>> frequencyCalculators[] = new FrequencyCalculator[NUMBER_OF_FILES];
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			frequencyCalculators[i] = new FrequencyCalculator(tokenFreq.get(i), documentFrequency);
			futureValues2.add(executor.submit(frequencyCalculators[i]));
		}
		
		for (Future<HashMap<String, Token>> tokens : futureValues2) {
			try {
				finalTokenFreq.add(tokens.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// Received all threads, shutdown
		executor.shutdown();
		long endTime = System.nanoTime();
		long elapsedTime = endTime-startTime;
		System.out.println("Total time for token calculation taken is: " + (double)(elapsedTime/1000000000.0));
		
//		CsvFileCreator csvCreator = new CsvFileCreator(finalTokenFreq);
		
		DatabaseConnector dc = new DatabaseConnector();
		dc.saveData(finalTokenFreq);
		
		endTime = System.nanoTime();
		elapsedTime = endTime-startTime;
		
		System.out.println("Total time taken is: " + (double)(elapsedTime/1000000000.0));
//		dc.printTFIDF();
		dc.killConnection();
	}

}
