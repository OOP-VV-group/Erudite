package chatBot;


public class Main {
	
	public static void main(String[] args) throws Exception {
		while (true) {
			var question = TextGenerator.getQuestion();
			IOConsole.sendBotMessages(new String[] { question.getQuestion() });
			
			var userMessage = IOConsole.collectUserMessage();
			if (userMessage.equals("quit"))
				break;
			else if (userMessage == question.getAnswer())
				IOConsole.sendBotMessages(new String[] { "Правильный ответ!" , ""});
			else
				IOConsole.sendBotMessages(new String[] { "Неправильный ответ! Правильный ответ: " + 
														 question.getAnswer(), ""});
		}
		IOConsole.closeScanner();
	}

}
