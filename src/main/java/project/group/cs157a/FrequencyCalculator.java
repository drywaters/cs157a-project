package project.group.cs157a;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import project.group.cs157a.Token;

public class FrequencyCalculator implements Callable<HashMap<String, Token>> {

	private HashMap<String, Integer> tokenFreqs = null;
	private HashMap<String, Integer> docFreqs = null;
	private HashMap<String, Token> finalFreqs = null;

	FrequencyCalculator(HashMap<String, Integer> tokenFreqs, HashMap<String, Integer> docFreqs) {
		this.tokenFreqs = tokenFreqs;
		this.docFreqs = docFreqs;
		finalFreqs = new HashMap<String, Token>();
	}

	@Override
	public HashMap<String, Token> call() throws Exception {
		int documentID = tokenFreqs.get("DOCUMENT NUMBER");
		int totalTokens = tokenFreqs.get("TOTAL TOKENS");
		finalFreqs.put("DOCUMENT NUMBER", new Token(documentID));
		tokenFreqs.remove("DOCUMENT NUMBER");
		tokenFreqs.remove("TOTAL TOKENS");
		for (Map.Entry<String, Integer> entry : tokenFreqs.entrySet()) {
		
			finalFreqs.put(entry.getKey(), new Token(documentID, entry.getKey()));
			Token token = finalFreqs.get(entry.getKey());
			token.setTf(entry.getValue() / (double) totalTokens);
			token.setIdf(Math.log(ProjectMain.NUMBER_OF_FILES / (double) docFreqs.get(entry.getKey())));
			token.setTfidf(token.getTf() * token.getIdf());
		}

		return finalFreqs;
	}

}
