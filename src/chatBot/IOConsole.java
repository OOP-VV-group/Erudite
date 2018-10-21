package chatBot;

import java.util.Scanner;

public enum IOConsole implements IOModule{
	INSTANCE;
	
	private Scanner scanner;
	private int id = 0;
	
	public void closeIO() {
		this.scanner.close();
	}
	
	public void openIO() {
		this.scanner = new Scanner(System.in);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void sendBotMessages(String[] messages) {
		for (String message : messages)
			System.out.println(message);	
	}

	public String[] collectUserMessages() {
		return new String[] { this.scanner.nextLine() };
	}
	
}
