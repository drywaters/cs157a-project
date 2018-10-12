package project.group.cs157a;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class Tokenizer implements Callable<HashMap<String, Integer>> {

	private int fileNumber;
	private int totalTokens;
	private HashMap<String, Integer> tokens;
	private StringBuffer tokenBuffer;

	Tokenizer(int fileNumber) {
		this.fileNumber = fileNumber;
		this.totalTokens = 0;
		this.tokenBuffer = new StringBuffer(10);
	}

	@Override
	public HashMap<String, Integer> call() throws Exception {

		tokens = new HashMap<>();
		tokens.put("TOTAL TOKENS", 0);
		tokens.put("DOCUMENT NUMBER", this.fileNumber);

		try (InputStream file = new FileInputStream("./sql/doc" + this.fileNumber + ".txt")) {
//		try (InputStream file = new FileInputStream("./files/Data_" + this.fileNumber + ".txt")) {
//		try (InputStream file = new FileInputStream("./tokenTestData/TT20")) {
			String content = IOUtils.toString(file, Charset.defaultCharset());
			
			for (int i = 0; i < content.length(); i++) {
				
				char currentChar = content.charAt(i);
				int currentCharValue = (int) currentChar;
				
				if (currentCharValue > 96 && currentCharValue < 123) {  // Lower case letter
					tokenBuffer.append(currentChar);
				}  else if (currentCharValue > 64 && currentCharValue < 91) { 	// Capital letter
					tokenBuffer.append(currentChar);
				} else { 
					nonAlphabet();
				}
			}
			
			tokens.put("TOTAL TOKENS", totalTokens);

			file.close();
			return tokens;
		}
	}
	
	// clear buffer, add token that is there
	// if character is not A-Za-z
	private void nonAlphabet() {
		checkBuffer();
	}
	
	private void checkBuffer() {
		if (tokenBuffer.length() > 0) {
			addToken();
		}
	}
	
	private void addToken() {
		this.totalTokens++;
		if (tokenExists()) {
			int currentOccur = tokens.get(tokenBuffer.toString());
			tokens.replace(tokenBuffer.toString(), currentOccur + 1);
		} else {
			tokens.put(tokenBuffer.toString(), 1);
		}
		clearBuffer();
	}
	
	private void clearBuffer() {
		tokenBuffer.delete(0,  tokenBuffer.length());
	}
	
	private boolean tokenExists() {
		return tokens.containsKey(tokenBuffer.toString());
	}
	
}