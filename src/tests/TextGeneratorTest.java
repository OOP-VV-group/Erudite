//package tests;
//
//import chatBot.TextGenerator;
//import static org.junit.Assert.*;
//
//import java.io.InputStreamReader;
//import java.io.LineNumberReader;
//import java.net.URL;
//import java.util.HashSet;
//
//import org.junit.Test;
//
//public class TextGeneratorTest
//{
//	private TextGenerator tg = TextGenerator.INSTANCE;
//
//	@Test
//	public void testGetHelp()
//	{
//		var help = tg.getHelp();
//		assertEquals(help, "Привет! Мы тут задаём вопросы о знаменитостях."
//				+ "Все даты записывай в формате ДД.ММ.ГГГГ");
//	} 
//	
//	private HashSet<String> questionsForId2 = new HashSet<String>();
//	
//	public boolean testGetQuestion(int id) 
//	{
//		var pair = tg.getQuestion(id);
//		String answer = pair.getAnswer();
//		String question = pair.getQuestion();
//		if (id == 2)
//			questionsForId2.add(question);
//		String result;
//		String person;
//		if (question.contains("Напишите дату рождения"))
//		{
//			result = helperForDate(answer);
//			person = question.substring(48,  question.length());
//		}
//		else
//		{
//			result = answer;
//			person = question.substring(28, question.length()).replace(" ", "_");
//		}
//		
//		try
//		{
//			String web_site = "https://ru.wikipedia.org/wiki/" + person;
//			URL url = new URL(web_site);
//			LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
//			String line = reader.readLine();
//			while(line != null)
//			{
//				if (line.contains(result))
//					break;
//				line = reader.readLine();
//			}
//			var isFound = (line != null);
//			reader.close();
//			return isFound;
//		}
//		catch(Exception ex)
//		{
//			return false;
//		}
//
//	}
//	
//	public static String helperForDate(String answer)
//	{
//		var date = answer.substring(3, 5);
//		var mounth = "";
//		if (date.compareTo("01") == 0) mounth = "января";
//		else if (date.compareTo("02") == 0) mounth = "февраля";
//		else if (date.compareTo("03") == 0) mounth = "марта";
//		else if (date.compareTo("04") == 0) mounth = "апреля";
//		else if (date.compareTo("05") == 0) mounth = "мая";
//		else if (date.compareTo("06") == 0) mounth = "июня";
//		else if (date.compareTo("07") == 0) mounth = "июля";
//		else if (date.compareTo("08") == 0) mounth = "августа";
//		else if (date.compareTo("09") == 0) mounth = "сентября";
//		else if (date.compareTo("10") == 0) mounth = "октября";
//		else if (date.compareTo("11") == 0) mounth = "ноября";
//		else mounth = "декабря";
//		String day;
//		if (answer.substring(0, 2).charAt(0) == '0')
//			day = answer.substring(0,1);
//		else day = answer.substring(0, 2);
//		var result = day + " " + mounth;
//		return result;
//	}
//	
//	@Test
//	public void testGetQuestion_one()
//	{
//		var result = testGetQuestion(1);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testGetQuestion_two()
//	{
//		var result = testGetQuestion(2);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testGetQuestion_three()
//	{
//		var result = testGetQuestion(3);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testGetQuestion_four()
//	{
//		var result = testGetQuestion(4);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testGetQuestion_five()
//	{
//		var result = testGetQuestion(5);
//		assertTrue(result);
//	}
//	
//	@Test
//	public void testGetQuestion_six()
//	{
//		var result1 = testGetQuestion(2);
//		var result2 = testGetQuestion(2);
//		var result3 = testGetQuestion(2);
//		var result4 = testGetQuestion(2);
//		var result5 = testGetQuestion(2);
//		var questions = questionsForId2.toArray();
//		var result6 = questions[0] != questions[1] && questions[1] != questions[2] &&
//				questions[2] != questions[3] && questions[3] != questions[4];
//
//		assertTrue(result1);
//		assertTrue(result6);
//	}
//}
