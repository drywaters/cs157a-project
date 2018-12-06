package project.group.cs157a;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvFileCreator {

	public CsvFileCreator(List<HashMap<String, Double>> words) {

//		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), new NotNull(), new NotNull()};

		final String[] header = new String[] { "doc_id", "token", "tfidf" };

		ICsvMapWriter mapWriter = null;
		try {
			mapWriter = new CsvMapWriter(new FileWriter("target/writeWithMapWriter.csv"),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			mapWriter.writeHeader(header);
//			mapWriter.write(words, header);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (mapWriter != null) {
				try {
					mapWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
