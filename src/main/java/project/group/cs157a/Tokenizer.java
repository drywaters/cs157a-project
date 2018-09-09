package project.group.cs157a;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Tokenizer implements Callable<HashMap<String, Double>> {
	
	private String filename = "";
	
	Tokenizer(String filename) {
		this.filename = filename;
	}

	@Override
	public HashMap<String, Double> call() throws Exception {
		
		HashMap<String, Double> tokenFrequency = new HashMap<>();
		
		try (InputStream modelIn = new FileInputStream("./lib/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer = new TokenizerME(model);
			try (InputStream file = new FileInputStream("./files/" + filename)) {
				String content = IOUtils.toString(file, Charset.defaultCharset());
				String[] tokens = tokenizer.tokenize(content);
				
				for (int i = 0; i < tokens.length; i++) {
					int freq = 0;
					if (tokens[i] != "") {
						for (int j = i + 1; j < tokens.length; j++) {
							if (tokens[i] == tokens[j]) {
								freq++;
								tokens[j] = "";
							}
						}
						tokenFrequency.put(tokens[i], (double)(freq / tokens.length));
					}
				}
				
				
				file.close();
				modelIn.close();
				
				return tokenFrequency;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
