package tests;

import chatBot.Main;
import chatBot.TextGenerator;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;


public class MainTest { 
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final InputStream originalIn = System.in;

	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	}
	
	@Test
	public void testIncorrectAnswer() throws Exception {
		var inputBuffer = "123\nquit\n".getBytes("UTF-8");
		final ByteArrayInputStream inContent = new ByteArrayInputStream(inputBuffer);
		System.setIn(inContent); 
		
		Main.main(new String[] {});
		
		assertTrue(outContent.toString().contains("Неправильный ответ!"));
	}
	
	@Test
	public void testHelp() throws Exception {
		var inputBuffer = "help\nquit\n".getBytes("UTF-8");
		final ByteArrayInputStream inContent = new ByteArrayInputStream(inputBuffer);
		System.setIn(inContent);
		
		Main.main(new String[] {});
		
		assertTrue(outContent.toString().substring(1).contains(TextGenerator.getHelp()));
	}
	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setIn(originalIn);
	}
	
}
