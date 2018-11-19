package project.group.cs157a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import project.group.cs157a.Token;

public class ConceptCreator {
	
	private Set<String> keywords;
	
	public ConceptCreator(List<HashMap<String,Double>> maps) {
		List<Token> allWords = createWordList(maps);
		List<Token> filteredWords = filteredKeywords(allWords);
		this.keywords = findKeywords(filteredWords);
		System.out.println(this.keywords.size());
	}
	
	private List<Token> createWordList(List<HashMap<String, Double>> maps) {
		List<Token> allWords = new ArrayList<>();
		for (int i = 0; i < maps.size(); i++) {
			for (Map.Entry<String, Double> entry: maps.get(i).entrySet()) {
				allWords.add(new Token(i + 1, entry.getKey(), entry.getValue()));
			}
		}
		
		allWords.sort((o1, o2) -> Double.compare(o2.getTfidf(), o1.getTfidf()));
		
		return allWords;
	}
	
	private List<Token> filteredKeywords(List<Token> allWords) {
		double maxDiff = 0.0;
		int location = 0;
				
//		for (int i = 1717; i < allWords.size(); i++) {
//			if (Math.abs(allWords.get(i-1).getTfidf() - allWords.get(i).getTfidf()) > maxDiff) {
//				maxDiff = Math.abs(allWords.get(i-1).getTfidf() - allWords.get(i).getTfidf());
//				location = i;
//			}
//		}

		return allWords.subList(0, 1717);
	}
	
	private Set<String> findKeywords (List<Token> filteredWords) {
		
		Set<String> keyWords = new HashSet<>();
		for (int i = 0 ; i < filteredWords.size(); i++) {
			if (!keyWords.contains(filteredWords.get(i).getWord())) {
				keyWords.add(filteredWords.get(i).getWord());
			}
		}
		
		return keyWords;
	}
	

}
