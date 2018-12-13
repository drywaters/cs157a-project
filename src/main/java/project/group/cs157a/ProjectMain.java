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

//	public static final int NUMBER_OF_FILES = 7870;
	public static final int NUMBER_OF_FILES = 10;

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		// Currently uses the number of files to set the number of threads open.  This
		// should change later as number of files increases
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		// Create a list to hold tokens returned, 10 threads that load files and token strings
		// and 10 future objects to hold the return values from the Callable (Tokenizer)
		List<HashMap<String, Double>> tokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		
		// Use actual words or stemmed words
//		Callable<HashMap<String, Integer>> tokenizers[] = new Tokenizer[NUMBER_OF_FILES];		
		Callable<HashMap<String, Double>> tokenizers[] = new TokenizerStemmer[NUMBER_OF_FILES];
		
		List<Future<HashMap<String, Double>>> futureValues = new ArrayList<>(NUMBER_OF_FILES);

		// Create a new Callable for each file name and execute
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
//			tokenizers[i] = new Tokenizer(i+1);
			tokenizers[i] = new TokenizerStemmer(i+1);
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

		// Calculate DF from list of token frequencies
		HashMap<String, Integer> documentFrequency = DocumentFrequency.calculateDF(tokenFreq);
		tokenizers = null;

		// Calculate Frequency for each Document
		Callable<HashMap<String, Double>> frequencyCalculators[] = new FrequencyCalculator[NUMBER_OF_FILES];
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			frequencyCalculators[i] = new FrequencyCalculator(tokenFreq.get(i), documentFrequency);
			futureValues.set(i, executor.submit(frequencyCalculators[i]));
		}
		
		for (int i = 0; i < NUMBER_OF_FILES; i++) {

			try {
				tokenFreq.set(i, futureValues.get(i).get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// Received all threads, shutdown
		executor.shutdown();
		long endTime = System.nanoTime();
		long elapsedTime = endTime-startTime;
		System.out.println("Total time for token calculation taken is: " + (double)(elapsedTime/1000000000.0));
		
		DatabaseConnector dc = new DatabaseConnector();
		dc.saveData(tokenFreq);
		
		// Saves the 1-concept table using an arbitrary gap as the separator between keywords and stopwords
		ArrayList<Token> keywords = dc.generateKeywordList(0.01);
		dc.save1Concepts(0.01, keywords);
		
		// Saves the 2-Concept Table
		dc.save2Concepts(keywords);

		endTime = System.nanoTime();
		elapsedTime = endTime-startTime;
		
		System.out.println("Total time taken is: " + (double)(elapsedTime/1000000000.0));
//		dc.printTFIDF();
		dc.killConnection();
		
	}

}
