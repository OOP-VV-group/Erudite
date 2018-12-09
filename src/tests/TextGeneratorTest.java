package tests;

import chatBot.TextGenerator;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Pattern; 


import org.junit.Test;

public class TextGeneratorTest
{
	private TextGenerator tg = TextGenerator.INSTANCE;

	@Test
	public void testGetHelp()
	{
		var help = tg.getHelp();
		assertEquals(help, "Привет! Мы тут задаём вопросы о знаменитостях. "
				+ "Все даты записывай в формате ДД.ММ.ГГГГ, а имена в формате ИФ");
	} 
	
	@Test
	public void testGetQuestion()
	{
		var result = true;
		try
		{
			for(var i = 0; i < 100; i ++)
				result = result && testGetQuestionHelper();
		}
		catch(IOException IO)
		{
			assert false : "Нет подключение к сети Интернет";
		}
		catch(IllegalArgumentException ill)
		{
			assert false;
		}
		assertTrue(result);
	}
	
	private boolean testGetQuestionHelper() throws IOException, IllegalArgumentException
	{
		var mediaWikiPage = "https://ru.wikipedia.org/w/api.php?format=xml&action=query&"
				+ "prop=revisions&rvprop=content&titles=";
		var answerQuestion = tg.getQuestion(0);
		var question = answerQuestion.getQuestion();
		var answer = answerQuestion.getAnswer();
		if (question.contains("Премия"))
		{
			var result =  workWithAward(question, mediaWikiPage);
			return result.equals(answer);
		}
		else if (question.contains("Когда родился(ась) "))
		{
			var result = workWithPerson(question, mediaWikiPage);
			return result.equals(answer);
		}
		throw new IllegalArgumentException();
	}
	
	private String workWithAward(String question, String mediaWikiPage) throws IOException, IllegalArgumentException
	{
		var award = question.split(" в ")[0].replaceAll("Кому была вручена ", "").replaceAll(" ", "_");
		var year = question.split(" в ")[1].replaceAll(" году?", "");
		year = year.substring(0, year.length() - 1);
		var page = mediaWikiPage + award;
		var url = new URL(page);
		var reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
		var line = reader.readLine();
		var rex = Pattern.compile("\\[\\[[а-я А-Я]+? \\([кино]*?премия, " + year + "\\)(\\|)[0-9]+?-я[\\w\\W]*?\\]\\]");
		while(line != null)
		{
			var match = rex.matcher(line);
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
						return newMatch.group(1).substring(index + 1);
					}
					line = reader.readLine();
				}
			}
			line = reader.readLine();
		}
		throw new IllegalArgumentException();
	}
	
	private String workWithPerson(String question, String mediaWikiPage) throws IOException, IllegalArgumentException
	{
		var person = question.substring(19);
    	person = person.substring(0, person.length() - 1).replaceAll(" ", "_");
		var page = mediaWikiPage + person;
		var url = new URL(page);
		var reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
		var line = reader.readLine();
		var rex = Pattern.compile("([0-9]{1,2})\\|([а-я]+?)\\|([0-9]{4})");
		while(line != null)
		{
			var match = rex.matcher(line);
			if (match.find())
				return getFormat(match.group(1) + " " + match.group(2) + " " + match.group(3));
			line = reader.readLine();
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
