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
	public static final int NUMBER_OF_FILES = 55;

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		// Set the number of threads to run concurrently.
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		// Create a list to hold words returned, threads load files and returns stems of words
		List<HashMap<String, Integer>> tokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		
		// Use Tokenizer if you want actual words
		// Use TokenizerStemmer if you want stemmed words
//		Callable<HashMap<String, Integer>> tokenizers[] = new Tokenizer[NUMBER_OF_FILES];		
		Callable<HashMap<String, Integer>> tokenizers[] = new TokenizerStemmer[NUMBER_OF_FILES];
		
		// Future Interface holds the values that are returned from the tokenizer
		List<Future<HashMap<String, Integer>>> futureValues = new ArrayList<>(NUMBER_OF_FILES);

		// Create a new Callable for each file name and execute
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			
			// Use Tokenizer if you want actual words
			// Use TokenizerStemmer if you want stemmed words
//			tokenizers[i] = new Tokenizer(i+1);
			tokenizers[i] = new TokenizerStemmer(i+1);
			futureValues.add(executor.submit(tokenizers[i]));
		}

		// When thread finishes and returns a value, assign to ArrayList of HashMaps<String, Integer>
		// that contain how many times each word is found in a document
		// and the total words/stems for each document
		for (Future<HashMap<String, Integer>> tokens : futureValues) {
			try {
				tokenFreq.add(new HashMap<String, Integer> (tokens.get()));
				tokens.get().clear();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		tokenizers = null;
		
		// Future returned values for final frequency calculation that 
		// contains an Map of Word/Stem and the TFIDF for each document
		List<Future<HashMap<String, Token>>> futureValues2 = new ArrayList<>(NUMBER_OF_FILES);

		// Calculate DF from list of word/stem frequencies
		HashMap<String, Integer> documentFrequency = DocumentFrequency.calculateDF(tokenFreq);

		// Calculate final TFIDF Frequency for each Document
		List<HashMap<String, Token>> finalTokenFreq = new ArrayList<>(NUMBER_OF_FILES);
		Callable<HashMap<String, Token>> frequencyCalculators[] = new FrequencyCalculator[NUMBER_OF_FILES];
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			frequencyCalculators[i] = new FrequencyCalculator(new HashMap<String, Integer>(tokenFreq.get(i)), documentFrequency);
			futureValues2.add(executor.submit(frequencyCalculators[i]));
			tokenFreq.get(i).clear();
		}
		
		for (Future<HashMap<String, Token>> tokens : futureValues2) {
			try {
				finalTokenFreq.add(new HashMap<String, Token> (tokens.get()));
				tokens.get().clear();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// Received all threads, shutdown
		executor.shutdown();
		long endTime = System.nanoTime();
		long elapsedTime = endTime-startTime;
		System.out.println("Total time for token calculation taken is: " + (double)(elapsedTime/1000000000.0));
		
		
		// Use CsvFileCreator if you want to save to local file
		// Use DatabaseConnect if you want to save to DB
		
		CsvFileCreator csvCreator = new CsvFileCreator(finalTokenFreq);
//		DatabaseConnector dc = new DatabaseConnector();
//		dc.saveData(finalTokenFreq);

		endTime = System.nanoTime();
		elapsedTime = endTime-startTime;
		
		System.out.println("Total time taken is: " + (double)(elapsedTime/1000000000.0));
//		dc.printTFIDF();
//		dc.killConnection();
	}

}
