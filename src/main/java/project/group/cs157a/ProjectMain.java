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
	public static final int NUMBER_OF_FILES = 3144;

	public static void main(String[] args) {

		long startTime = System.nanoTime();
		
		// Currently uses the number of files to set the number of threads open.  This
		// should change later as number of files increases
		ExecutorService executor = Executors.newFixedThreadPool(5);
				

		TokenizerStemmer tokenizer = new TokenizerStemmer();

		List<HashMap<String, Double>> tokenFreq = tokenizer.getTokens();
		
		// Calculate DF from list of token frequencies
		HashMap<String, Integer> documentFrequency = DocumentFrequency.calculateDF(tokenFreq);
		
		List<Future<HashMap<String, Double>>> futureValues = new ArrayList<>(NUMBER_OF_FILES);
		
		// Calculate Frequency for each Document
		Callable<HashMap<String, Double>> frequencyCalculators[] = new FrequencyCalculator[NUMBER_OF_FILES];
		for (int i = 0; i < NUMBER_OF_FILES; i++) {
			frequencyCalculators[i] = new FrequencyCalculator(tokenFreq.get(i), documentFrequency);
			futureValues.add(i, executor.submit(frequencyCalculators[i]));
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
		
//		CsvFileCreator csvCreator = new CsvFileCreator(tokenFreq);
		
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
