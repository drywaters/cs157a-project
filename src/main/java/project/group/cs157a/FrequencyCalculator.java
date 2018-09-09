package project.group.cs157a;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class FrequencyCalculator implements Callable<HashMap<String, Double>>{
	
	private HashMap<String, Double> tokenFreqs = null;
	private HashMap<String, Double> docFreqs = null;
	
	FrequencyCalculator(HashMap<String,Double> tokenFreqs, HashMap<String, Double> docFreqs) {
		this.tokenFreqs = tokenFreqs;
		this.docFreqs = docFreqs;
	}

	@Override
	public HashMap<String, Double> call() throws Exception {
		for (Map.Entry<String, Double> entry: tokenFreqs.entrySet()) {
			entry.setValue(entry.getValue() * (ProjectMain.NUMBER_OF_FILES / docFreqs.get(entry.getKey())));
		}
		
		return tokenFreqs;
	}

}
