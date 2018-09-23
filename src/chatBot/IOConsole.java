package chatBot;

import java.util.Scanner;

public final class IOConsole {
	private static Scanner scanner = new Scanner(System.in);
	
	public static void closeScanner() {
		scanner.close();
	}

	public static void sendBotMessages(String[] messages) {
		for (String message : messages)
			System.out.println(message);	
	}

	public static String collectUserMessage() {
		return scanner.nextLine();
	}
	
}
