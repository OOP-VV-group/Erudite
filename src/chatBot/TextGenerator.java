package chatBot;

import java.util.Random;
import java.net.*;
import java.io.*;

public class TextGenerator
{
	public static class QuestionAnswer
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
	
	private static String help = "������! �� ��� ����� ������� � �������������."
			+ "��� ���� ��������� � ������� ��.��.����";
	
	public static String getHelp()
	{
		return help;
	}
	
	private static String[] listQuestionsAbout = {"������_��������", "������",
		"��������,_��������_��������", "����,_�����", "�����,_����", "�����,_����",
		"���������,_���"};
	
	private static Random rnd = new Random();
	
	public static QuestionAnswer getQuestion() 
	{
		var number = rnd.nextInt(listQuestionsAbout.length);
		
		String information;
		var questionAboutDate = rnd.nextBoolean();
		
		if (questionAboutDate)
			information = getBornInformation(listQuestionsAbout[number]);
		else information = getPlaceBornInformation(listQuestionsAbout[number]);
		
		var person = listQuestionsAbout[number].replaceAll("_", " ");
		var question = "�������� ���� �������� ��������, ���������� ��� " + person;
		
		if (!questionAboutDate)
			question = "�������� �����, ��� ������� " + person;
		var questionAnswer = new QuestionAnswer(question, information);
		return questionAnswer;
	}
	
	private static String getBornInformation(String page)
	{
		try 
		{
			var web_site = "https://ru.wikipedia.org/wiki/" + page;
			var url = new URL(web_site);
			var reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
			var line = reader.readLine();
			
			while (line != null)
			{
				if (line.contains("����&#160;��������") || line.contains("���� ��������")
						|| line.contains("��������"))
				{
					while(!line.contains("title"))
						line = reader.readLine();
					break;
				}
				line = reader.readLine();
			}
		
		var helperIndex = line.indexOf("title");
		var firstIndex = line.indexOf(">", helperIndex);
		var secondIndex = line.indexOf("</a>", firstIndex);
		var thirdIndex = line.indexOf("���");
		var information = line.substring(firstIndex+1, secondIndex) + " " + 
					line.substring(thirdIndex+5, thirdIndex + 9);
		reader.close();
		information = getFormat(information);
		return information;
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	private static String getFormat(String information)
	{
		var text = information.split(" ");
		var mounth = "";
		if (text[1].compareTo("������") == 0) mounth = ".01.";
		else if (text[1].compareTo("�������") == 0) mounth = ".02.";
		else if (text[1].compareTo("�����") == 0) mounth = ".03.";
		else if (text[1].compareTo("������") == 0) mounth = ".04.";
		else if (text[1].compareTo("���") == 0) mounth = ".05.";
		else if (text[1].compareTo("����") == 0) mounth = ".06.";
		else if (text[1].compareTo("����") == 0) mounth = ".07.";
		else if (text[1].compareTo("�������") == 0) mounth = ".08.";
		else if (text[1].compareTo("��������") == 0) mounth = ".09.";
		else if (text[1].compareTo("�������") == 0) mounth = ".10.";
		else if (text[1].compareTo("������") == 0) mounth = ".11.";
		else mounth = ".12.";
		if (text[0].length() == 1)
			text[0] = "0" + text[0];
		var formatedinformation = text[0] + mounth + text[2];
		return formatedinformation;
	}
	
	private static String getPlaceBornInformation(String page)
	{
		try 
		{
			var web_site = "https://ru.wikipedia.org/wiki/" + page;
			var url = new URL(web_site);
			var reader = new LineNumberReader(new InputStreamReader(url.openStream(), "UTF-8"));
			var line = reader.readLine();
			
			while (line != null)
			{
				if (line.contains("�����&#160;��������")
						|| line.contains("����� ��������"))
				{
					while(!line.contains("title"))
						line = reader.readLine();
					break;
				}
				line = reader.readLine();
			}
		
		var helperIndex = line.indexOf("title");
		var firstIndex = line.indexOf(">", helperIndex);
		var secondIndex = line.indexOf("</a>", firstIndex);
		var information = line.substring(firstIndex+1, secondIndex);
		reader.close();
		return information;
		}
		catch(Exception ex) {
			return null;
		}
	}
	
}
