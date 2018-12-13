package project.group.cs157a;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.group.cs157a.Token;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvFileCreator {

	public CsvFileCreator(List<HashMap<String, Token>> words) {

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), new NotNull(), new NotNull(),
				new NotNull(), new NotNull()

		};
		
		List<Token> tokens = new ArrayList<>();
		
		for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {
			for (Map.Entry<String, Token> entry : words.get(i).entrySet()) {
				if (!entry.getKey().equals("DOCUMENT NUMBER")) {
					tokens.add(entry.getValue());
				}
			}
		}
		words = null;
		
		Collections.sort(tokens);	

		final String[] header = new String[] { "docID", "word", "tf", "idf", "tfidf" };

		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter("target/writeWithBeanWriter.csv"),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(header);
			
			for (int i = 0; i < tokens.size(); i++) {
				beanWriter.write(tokens.get(i), header, processors);				
			}

//			for (int i = 0; i < ProjectMain.NUMBER_OF_FILES; i++) {
//				for (Map.Entry<String, Token> entry : words.get(i).entrySet()) {
//					if (!entry.getKey().equals("DOCUMENT NUMBER")) {
//						beanWriter.write(entry.getValue(), header, processors);
//					}
//				}
//			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (beanWriter != null) {
				try {
					beanWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
