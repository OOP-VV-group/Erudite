package chatBot;

import chatBot.TextGenerator.QuestionAnswer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Erudite {
	private IOModule[] modulesIO =  new IOModule[] { IOConsole.INSTANCE };
	private final HashSet<String> COMMANDS = 
		new HashSet<String>(Arrays.asList("quit", "help"));
	private TextGenerator textGenerator;
	
	/*
	public Erudite(IOModule[] modulesIO) {
		for (IOModule moduleIO : modulesIO) {
			if (moduleIO == IOConsole.INSTANCE)
				continue;
			this.modulesIO.
			moduleIO.openIO();
		}
	}*/
	
	public Erudite() { 
		this.textGenerator = TextGenerator.INSTANCE;
		
		this.modulesIO[0].openIO();
		this.sendStartMessage(0);
	}
	
	public void sendStartMessage(int id) {
		this.modulesIO[id].sendBotMessages(new String[] { 
				this.textGenerator.getHelp(), 
				"Команды: ",
				"    help - вывести справку",
				"    quit - закончить сессию",
				""});
	}
	
	public String[] askQuestion(int id) throws IOException {
		return this.askQuestion(id, null);
	}
	
	public String[] askQuestion(int id, QuestionAnswer question) throws IOException {
		if (question == null)
			question = this.textGenerator.getQuestion(id);
		this.modulesIO[id].sendBotMessages(new String[] { question.getQuestion() });
		
		var messages = this.modulesIO[id].collectUserMessages();
		while (COMMANDS.contains(messages[messages.length - 1])) {
			switch (messages[messages.length - 1]) {
				case "quit": 
					throw new IOException("Пользователь закончил диалог");
				case "help":
					this.modulesIO[id].sendBotMessages(
							new String[] { this.textGenerator.getHelp() });
			}
			messages = this.modulesIO[id].collectUserMessages();
		}
		
		if (messages[messages.length - 1].equals(question.getAnswer())) {
			var botReply = new String[] { "Правильный ответ!" , ""};
			this.modulesIO[id].sendBotMessages(botReply);
			return botReply;
		}
		
		var botReply = new String[] { 
				"Неправильный ответ! Правильный ответ: " +
				question.getAnswer(), "" };
		modulesIO[id].sendBotMessages(botReply);
		return botReply;
	}
	
	public void closeIO(int id) {
		modulesIO[id].closeIO();
	}
}
