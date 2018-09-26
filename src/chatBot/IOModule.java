package chatBot;

public interface IOModule {
	void openIO();
	void closeIO();
	void sendBotMessages(String[] messages);
	String[] collectUserMessages();
}
