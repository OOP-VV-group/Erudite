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
	private String startMessage = 
		this.textGenerator.getHelp() + "\n" + 
		"Команды: \n" +
		"    help - вывести справку\n" +
		"    quit - закончить сессию\n";
	private String helpMessage = 
		"Мы тут задаём вопросы о знаменитостях. " + 
		"Все даты записывай в формате ДД.ММ.ГГГГ, а имена в формате ИФ\n" +
		"Команды: \n" +
		"    help - вывести справку\n" +
		"    quit - закончить сессию\n";
	private String quitMessage = "Спасибо за ответы!";
	
	public String getStartMessage() { return startMessage; }
	
	public String getHelpMessage() { return helpMessage; }
	
	public String getQuitMessage() { return quitMessage; }
	
	public String getQuestion(int id) throws IOException 
	{
		return this.getQuestion(id, null);
	}
	
	public String getQuestion(int id, QuestionAnswer question) throws IOException 
	{
		if (question == null)
			question = this.textGenerator.getQuestion(id);
		
		if (this.questionAsked(id))
			throw new IllegalArgumentException("Question already asked");
		
		this.askedQuestions.put(id, question);
		return question.getQuestion();
	}
	
	public String checkAnswer(int id, String answer) throws IOException 
	{
		if (COMMANDS.contains(answer))
			return answer;
		
		if (!this.questionAsked(id))
			throw new IOException("While checking answer no asked question found");
		
		String trueAnswer = this.askedQuestions.get(id).getAnswer();
		this.askedQuestions.remove(id);
		if (trueAnswer.equals(answer))
			return "Правильный ответ!\n";
		return Erudite.stringsConcat(new String[] {
			"Неправильный ответ. Правильный ответ: ",
			trueAnswer,
			"\n"});
	}
	
	public void removeUser(int id) 
	{
		if (this.questionAsked(id))
			this.askedQuestions.remove(id);
	}
	
	public static String stringsConcat(String[] strings)
	{
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < strings.length; i++)
			result.append(strings[i]);
		return result.toString();
	}
	
	public boolean questionAsked(int id) 
	{
		if (this.askedQuestions.containsKey(id))
			return true;
		return false;
	}
}
