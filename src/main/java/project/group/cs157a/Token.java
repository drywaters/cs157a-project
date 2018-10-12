package project.group.cs157a;

public class Token {
	
	private String word;
	private double tf;
	private double idf;
	private double tfidf;	
	private int docID;
	
	public Token() {}
	
	public Token(int docID) {
		this.docID = docID;
	}
	
	public Token(int docID, String word) {
		this.docID = docID;
		this.word = word;
	}
	
	public Token(String word, double tf, double idf, double tfidf) {
		super();
		this.word = word;
		this.tf = tf;
		this.idf = idf;
		this.tfidf = tfidf;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int docID) {
		this.docID = docID;
	}

	@Override
	public String toString() {
		return "Token [word=" + word + ", tf=" + tf + ", idf=" + idf + ", tfidf=" + tfidf + "]";
	}
}
