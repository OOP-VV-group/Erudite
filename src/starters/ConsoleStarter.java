package starters;

import java.io.IOException;

import chatBot.Erudite;
import chatBot.IOConsole;

public class ConsoleStarter {

	public static void main(String[] args) {
		var erudite = new Erudite(new IOConsole());
		erudite.sendStartMessage();
		
		while (true) {
			try {
				erudite.askQuestion();
			}
			catch (IOException exceptionIO) {
				break;
			}
		}
		
	}
}
