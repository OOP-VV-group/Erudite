package chatBot;

public interface IOModule {
	void openIO();
	void closeIO();
	int getId();
	void sendBotMessages(String[] messages);
	String[] collectUserMessages();
}
