package chatBot;

import chatBot.TextGenerator.QuestionAnswer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Erudite {
	private final HashSet<String> COMMANDS = 
		new HashSet<String>(Arrays.asList("quit", "help"));
	private HashMap<Integer, QuestionAnswer> askedQuestions =
		new HashMap<Integer, QuestionAnswer>();
	private TextGenerator textGenerator = TextGenerator.INSTANCE;
	
	public String[] getStartMessage() {
		return new String[] { 
				this.textGenerator.getHelp(), 
				"Команды: ",
				"    help - вывести справку",
				"    quit - закончить сессию",
				""};
	}
	
	public String getQuestion(int id) throws IOException {
		return this.getQuestion(id, null);
	}
	
	public String getQuestion(int id, QuestionAnswer question) throws IOException {
		if (question == null)
			question = this.textGenerator.getQuestion(id);
		
		if (this.askedQuestions.containsKey(id))
			return this.askedQuestions.get(id).getQuestion();
		
		this.askedQuestions.put(id, question);
		return question.getQuestion();
	}
	
	public String[] checkAnswer(int id, String answer) throws IOException {
		if (COMMANDS.contains(answer))
			switch (answer) {
				case "quit":
					return new String[] { "quit" };
				case "help":
					return new String[] { "help" };
			}
		
		if (!this.askedQuestions.containsKey(id))
			throw new IOException("While checking answer no asked question found");
		
		if (this.askedQuestions.get(id).getAnswer().equals(answer))
			return new String[] { "Правильный ответ!\n" };
		return new String[] {
			"Неправильный ответ. Правильный ответ: " + this.askedQuestions.get(id).getAnswer(), 
			""};
	}
	
	public void closeIO(int id) {
		if (this.askedQuestions.containsKey(id))
			this.askedQuestions.remove(id);
	}
}
