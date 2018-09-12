package project.group.cs157a;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class Tokenizer implements Callable<HashMap<String, Integer>> {

	private int fileNumber = 0;
	private int totalTokens = 0;
	private HashMap<String, Integer> tokens;
	private StringBuffer tokenBuffer;

	Tokenizer(int fileNumber) {
		this.fileNumber = fileNumber;
		this.tokenBuffer = new StringBuffer(10);
	}

	@Override
	public HashMap<String, Integer> call() throws Exception {

		tokens = new HashMap<>();
		tokens.put("TOTAL TOKENS", 0);
		tokens.put("DOCUMENT NUMBER", this.fileNumber);

//		try (InputStream file = new FileInputStream("./files/Data_" + this.fileNumber + ".txt")) {
		try (InputStream file = new FileInputStream("./tokenTestData/TT20")) {
			String content = IOUtils.toString(file, Charset.defaultCharset());
			
			for (int i = 0; i < content.length(); i++) {
				
				char currentChar = content.charAt(i);
				int currentCharValue = (int) currentChar;
				
				if (currentCharValue > 96 && currentCharValue < 123) {  // Lower case letter
					tokenBuffer.append(currentChar);
				} else if (currentCharValue > 47 && currentCharValue < 58) {  // Number
					tokenBuffer.append(currentChar);
				} else if (currentCharValue > 64 && currentCharValue < 91) { 	// Capital letter
					tokenBuffer.append(currentChar);
				} else { 
					addSpecialCharacter(currentChar);
				}
			}
			
			tokens.put("TOTAL TOKENS", totalTokens);

			System.out.println(tokens);
			file.close();
			return tokens;
		}
	}
	
	// add special characters to tokens
	private void addSpecialCharacter(char token) {
		checkBuffer();
		System.out.println("Token is: " + token + "  With value: " + (int) token);
		tokenBuffer.append((int)token);
		addToken();
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
