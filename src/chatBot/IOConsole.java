package chatBot;

import java.util.Scanner;

public class IOConsole implements IOModule {
	private Scanner scanner;
	private int id = 0;
	private static IOConsole consoleIO = null;
	
	private IOConsole() {
		IOConsole.consoleIO = this;
	}
	
	public static IOConsole getInstance() {
		if (IOConsole.consoleIO == null)
			IOConsole.consoleIO = new IOConsole();

		return IOConsole.consoleIO;
	}
	
	public void open() {
		this.scanner = new Scanner(System.in);
	}

	public void close() {
		this.scanner.close(); 
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
