package starters;

import java.io.IOException;

import chatBot.Erudite;
import chatBot.IOConsole;

public class ConsoleStarter {

	public static void main(String[] args) {
		var erudite = new Erudite();
		
		while (true) {
			try {
				erudite.askQuestion(0);
			}
			catch (IOException exceptionIO) {
				break;
			}
		}
		
	}
}
