package chatBot;

import java.util.Scanner;

public class IOConsole implements IOModule{
	private static Scanner scanner;
	
	public void closeIO() {
		scanner.close();
	}
	
	public void openIO() {
		IOConsole.scanner = new Scanner(System.in);
	}
	
	public void sendBotMessages(String[] messages) {
		for (String message : messages)
			System.out.println(message);	
	}

	public String[] collectUserMessages() {
		return new String[] { scanner.nextLine() };
	}
	
}
