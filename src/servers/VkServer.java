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
	private static Random random = new Random();
	private static String EXIT_SEQUENCE = "SERVER_EXIT"; 
	private String requestKey;
	private String requestServer;
	private HashMap<Integer, String> userMessage = new HashMap<Integer, String>();
	private Erudite erudite = new Erudite();
	
	public static void main(String[] args)
			throws MalformedURLException, IOException 
	{
		VkServer vkServer = new VkServer();
		
        JsonReader getServerJR = vkServer.createJsonReaderForHttpsRequest(
        	Erudite.stringsConcat(new String[] {
    			"https://api.vk.com/method/",
    			"groups.getLongPollServer?",
    			"group_id=175288971&",
    			"access_token=097e0d17214a1f95a90b7a4cfbaf44b64093e3ce1a6b997d5b043dd13828b",
    				"9fdf7ca2a0606e188c8daf85&",
    			"v=5.92"}));
        JsonObject getServerJsonObj = getServerJR.readObject();
        if (getServerJsonObj.containsKey("error"))
        	throw new RuntimeException(getServerJsonObj.toString());
        getServerJsonObj = getServerJsonObj.getJsonObject("response");
        getServerJR.close();
        
        vkServer.requestKey = getServerJsonObj.getString("key");
        vkServer.requestServer = getServerJsonObj.getString("server");
        String requestTs = Files.readAllLines(
        	Paths.get(System.getProperty("user.dir") + "\\src\\servers\\server_data"),
        	StandardCharsets.UTF_8).get(0).substring(8);
        
        while (true)
        {
        	if (VkServer.exitSequencePresented())
        		break;
        	
	        JsonReader requestJR = vkServer.createJsonReaderForHttpsRequest(
	        	vkServer.makeRequestUrlString(requestTs));
	        JsonObject requestJO = requestJR.readObject();
	        if (requestJO.containsKey("failed"))
	        	throw new RuntimeException(requestJO.toString());
	        requestTs = requestJO.getString("ts");
	        requestJR.close();
	        
	        HashMap<Integer, String> repliedUsers = VkServer.getRepliedUsersFromUpdates(
	        	requestJO.getJsonArray("updates"));
	        
	        for (int userId : repliedUsers.keySet())
	        {
	        	String botMessage;
	        	try
	        	{
	        		botMessage = vkServer.initiateEruditeLogic(userId, repliedUsers);
	        	}
	        	catch (IOException catchedException)
	        	{
	        		if (catchedException.getMessage().equals("User quited"))
	        			continue;
	        		throw catchedException;
	        	}
	        	vkServer.userMessage.put(userId, repliedUsers.get(userId));
	        	
		        JsonReader botMessageJR = vkServer.createJsonReaderForHttpsRequest(
		        	VkServer.makeBotMessageUrl(
		        		userId, 
		        		URLEncoder.encode(botMessage, "UTF-8")));
		        JsonObject botMessageJO = botMessageJR.readObject();
		        if (botMessageJO.containsKey("error"))
		        {
		        	System.out.println(botMessageJO.toString());
		        	vkServer.removeUser(userId);
		        }
		        botMessageJR.close();
	        }
        }
        
        Files.write(
        	Paths.get(System.getProperty("user.dir") + "\\src\\servers\\server_data"), 
        	("last_ts=" + requestTs).getBytes(),
        	StandardOpenOption.WRITE);
	}
	
	private static String makeBotMessageUrl(int userId, String message) 
			throws MalformedURLException
	{
		return Erudite.stringsConcat(new String[] {
			"https://api.vk.com/method/",
			"messages.send?",
			"user_id=", Integer.toString(userId), "&",
			"random_id=", Integer.toString(VkServer.random.nextInt(1000000)), "&",
			"message=", message, "&",
			"access_token=097e0d17214a1f95a90b7a4cfbaf44b64093e3ce1a6b997d5b043dd13828b",
				"9fdf7ca2a0606e188c8daf85&",
			"v=5.92"});
	}
	
	private static boolean exitSequencePresented() throws IOException
	{
		if (System.in.available() > 0)
    	{
    		BufferedReader consoleBR = new BufferedReader(new InputStreamReader(System.in));
    		while (System.in.available() > 0)
    		{
        		String consoleMessage = consoleBR.readLine().toString();
        		if (consoleMessage.equals(EXIT_SEQUENCE))
        			return true;
    		}
    	}
		return false;
	}
	
	private static HashMap<Integer, String> getRepliedUsersFromUpdates(JsonArray updates)
	{
		HashMap<Integer, String> repliedUsers = new HashMap<Integer, String>();
        for (JsonValue update : updates) 
        {
        	JsonObject updateObj = Json.createReader(new StringReader(update.toString())).readObject();
        	
        	if (updateObj.getString("type").equals("message_new"))
        		repliedUsers.put(
        			updateObj.getJsonObject("object").getInt("user_id"), 
        			updateObj.getJsonObject("object").getString("body"));
        }
        
        return repliedUsers;
	}
	
	private String initiateEruditeLogic(int userId, HashMap<Integer, String> repliedUsers) throws IOException
	{
		String botMessage = new String();
		if (!this.userMessage.containsKey(userId))
    		botMessage = Erudite.stringsConcat(new String[] {
    			this.erudite.getStartMessage(),
    			this.erudite.getQuestion(userId)});
    	else
    	{
    		String result = this.erudite.checkAnswer(userId, repliedUsers.get(userId));
    		if (result.equals("help"))
    			botMessage = this.erudite.getHelpMessage();
    		else if (result.equals("quit"))
    		{
    			this.removeUser(userId);
    			throw new IOException("User quited");
    		}
    		else
    			botMessage = Erudite.stringsConcat(new String[] {
    				result,
    				this.erudite.getQuestion(userId)});
    	}
		
		return botMessage;
	}
	
	private void removeUser(int userId)
	{
		if (this.userMessage.containsKey(userId))
			this.userMessage.remove(userId);
		this.erudite.removeUser(userId);
	}
	
	private JsonReader createJsonReaderForHttpsRequest(String requestUrlString) 
			throws IOException
	{
		URL requestUrl = new URL(requestUrlString);
        HttpsURLConnection requestConn = 
        	(HttpsURLConnection)requestUrl.openConnection();
        BufferedReader requestBR = new BufferedReader(
        	new InputStreamReader(requestConn.getInputStream()));
        
        return Json.createReader(requestBR);
	}
	
	private String makeRequestUrlString(String requestTs) throws MalformedURLException 
	{
		if (this.requestKey == null || this.requestServer == null)
			throw new NullPointerException("Request's server or key is/are not initialized");
		
		return Erudite.stringsConcat(new String[] {
			this.requestServer, "?", 
			"act=a_check&",
			"key=", this.requestKey, "&", 
			"ts=", requestTs, "&", 
			"wait=25"});
	}
}
