package project.group.cs157a;

public class Token {
	
	private String word;
	private double tf;
	private double idf;
	private double tfidf;	
	private int docid;
	
	public Token() {}
	
	public Token(int docid) {
		this.docid = docid;
	}
	
	public Token(int docid, String word) {
		this.docid = docid;
		this.word = word;
	}

	public Token(int docid, String word, double tfidf) {
		this.docid = docid;
		this.word = word;
		this.tfidf = tfidf;
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

	public int getDocid() {
		return docid;
	}

	public void setDocid(int docid) {
		this.docid = docid;
	}

	@Override
	public String toString() {
		return "Token [docid=" + docid + ", word=" + word + ", tfidf=" + tfidf + "]";
	}
}
