package servers;

import java.io.IOException;
import java.util.Scanner;

import chatBot.Erudite;

public class ConsoleServer {
	private static Erudite erudite = new Erudite();

	public static void main(String[] args) throws IOException {
		Scanner console = new Scanner(System.in);
		
		System.out.println(ConsoleServer.erudite.getStartMessage());
		while (true)
			try {
				System.out.println(ConsoleServer.erudite.getQuestion(0));
				
				String result = ConsoleServer.erudite.checkAnswer(0, console.nextLine());
				while (true)
					if (result.equals("help")) {
						System.out.println(erudite.getHelpMessage());	
						result = ConsoleServer.erudite.checkAnswer(
								0,
								console.nextLine());
						continue;
					} 
					else if (result.equals("quit"))
						throw new IOException("User quited"); 
					else
						break;
				System.out.println(result);
			}
			catch (IOException exceptionIO) {
				console.close();

				if (exceptionIO.getMessage().equals("User quited"))
					break;
				if (exceptionIO.getMessage().equals("While checking answer no asked question found"))
					throw exceptionIO;
			}
		
		console.close();
	}
}
