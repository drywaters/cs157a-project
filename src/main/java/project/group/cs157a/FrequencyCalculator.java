package project.group.cs157a;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import project.group.cs157a.Token;

public class FrequencyCalculator implements Callable<HashMap<String, Double>> {

	private HashMap<String, Double> tokenFreqs = null;
	private HashMap<String, Integer> docFreqs = null;

	FrequencyCalculator(HashMap<String, Double> tokenFreqs, HashMap<String, Integer> docFreqs) {
		this.tokenFreqs = tokenFreqs;
		this.docFreqs = docFreqs;
	}

	@Override
	public HashMap<String, Double> call() throws Exception {
		double totalTokens = tokenFreqs.get("TOTAL TOKENS");
		tokenFreqs.remove("TOTAL TOKENS");
		for (Map.Entry<String, Double> entry : tokenFreqs.entrySet()) {
			if (!entry.getKey().equals(("DOCUMENT NUMBER"))) {
				tokenFreqs.put(entry.getKey(), (entry.getValue() / totalTokens) * Math.log(ProjectMain.NUMBER_OF_FILES / (double) docFreqs.get(entry.getKey())));
			}
		}

		return tokenFreqs;
	}

}
