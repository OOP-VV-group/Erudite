package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;

import chatBot.Erudite;

public class VkServer 
{
	private static String requestKey;
	private static String requestServer;
	private static String EXIT_SEQUENCE = "SERVER_EXIT";
	private static HashMap<Integer, String> userMessage = new HashMap<Integer, String>();
	private static Random random = new Random(); 
	private static Erudite erudite = new Erudite();
	
	public static void main(String[] args)
			throws MalformedURLException, IOException, URISyntaxException 
	{		
		URL getLongPollServerUrl = new URL(Erudite.stringsConcat(new String[] {
			"https://api.vk.com/method/",
			"groups.getLongPollServer?",
			"group_id=175288971&",
			"access_token=097e0d17214a1f95a90b7a4cfbaf44b64093e3ce1a6b997d5b043dd13828b",
				"9fdf7ca2a0606e188c8daf85&",
			"v=5.92"}));
        HttpsURLConnection getServerConnection = 
        	(HttpsURLConnection)getLongPollServerUrl.openConnection();
        BufferedReader getServerBufferedReader = new BufferedReader(
        	new InputStreamReader(getServerConnection.getInputStream()));
        
        JsonReader jsonReader = Json.createReader(getServerBufferedReader);
        JsonObject getServerJsonObj = jsonReader.readObject();
        if (getServerJsonObj.containsKey("error"))
        	throw new RuntimeException(getServerJsonObj.toString());
        getServerJsonObj = getServerJsonObj.getJsonObject("response");
        
        VkServer.requestKey = getServerJsonObj.getString("key");
        VkServer.requestServer = getServerJsonObj.getString("server");
        String requestTs = Files.readAllLines(
        	Paths.get(System.getProperty("user.dir") + "\\src\\servers\\server_data"),
        	StandardCharsets.UTF_8).get(0).substring(8);
        
        main:
        while (true)
        {
        	if (System.in.available() > 0)
        	{
        		BufferedReader consoleBR = new BufferedReader(new InputStreamReader(System.in));
        		while (System.in.available() > 0)
        		{
	        		String consoleMessage = consoleBR.readLine().toString();
	        		if (consoleMessage.equals(EXIT_SEQUENCE))
	        			break main;
        		}
        	}
        		
	        URL requestUrl = VkServer.makeRequestUrl(requestTs);
	        HttpsURLConnection requestConn = 
	        		(HttpsURLConnection)requestUrl.openConnection();
	        BufferedReader requestBR = new BufferedReader(new InputStreamReader(
	        	requestConn.getInputStream()));
	        
	        jsonReader = Json.createReader(requestBR);
	        JsonObject requestJO = jsonReader.readObject();
	        if (requestJO.containsKey("failed"))
	        	throw new RuntimeException(requestJO.toString());
	        requestTs = requestJO.getString("ts");
	        
	        JsonArray updates = requestJO.getJsonArray("updates");
	        HashMap<Integer, String> repliedUsers = new HashMap<Integer, String>();
	        for (JsonValue update : updates)
	        {
	        	JsonObject updateObj = Json.createReader(new StringReader(update.toString())).readObject();
	        	 
	        	if (updateObj.getString("type").equals("message_new"))
	        	{
	        		int userId = updateObj.getJsonObject("object").getInt("user_id");
	        		String message = updateObj.getJsonObject("object").getString("body");
	        		repliedUsers.put(userId, message);
	        	}
	        }
	        
	        for (int userId : repliedUsers.keySet())
	        {
	        	String botMessage = new String();
	        	if (!VkServer.userMessage.containsKey(userId))
	        		botMessage = Erudite.stringsConcat(new String[] {
	        			VkServer.erudite.getStartMessage(),
	        			VkServer.erudite.getQuestion(userId)});
	        	else
	        	{
	        		String result = VkServer.erudite.checkAnswer(userId, repliedUsers.get(userId));
	        		if (result.equals("help"))
	        			botMessage = VkServer.erudite.getHelpMessage();
	        		else if (result.equals("quit"))
	        		{
	        			VkServer.removeUser(userId);
	        			continue;
	        		}
	        		else
	        			botMessage = Erudite.stringsConcat(new String[] {
	        				result,
	        				VkServer.erudite.getQuestion(userId)});
	        	}	
	        	VkServer.userMessage.put(userId, repliedUsers.get(userId));
	        	
	        	URL botMessageUrl = VkServer.makeBotMessageUrl(
	        		userId, 
	        		URLEncoder.encode(botMessage, "UTF-8"));
		        HttpsURLConnection botMessageConn = 
	        		(HttpsURLConnection)botMessageUrl.openConnection();
		        BufferedReader botMessageBR = new BufferedReader(new InputStreamReader(
		        	botMessageConn.getInputStream()));
		        
		        jsonReader = Json.createReader(botMessageBR);
		        JsonObject botMessageJO = jsonReader.readObject();
		        if (botMessageJO.containsKey("error"))
		        {
		        	System.out.println(botMessageJO.toString());
		        	VkServer.removeUser(userId);
		        }
	        }
        }
        
        Files.write(
        	Paths.get(System.getProperty("user.dir") + "\\src\\servers\\server_data"), 
        	("last_ts=" + requestTs).getBytes(),
        	StandardOpenOption.WRITE);
        getServerBufferedReader.close();
	}
	
	private static void removeUser(int userId)
	{
		if (VkServer.userMessage.containsKey(userId))
			VkServer.userMessage.remove(userId);
		VkServer.erudite.removeUser(userId);
	}
	
	private static URL makeBotMessageUrl(int userId, String message) throws MalformedURLException
	{
		return new URL(Erudite.stringsConcat(new String[] {
			"https://api.vk.com/method/",
			"messages.send?",
			"user_id=", Integer.toString(userId), "&",
			"random_id=", Integer.toString(random.nextInt(1000000)), "&",
			"message=", message, "&",
			"access_token=097e0d17214a1f95a90b7a4cfbaf44b64093e3ce1a6b997d5b043dd13828b",
				"9fdf7ca2a0606e188c8daf85&",
			"v=5.92"}));
	}
	
	private static URL makeRequestUrl(String requestTs) throws MalformedURLException 
	{
		if (VkServer.requestKey == null || VkServer.requestServer == null)
			throw new NullPointerException("Request's server or key is/are not initialized");
		
		return new URL(Erudite.stringsConcat(new String[] {
			VkServer.requestServer, "?", 
			"act=a_check&",
			"key=", VkServer.requestKey, "&", 
			"ts=", requestTs, "&", 
			"wait=25"}));
	}
}
