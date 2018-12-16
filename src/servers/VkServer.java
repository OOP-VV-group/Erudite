package servers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonException;
import javax.net.ssl.HttpsURLConnection;

import chatBot.Erudite;

public class VkServer 
{
	private static String requestKey;
	private static String requestServer;
	
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
        System.out.println(requestTs); // debug
        
        // while (true)
        URL requestUrl = VkServer.makeRequestUrl(requestTs); //temp
        HttpsURLConnection requestConn = 
        		(HttpsURLConnection)requestUrl.openConnection();
        BufferedReader requestBR = new BufferedReader(new InputStreamReader(
        	requestConn.getInputStream()));
        
        jsonReader = Json.createReader(requestBR);
        JsonObject requestJO = jsonReader.readObject();
        if (requestJO.containsKey("failed"))
        	throw new RuntimeException(requestJO.toString());
        requestTs = requestJO.getString("ts");
        
        
        System.out.println(requestJO.toString()); // debug
        
        
        Files.write(
        	Paths.get(System.getProperty("user.dir") + "\\src\\servers\\server_data"), 
        	("last_ts=" + requestTs).getBytes(),
        	StandardOpenOption.WRITE);
        getServerBufferedReader.close();
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
