package project.group.cs157a;

public class ProjectMain {

	public static void main(String[] args) {
//		DatabaseConnector dc = new DatabaseConnector();
//		dc.createDatabase();
//		dc.killConnection();
//		
		
		Tokenizer tokenizer = new Tokenizer("Data(1).txt");
		try {
			String[] tokens = tokenizer.call();
			for (String token: tokens) {
				System.out.println(token);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
