package project.group.cs157a;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;

public class TokenTest {

	public static void main(String[] args) {
		CreateTokens tokens = new CreateTokens();
	}

	private static class CreateTokens {

		private int fileNumber = 0;
		private int totalTokens = 0;
		private ArrayList<String> tokens;
		private StringBuffer tokenBuffer;

		CreateTokens() {
			tokens = new ArrayList<>();
			tokenBuffer = new StringBuffer(10);
			try (InputStream file = new FileInputStream("./tokenTestData/TT20")) {
				String content = IOUtils.toString(file, Charset.defaultCharset());

				for (int i = 0; i < content.length(); i++) {

					char currentChar = content.charAt(i);
					int currentCharValue = (int) currentChar;

					if (currentCharValue > 96 && currentCharValue < 123) { // Lower case letter
						tokenBuffer.append(currentChar);
					} else if (currentCharValue > 47 && currentCharValue < 58) { // Number
						tokenBuffer.append(currentChar);
					} else if (currentCharValue > 64 && currentCharValue < 91) { // Capital letter
						tokenBuffer.append(currentChar);
					} else if (currentCharValue == 10) {
						continue; 
					} else {
						addSpecialCharacter(currentChar);
					}
				}

				for (int i = 0; i < tokens.size(); i++) {
					System.out.println(tokens.get(i) + "\t" + (i + 1));
				}
				file.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// add special characters to tokens
		private void addSpecialCharacter(char token) {
			checkBuffer();
			tokenBuffer.append(Integer.toHexString((int) token));
			addToken();
		}

		private void checkBuffer() {
			if (tokenBuffer.length() > 0) {
				addToken();
			}
		}

		private void addToken() {
			this.totalTokens++;
			tokens.add(tokenBuffer.toString());
			clearBuffer();
		}

		private void clearBuffer() {
			tokenBuffer.delete(0, tokenBuffer.length());
		}

	}

}
