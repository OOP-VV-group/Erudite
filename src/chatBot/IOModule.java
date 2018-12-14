package chatBot;

public interface IOModule {
	void close();
	int getId();
	void sendBotMessages(String[] messages);
	String[] collectUserMessages();
}
