package tests;

import chatBot.Erudite;
import chatBot.TextGenerator;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;


public class EruditeConsoleTest { 
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final InputStream originalIn = System.in;

	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}
	
	private void sendMessageToConsole(String message) 
			throws UnsupportedEncodingException {
		var inputBuffer = message.getBytes("UTF-8");
		final ByteArrayInputStream inContent = new ByteArrayInputStream(inputBuffer);
		System.setIn(inContent); 
	}
	
	@Test
	public void testIncorrectAnswer() throws Exception {
		try { this.sendMessageToConsole("123\nquit\n"); }
		catch (UnsupportedEncodingException exception) {
			assert false : "Message to console is given with wrong encoding";
		
		}
		
		var erudite = new Erudite();
		String[][] botReplies = new String[][] {{}, {}};
		try {
			for (int i = 0; i < 2; i++)
				botReplies[0] = erudite.askQuestion(0);
		}
		catch (IOException exceptionIO) {
			assert exceptionIO.getMessage().equals("Пользователь закончил диалог");
			Assert.assertEquals( exceptionIO.getMessage(), "Пользователь закончил диалог");
		}
		
		assert botReplies[0][0].startsWith("Неправильный ответ");
		
		erudite.closeIO(0);
	}
	
//	@Test
//	public void testCorrectAnswer() {
//		var textGenerator = TextGenerator.INSTANCE;
//		var question = textGenerator.getQuestion(0);
//		try { 
//			this.sendMessageToConsole(question.getAnswer() + "\nquit\n"); 
//		}
//		catch (UnsupportedEncodingException exception) {
//			assert false : "Message to console is given with wrong encoding";
//		}
//		
//		var erudite = new Erudite();
//		String[][] botReplies = new String[][] {{}, {}};
//		try {
//			for (int i = 0; i < 2; i++)
//				botReplies[i] = erudite.askQuestion(0, question);
//		}
//		catch (IOException exceptionIO) {
//			assert exceptionIO.getMessage().equals("Пользователь закончил диалог");
//		}
//		
//		assert botReplies[0][0].startsWith("Правильный ответ");
//		
//		erudite.closeIO(0);
//	}
//	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setIn(originalIn);
	}
	
}
