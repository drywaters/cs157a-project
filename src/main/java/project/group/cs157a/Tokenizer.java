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

public class Tokenizer implements Callable<HashMap<String, Integer>> {

	private int fileNumber = 0;
	private int totalTokens = 0;
	private HashMap<String, Integer> tokens;
	private StringBuffer token;

	Tokenizer(int fileNumber) {
		this.fileNumber = fileNumber;
	}

	@Override
	public HashMap<String, Integer> call() throws Exception {

		tokens = new HashMap<>();
		tokens.put("TOTAL TOKENS", 0);
		tokens.put("DOCUMENT NUMBER", this.fileNumber);

		try (InputStream file = new FileInputStream("./files/Data(" + this.fileNumber + ").txt")) {
			String content = IOUtils.toString(file, Charset.defaultCharset());
			
			for (int i = 0; i < content.length(); i++) {
				int currentChar = content.charAt(i);
				
				// First group of special characters
				if (currentChar > 32 && currentChar < 48) {
					addToken(currentChar);
				} else if (currentChar > 47 && currentChar < 57) {  // Number
					token.append(currentChar);
				} else if (currentChar > 57 && currentChar < 65) { 	// Second group of special characters
					addToken();
				} else if (currentChar > 64 && currentChar < 91) {  // Capital letter
					token.append(currentChar);
				} else if (currentChar > 90 && currentChar < 97) {  // 3rd group of special characters
					addToken();
				} else if (currentChar > 96 && currentChar < 123) { // Lower case letter
					token.append(currentChar);
				} else if (currentChar > 122 && currentChar < 127) { // 4th group of special characters
					addToken();
				} else {
					if (token.length() > 0) {
						addToken();
					} else {
						continue;
					}
				}
			}

			file.close();

			return tokens;
		}
	}
	
	// add special character to tokens
	public void addToken(int token) {
		this.totalTokens++;
		tokens.replace("TOTAL TOKENS", totalTokens);
	}
	
	// add whatever is in buffer not adding a character
	public void addToken() {
		
	}
}
