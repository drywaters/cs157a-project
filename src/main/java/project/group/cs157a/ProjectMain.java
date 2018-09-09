package project.group.cs157a;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProjectMain {
	
	private static final int NUMBER_OF_FILES = 10;

	public static void main(String[] args) {
		
		
//		DatabaseConnector dc = new DatabaseConnector();
//		dc.createDatabase();
//		dc.killConnection();
//		
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<String[]>> list = new ArrayList<>(10);
		
		Callable<String[]>[] tokenizer = new Tokenizer[10];
		
		for (int i = 1; i < NUMBER_OF_FILES; i++) {
			Future<String[]> future = executor.submit(new Tokenizer("Data(" + i + ").txt"));
			list.add(future);
		}
		

	}

}
