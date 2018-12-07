package chatBot;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern; 

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
	
	private Map<Integer, HashSet<QuestionAnswer>> idQuestion = new HashMap<Integer, HashSet<QuestionAnswer>>();
	
	
	private final String help = "Привет! Мы тут задаём вопросы о знаменитостях. "
						+ "Все даты записывай в формате ДД.ММ.ГГГГ, а имена в формате ИФ";
	
	public String getHelp()
	{
		return help;
	}
	
	private final String[] listQuestionsAbout = {"awards", "persons"};
	
	private final String[] awards = {"Оскар+роль", "Золотой+глобус+роль"};
	private String[] persons = {"Русская_литература"};
	
	private Random rnd = new Random();
	
	private final String startMediaWiki = "https://ru.wikipedia.org/w/api.php";
	
	private Map<String, ArrayList<QuestionAnswer>> foundQuestionsAnswers = new HashMap<String, ArrayList<QuestionAnswer>>();
	
	public QuestionAnswer getQuestion(int id) throws IOException
	{
		var whatQuestion = rnd.nextInt(listQuestionsAbout.length);
		var questionAbout = listQuestionsAbout[whatQuestion];
		switch(questionAbout)
		{
		case "awards":
			return getQuestionAboutAwards(id);
//		case "persons":
//			return getQuestionAboutPersons(id);
		default:
			return getQuestionAboutPersons(id);
		}
	}
	
	private QuestionAnswer getQuestionAboutAwards(int id) throws IOException
	{
		var pattern = "title=(\")(Премия[\\w\\W]+?)(\")";
		var rex = Pattern.compile(pattern);
		var whatQuestion = rnd.nextInt(awards.length);
		var questionAbout = awards[whatQuestion];
		var reader = getReader(true, questionAbout);
		var line = reader.readLine();
		var match = rex.matcher(line);
		var listNameAwards = new ArrayList<String>();
		while(match.find())
			listNameAwards.add(match.group().split("\"")[1].replaceAll(" ", "_"));
	
		var currentAward = listNameAwards.get(rnd.nextInt(listNameAwards.size()));			
		if (foundQuestionsAnswers.containsKey(currentAward))
		{
			if (foundQuestionsAnswers.get(currentAward).size() == idQuestion.get(id).size())
				idQuestion.get(id).clear();
			var questionsAnswers = foundQuestionsAnswers.get(currentAward);
			var questionAnswer = questionsAnswers.get(rnd.nextInt(questionsAnswers.size()));
			if (checkUsedQuestion(id, questionAnswer))
				return getQuestionAboutPersons(id);
			return questionAnswer;
		}
		var nameAwards = new String[listNameAwards.size()];
		nameAwards = listNameAwards.toArray(nameAwards);
		reader = getReader(false, currentAward);
		line = reader.readLine();
		pattern = "\\[\\[[а-я А-Я]+? \\([кино]*?премия, ([0-9]{4})\\)(\\|)[0-9]+?-я[\\w\\W]*?\\]\\]";
		rex = Pattern.compile(pattern);
		var questionsAnswers = new ArrayList<QuestionAnswer>();
		var nameCurrentAward = currentAward.replaceAll("_", " ");
		while(line != null)
		{
			match = rex.matcher(line);
			if (match.find())
			{
				var newPattern = "\\[\\[([[А-Я а-яЁё] \\.’\\,\\(\\)\\|-]+?)\\]\\]";
				var newRex = Pattern.compile(newPattern);
				while (line!=null)
				{	
					var newMatch = newRex.matcher(line);
					if (newMatch.find())
					{
						var index = newMatch.group(1).indexOf('|');
						questionsAnswers.add(new QuestionAnswer("Кому была вручена " + nameCurrentAward + " в " + match.group(1) + " году?", 
								newMatch.group(1).substring(index + 1)));
						break;
					}
					line = reader.readLine();
				}
			
			}
			line = reader.readLine();
		}
		foundQuestionsAnswers.put(currentAward, questionsAnswers);
		var questionAnswer = questionsAnswers.get(rnd.nextInt(questionsAnswers.size()));
		if (checkUsedQuestion(id, questionAnswer))
			return getQuestionAboutAwards(id);
		
		return questionAnswer;

	}
	
	
	private LineNumberReader getReader(boolean isFirstCalling, String questionAbout) throws IOException 
	{
		Map<String, String> fieldsAndValues = new HashMap<String, String>();
		fieldsAndValues.put("format", "xml");
		fieldsAndValues.put("action", "query");
		if (isFirstCalling)
		{
			fieldsAndValues.put("list", "search");
			fieldsAndValues.put("srsearch", questionAbout);
		}
		else
		{
			fieldsAndValues.put("prop", "revisions");
			fieldsAndValues.put("rvprop", "content");
			fieldsAndValues.put("titles", questionAbout);
		}
		var page = getPage(fieldsAndValues);
		var url = new URL(page);
		var reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
		return reader;
	}
	
	private String getPage(Map<String, String> fieldsAndValues)
	{	
		var fields = new ArrayList<String>();
		var values = new ArrayList<String>();
		var formatter = new Formatter();
		
		for(var entry : fieldsAndValues.entrySet())
		{
			fields.add(entry.getKey());
			values.add(entry.getValue());
		}
		
		if (fields.size() != values.size())
			throw new IllegalArgumentException();
		
		if (fields.size() == 4)
			formatter.format("%s?%s=%s&%s=%s&%s=%s&%s=%s", startMediaWiki, fields.get(0), values.get(0), fields.get(1),
																	   	   values.get(1), fields.get(2), values.get(2), 
																		   fields.get(3), values.get(3));
		else if (fields.size() == 5)
			formatter.format("%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s", startMediaWiki, fields.get(0), values.get(0), fields.get(1),
					 															 values.get(1), fields.get(2), values.get(2), 
					 															 fields.get(3), values.get(3), 
					 															 fields.get(4), values.get(4));
		else throw new IllegalArgumentException();
		return formatter.toString();
	}
	
	private boolean checkUsedQuestion(int id, QuestionAnswer questionAnswer)
	{
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
		return isUsedQuestion;
	}
	
	private QuestionAnswer getQuestionAboutPersons(int id) throws IOException
	{
		var questionAbout = persons[rnd.nextInt(persons.length)];
		if (foundQuestionsAnswers.containsKey(questionAbout))
		{
			if (foundQuestionsAnswers.get(questionAbout).size() == idQuestion.get(id).size())
				idQuestion.get(id).clear();
			var questionsAnswers = foundQuestionsAnswers.get(questionAbout);
			var questionAnswer = questionsAnswers.get(rnd.nextInt(questionsAnswers.size()));
			if (checkUsedQuestion(id, questionAnswer))
				return getQuestionAboutPersons(id);
			return questionAnswer;
		}
		else {System.out.println("Подождите, идёт загрузка информации...");}
		var questionsAnswers = new ArrayList<QuestionAnswer>();
		var reader = getReader(false, questionAbout);
		var line = reader.readLine();
		var pattern = Pattern.compile("([А-Яа-я]+?, [А-Я а-я]+?)\\|[А-Я а-я]*?\\]\\]");
		while(line != null)
		{
			var match = pattern.matcher(line);
			if (match.find())
				{
					var name = match.group(1).replaceAll(" ", "_");
					String date;
					try
					{
						date = getBirthDate(name);
						date = getFormat(date);
					}
					catch(IllegalArgumentException ex) 
					{
						line = reader.readLine();
						continue;
					}
					questionsAnswers.add(new QuestionAnswer("Когда родился(ась) " + match.group(1) + "?", date));
				}
			line = reader.readLine();
		}
		foundQuestionsAnswers.put(questionAbout, questionsAnswers);
		var questionAnswer = questionsAnswers.get(rnd.nextInt(questionsAnswers.size()));
		if (checkUsedQuestion(id, questionAnswer))
			return getQuestionAboutPersons(id);
		
		return questionAnswer;
	}
	
	private String getBirthDate(String name) throws IOException, IllegalArgumentException
	{
		var personReader = getReader(false, name);
		var personLine = personReader.readLine();
		var personPattern = Pattern.compile("([0-9]{1,2})\\|([а-я]+?)\\|([0-9]{4})");
		while(personLine != null)
		{
			var personMatch = personPattern.matcher(personLine);
			if (personMatch.find())
				return personMatch.group(1) + " " + personMatch.group(2) + " " + personMatch.group(3);
			personLine = personReader.readLine();
		}
		throw new IllegalArgumentException();
		
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
