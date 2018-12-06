package project.group.cs157a;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.tartarus.snowball.ext.PorterStemmer;

public class TokenizerStemmer {

	private double totalTokens;
	private List<HashMap<String, Double>> tokens;
	private StringBuffer tokenBuffer;
	private PorterStemmer stemmer;
	
	TokenizerStemmer() {
		this.totalTokens = 0;
		this.tokenBuffer = new StringBuffer(10);
		tokens = new ArrayList<>();
		stemmer = new PorterStemmer();
	}

	public List<HashMap<String, Double>> getTokens() {

		String[] splitText = splitFile();

		for (int i = 0; i < splitText.length; i++) {
			try {
				this.tokens.add(tokenStrings(splitText[i], (double) i+1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tokens;
	}

	public String[] splitFile() {

		try (InputStream file = new FileInputStream("./singleFile/2.txt")) {
			String content = IOUtils.toString(file, Charset.defaultCharset());
			return content.split("\\.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	public HashMap<String, Double> tokenStrings(String content, double fileNumber) throws Exception {

		HashMap<String, Double> fileMap = new HashMap<>();
		fileMap.put("TOTAL TOKENS", 0.0);
		fileMap.put("DOCUMENT NUMBER", fileNumber);

		for (int i = 0; i < content.length(); i++) {

			char currentChar = Character.toLowerCase(content.charAt(i));
			int currentCharValue = (int) currentChar;


			if (currentCharValue > 96 && currentCharValue < 123) { // Lower case letter
				tokenBuffer.append(currentChar);
			} else if (currentCharValue > 64 && currentCharValue < 91) { // Capital letter
				tokenBuffer.append(currentChar);
			} else {
				nonAlphabet(fileMap);
			}
		}

		fileMap.put("TOTAL TOKENS", totalTokens);


		return fileMap;

	}

	// clear buffer, add token that is there
	// if character is not A-Za-z
	private void nonAlphabet(HashMap<String, Double> fileMap) {
		checkBuffer(fileMap);
	}

	private void checkBuffer(HashMap<String, Double> fileMap) {
		if (tokenBuffer.length() > 0) {
			addToken(fileMap);
		}
	}

	private void addToken(HashMap<String, Double> fileMap) {
		this.totalTokens++;
		stemmer.setCurrent(tokenBuffer.toString());
		stemmer.stem();
		String token = stemmer.getCurrent();
		if (token.length() > 0) {
			if (fileMap.containsKey(token)) {
				double currentOccur = fileMap.get(token);
				fileMap.replace(token, currentOccur + 1);
			} else {
				fileMap.put(token, 1.0);
			}
		}
		clearBuffer();
	}

	private void clearBuffer() {
		tokenBuffer.delete(0, tokenBuffer.length());
	}

}