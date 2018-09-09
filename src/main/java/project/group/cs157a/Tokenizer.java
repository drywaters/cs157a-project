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
		try (InputStream modelIn = new FileInputStream("./lib/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer = new TokenizerME(model);
			try (InputStream file = new FileInputStream("./files/" + filename)) {
				String content = IOUtils.toString(file, Charset.defaultCharset());
				String[] tokens = tokenizer.tokenize(content);
				file.close();
				modelIn.close();
				
				HashMap<String, Double> testData = new HashMap<>();
				if (Math.random() > 0.5) {
					testData.put("test", 1.0);	
				}
				if (Math.random() > 0.5) {
					if (Math.random() > 0.5) {
						testData.put("tested", 5.0);	
					} else {
						testData.put("tested", 2.0);
					}
				} 
				
				testData.put("tests", 4.0);
				testData.put("tester", 3.0);
				testData.put("testy", 2.0);
				return testData;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
