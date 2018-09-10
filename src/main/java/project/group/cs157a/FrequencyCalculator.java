package project.group.cs157a;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FrequencyCalculator implements Callable<HashMap<String, Double>>{
	
	private HashMap<String, Integer> tokenFreqs = null;
	private HashMap<String, Integer> docFreqs = null;
	private HashMap<String, Double> finalFreqs = null;
	
	FrequencyCalculator(HashMap<String, Integer> tokenFreqs, HashMap<String, Integer> docFreqs) {
		this.tokenFreqs = tokenFreqs;
		this.docFreqs = docFreqs;
		finalFreqs = new HashMap<String, Double>();
	}

	@Override
	public HashMap<String, Double> call() throws Exception {
		for (Map.Entry<String, Integer> entry: tokenFreqs.entrySet()) {
			finalFreqs.put(entry.getKey(), (double) (entry.getValue() * Math.log(ProjectMain.NUMBER_OF_FILES / (double) docFreqs.get(entry.getKey()))));
		}
		
		return finalFreqs;
	}

}
