package tests;

import chatBot.Erudite;
import chatBot.TextGenerator;

import java.io.IOException;

import org.junit.Test;

public class EruditeTest { 
	@Test
	public void questionClosable() throws IOException {
		var erudite = new Erudite();
		erudite.getQuestion(0);
		erudite.checkAnswer(0, "");
		try {
			erudite.checkAnswer(0, "");
		}
		catch (IOException exceptionIO) {
			assert exceptionIO.getMessage().equals("While checking answer no asked question found");
		}
	}
	
	@Test
	public void CorrectAnswer() throws IOException {
		var erudite = new Erudite();
		var question = TextGenerator.INSTANCE.getQuestion(0);
		erudite.getQuestion(0, question);
		assert erudite.checkAnswer(0, question.getAnswer())[0].startsWith("Правильный ответ");
	}
	
	@Test
	public void IncorrectAnswer() throws IOException {
		var erudite = new Erudite();
		erudite.getQuestion(0);
		assert erudite.checkAnswer(0, "123")[0].startsWith("Неправильный ответ");
	}
}
