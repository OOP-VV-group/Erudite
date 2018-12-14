package tests;

import chatBot.TextGenerator;
import junit.framework.Assert;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
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
	
	private Map<String, String> questionsAndAnswers = new HashMap<String, String>();
	private int count = 0;
	
	@Before
	public void fillQuestionsAndAnswers()
	{
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль второго плана в 1995 году?", "Дайан Уист");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль второго плана в 2001 году?", "Марша Гей Харден");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль второго плана в 2015 году?", "Патрисия Аркетт");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль второго плана в 1965 году?", "Лиля Кедрова");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль второго плана в 2000 году?", "Анджелина Джоли");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль в 2017 году?", "Эмма Стоун");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль в 1990 году?", "Джессика Тэнди");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль в 1970 году?", "Мэгги Смит");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль в 2000 году?", "Хилари Суонк");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую женскую роль в 1980 году?", "Салли Филд");
		questionsAndAnswers.put("Кому была вручена Премия «Золотой глобус» за лучшую мужскую роль — драма в 2017 году?", "Кейси Аффлек");
		questionsAndAnswers.put("Кому была вручена Премия «Золотой глобус» за лучшую мужскую роль — драма в 2016 году?", "Леонардо Ди Каприо");
		questionsAndAnswers.put("Кому была вручена Премия «Золотой глобус» за лучшую мужскую роль — драма в 1986 году?", "Джон Войт");
		questionsAndAnswers.put("Кому была вручена Премия «Золотой глобус» за лучшую мужскую роль — драма в 1970 году?", "Джон Уэйн");
		questionsAndAnswers.put("Кому была вручена Премия «Золотой глобус» за лучшую мужскую роль — драма в 1969 году?", "Питер О’Тул");
		questionsAndAnswers.put("Когда родился(ась) Фадеев, Александр Александрович?", "24.12.1901");
		questionsAndAnswers.put("Когда родился(ась) Барто, Агния Львовна?", "17.02.1906");
		questionsAndAnswers.put("Когда родился(ась) Пушкин, Александр Сергеевич?", "06.06.1799");
		questionsAndAnswers.put("Когда родился(ась) Сумароков, Александр Петрович?", "25.11.1717");
		questionsAndAnswers.put("Когда родился(ась) Когда родился(ась) Гоголь, Николай Васильевич?", "01.04.1809");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль в 2018 году?", "Гэри Олдмен");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль в 1988 году?", "Майкл Дуглас");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль в 1970 году?", "Джон Уэйн");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль в 1960 году?", "Чарлтон Хестон");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль в 1953 году?", "Гэри Купер");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль второго плана в 2009 году?", "Хит Леджер");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль второго плана в 2000 году?", "Майкл Кейн");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль второго плана в 1981 году?", "Тимоти Хаттон");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль второго плана в 1975 году?", "Роберт Де Ниро");
		questionsAndAnswers.put("Кому была вручена Премия «Оскар» за лучшую мужскую роль второго плана в 1960 году?", "Хью Гриффит");
	}
	
	@Test
	public void testGetQuestion()
	{
		var result = true;
		while(count < questionsAndAnswers.size())
		{
			try 
			{
				var questionAnswer = tg.getQuestion(0);
				for(var question : questionsAndAnswers.keySet())
					if (questionAnswer.getQuestion().equals(question))
					{
						//System.out.println(questionAnswer.getQuestion());
						//System.out.println(questionAnswer.getAnswer());
						result = result && questionAnswer.getAnswer().equals(questionsAndAnswers.get(question));
						//System.out.println(result);
						count++;
					}
			}
			catch(IOException IO) {System.exit(-1);}
		}
		assert result;
	}
}
