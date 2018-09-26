package tests;

import chatBot.Erudite;
import chatBot.IOConsole;
import chatBot.TextGenerator;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;


public class EruditeConsoleTest { 
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final InputStream originalIn = System.in;

	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}
	
	public void testQuestioning(
			String input, 
			String output, 
			int questionsCount,
			int skippedOutputSymbolsCount) throws Exception {
		var inputBuffer = input.getBytes("UTF-8");
		final ByteArrayInputStream inContent = new ByteArrayInputStream(inputBuffer);
		System.setIn(inContent); 
		
		var erudite = new Erudite(new IOConsole());
		try {
			for (int i = 0; i < questionsCount; i++)
				erudite.askQuestion();
		}
		catch (IOException exceptionIO) {
			assertTrue(exceptionIO.getMessage().equals("Пользователь закончил диалог"));
		}
		
		assertTrue(
				outContent
					.toString()
					.substring(skippedOutputSymbolsCount)
					.contains(output));
		
		erudite.closeIO();
	}
	
	@Test
	public void testIncorrectAnswer() throws Exception {
		testQuestioning("123\nquit\n", "Неправильный ответ!", 2, 0);
	}
	
	@Test
	public void testHelp() throws Exception {
		testQuestioning("help\nquit\n", TextGenerator.getHelp(), 2, 1);
	}
	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setIn(originalIn);
	}
	
}
