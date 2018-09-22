package chatBot;

public class IOConsole implements IOModule{
	static int idCounter = 0;
	int id;
	
	IOConsole() {
		this.id = IOConsole.idCounter++;
	}
	
	public int getId() {
		return this.id;
	}

	public void sendBotMessages(String[] messages) {
		for (String message : messages)
			System.out.println(message);
	}

	public String[] collectUserMessages() { // WIP
		return new String[] {}; 
	}
	
}
