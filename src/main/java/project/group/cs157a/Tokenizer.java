package project.group.cs157a;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Tokenizer implements Callable<String[]> {
	
	private String filename = "";
	
	Tokenizer(String filename) {
		this.filename = filename;
	}

	@Override
	public String[] call() throws Exception {
		try (InputStream modelIn = new FileInputStream("./lib/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			TokenizerME tokenizer = new TokenizerME(model);
			try (InputStream file = new FileInputStream("./files/" + filename)) {
				String content = IOUtils.toString(file, Charset.defaultCharset());
				String[] tokens = tokenizer.tokenize(content);
				file.close();
				modelIn.close();
				return tokens;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
