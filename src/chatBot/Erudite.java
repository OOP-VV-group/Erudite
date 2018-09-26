package chatBot;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Erudite {
	private IOModule moduleIO;
	private final HashSet<String> COMMANDS = 
		new HashSet<String>(Arrays.asList("quit", "help"));

	public Erudite(IOModule moduleIO) {
		this.moduleIO = moduleIO;
		moduleIO.openIO();
	}
	
	public void sendStartMessage() {
		this.moduleIO.sendBotMessages(new String[] { 
				TextGenerator.getHelp(), 
				"Команды: ",
				"    help - вывести справку",
				"    quit - закончить сессию",
				""});
	}
	
	public void askQuestion() throws IOException {
		var question = TextGenerator.getQuestion();
		this.moduleIO.sendBotMessages(new String[] { question.getQuestion() });
		
		var messages = this.moduleIO.collectUserMessages();
		while (COMMANDS.contains(messages[messages.length - 1])) {
			switch (messages[messages.length - 1]) {
				case "quit": 
					throw new IOException("Пользователь закончил диалог");
				case "help":
					this.moduleIO.sendBotMessages(
							new String[] { TextGenerator.getHelp() });
			}
			messages = this.moduleIO.collectUserMessages();
		}
		
		if (messages[messages.length - 1].equals(question.getAnswer()))
			this.moduleIO.sendBotMessages(new String[] { "Правильный ответ!" , ""});
		else
			moduleIO.sendBotMessages(new String[] { 
					"Неправильный ответ! Правильный ответ: " +
					question.getAnswer(), "" }); 
	}
	
	public void closeIO() {
		moduleIO.closeIO();
	}
}
