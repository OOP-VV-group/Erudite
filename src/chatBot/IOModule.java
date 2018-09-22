package chatBot;

public interface IOModule {
	int getId();
	void sendBotMessages(String[] messages);
	String[] collectUserMessages();
}