package chatBot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.net.*;
import java.io.*;

public enum TextGenerator
{
	INSTANCE;

	public class QuestionAnswer
	{
		private String question;
		private String answer;
		
		public QuestionAnswer(String quest, String ans)
		{
			question = quest;
			answer = ans;
		}
		public String getQuestion()
		{
			return question;
		}
		public String getAnswer()
		{
			return answer;
		}
	}
	
	private Map<Integer, HashSet<QuestionAnswer>> idQuestion =
			new HashMap<Integer, HashSet<QuestionAnswer>>();
	
	
	private String help = "Привет! Мы тут задаём вопросы о знаменитостях."
			+ "Все даты записывай в формате ДД.ММ.ГГГГ";
	
	public String getHelp()
	{
		return help;
	}
	
	private String[] listQuestionsAbout = {"Фредди_Меркьюри", "Эминем",
		"Высоцкий,_Владимир_Семёнович", "Форд,_Генри", "Джобс,_Стив", "Гейтс,_Билл",
		"Маккартни,_Пол", "Асанов,_Магаз_Оразкимович"};
	
	private Random rnd = new Random();
	
	public QuestionAnswer getQuestion(int id) 
	{
		int number = rnd.nextInt(listQuestionsAbout.length);
		
		String information;
		boolean questionAboutDate = rnd.nextBoolean();
		
			information = getBornInformation(listQuestionsAbout[number], questionAboutDate);
		
		String person = listQuestionsAbout[number].replaceAll("_", " ");
		String question = "Напишите дату рождения человека, известного как " + person;
		
		if (!questionAboutDate)
			question = "Напишите город, где родился " + person;
		QuestionAnswer questionAnswer = new QuestionAnswer(question, information);
		var isUsedQuestion = false;
		var isNewKey = true;
		
		wasQuestion: for (int key : idQuestion.keySet())
		{
			if (key == id)
			{
				isNewKey = false;
				var questions = idQuestion.get(id);
				for (QuestionAnswer qa : questions)
					if (qa.question.equals(questionAnswer.question)) 
					{
						isUsedQuestion = true;
						break wasQuestion;
					}
			}
		}
		if (isUsedQuestion)
			questionAnswer = getQuestion(id);
		if (isNewKey)
		{
			var questions = new HashSet<QuestionAnswer>();
			questions.add(questionAnswer);
			idQuestion.put(id, questions);
		}
		else
		{
			var questions = idQuestion.get(id);
			questions.add(questionAnswer);
			idQuestion.put(id, questions);
		}
		var questions = idQuestion.get(id); 
		if (questions.size() >= listQuestionsAbout.length * 2)
		{
			questions.clear();
			idQuestion.put(id, questions);
		}
		
		return questionAnswer;
	}
	
	private String getBornInformation(String page, boolean aboutDate)
	{
		try 
		{
			String web_site = "https://ru.wikipedia.org/wiki/" + page;
			URL url = new URL(web_site);
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String line = reader.readLine();
			String firstSearchedWord;
			String secondSearchedWord;
			if (aboutDate)
			{
				firstSearchedWord = "Дата&#160;рождения";
				secondSearchedWord = "Дата рождения";
			}
			else
			{
				firstSearchedWord = "Место&#160;рождения";
				secondSearchedWord = "Место рождения";
			}
			
			while (line != null)
			{
				if (line.contains(firstSearchedWord) || line.contains(secondSearchedWord))
				{
					while(!line.contains("title"))
						line = reader.readLine();
					break;
				}
				line = reader.readLine();
			}
		
		int helperIndex = line.indexOf("title");
		int firstIndex = line.indexOf(">", helperIndex);
		int secondIndex = line.indexOf("</a>", firstIndex);
	    int thirdIndex = line.indexOf("год");
	    String information;
	    if (aboutDate)
	    {
	    	information = line.substring(firstIndex+1, secondIndex) + " " + 
						line.substring(thirdIndex+5, thirdIndex + 9);
			information = getFormat(information);
	    }
	    else
			information = line.substring(firstIndex+1, secondIndex);
		reader.close();
		return information;
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	private String getFormat(String information)
	{
		String[] text = information.split(" ");
		String mounth = "";
		if (text[1].compareTo("января") == 0) mounth = ".01.";
		else if (text[1].compareTo("февраля") == 0) mounth = ".02.";
		else if (text[1].compareTo("марта") == 0) mounth = ".03.";
		else if (text[1].compareTo("апреля") == 0) mounth = ".04.";
		else if (text[1].compareTo("мая") == 0) mounth = ".05.";
		else if (text[1].compareTo("июня") == 0) mounth = ".06.";
		else if (text[1].compareTo("июля") == 0) mounth = ".07.";
		else if (text[1].compareTo("августа") == 0) mounth = ".08.";
		else if (text[1].compareTo("сентября") == 0) mounth = ".09.";
		else if (text[1].compareTo("октября") == 0) mounth = ".10.";
		else if (text[1].compareTo("ноября") == 0) mounth = ".11.";
		else mounth = ".12.";
		if (text[0].length() == 1)
			text[0] = "0" + text[0];
		String formatedinformation = text[0] + mounth + text[2];
		return formatedinformation;
	}
	
}
