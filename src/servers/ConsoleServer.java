package servers;

import java.io.IOException;
import java.util.HashMap;

import chatBot.Erudite;
import chatBot.IOConsole;
import chatBot.IOModule;

public class ConsoleServer {
	private static Erudite erudite = new Erudite();

	public static void main(String[] args) throws IOException {
		IOConsole.getInstance().open();
		IOConsole.getInstance().sendBotMessages(
				 ConsoleServer.erudite.getStartMessage());
		
		while (true)
			try {
				IOConsole.getInstance().sendBotMessages(
						new String[] { ConsoleServer.erudite.getQuestion(0) });
				
				String[] result = ConsoleServer.erudite.checkAnswer(
						0,
						IOConsole.getInstance().collectUserMessages()[0]);
				while (true)
					if (result[0].equals("help")) {
						IOConsole.getInstance().sendBotMessages(
								new String[] {
									"Мы тут задаём вопросы о знаменитостях. " + 
									"Все даты записывай в формате ДД.ММ.ГГГГ, а имена в формате ИФ",
									"Команды: ",
									"    help - вывести справку",
									"    quit - закончить сессию",
									""});	
						result = ConsoleServer.erudite.checkAnswer(
								0,
								IOConsole.getInstance().collectUserMessages()[0]);
						continue;
					} 
					else if (result[0].equals("quit"))
						throw new IOException("User quited"); 
					else
						break;
				IOConsole.getInstance().sendBotMessages(result);
			}
			catch (IOException exceptionIO) {
				if (exceptionIO.getMessage().equals("User quited"))
					break;
				if (exceptionIO.getMessage().equals("While checking answer no asked question found"))
					throw exceptionIO;
			}
		
		IOConsole.getInstance().close();
	}
}
