package project.group.cs157a;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentFrequency {

	public static HashMap<String, Integer> calculateDF(List<HashMap<String, Double>> tokensList) {
		HashMap<String, Integer> docFreq = new HashMap<>();
		
		for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {
			for (Map.Entry<String, Double> entry: tokensList.get(i).entrySet()) {
				String key = entry.getKey();
				
				// if key has not been counted yet, add it and count frequency
				if (!docFreq.containsKey(key)) {
					
					// At end of documents, key not found elsewhere
					if (i == ProjectMain.NUMBER_OF_FILES) {
						docFreq.put(key, 1);
					} else {
						docFreq.put(key, calculateFreq(i+1, key, tokensList));	
					}
				}
			}
		}
		
		return docFreq;
	}
	
	private static Integer calculateFreq(int startingLocation, String key, List<HashMap<String, Double>> maps) {
		int occurences = 1;
		for (int i = startingLocation; i < ProjectMain.NUMBER_OF_FILES; i++) {
			if (maps.get(i).containsKey(key)) {
				occurences++;
			}
		}
		
		return occurences;
	}

}
