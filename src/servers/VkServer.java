package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonException;
import javax.net.ssl.HttpsURLConnection;

public class VkServer {
	public static void main(String[] args) 
			throws MalformedURLException, IOException {
		URL getLongPollServerUrl = new URL(
				"https://api.vk.com/method/" +
				"groups.getLongPollServer?" +
				"group_id=175288971&" +
				"access_token=097e0d17214a1f95a90b7a4cfbaf44b64093e3ce1a6b997d5b043dd13828b" +
				"9fdf7ca2a0606e188c8daf85&" +
				"v=5.92");
        HttpsURLConnection getServerConnection = 
        		(HttpsURLConnection)getLongPollServerUrl.openConnection();
        BufferedReader getServerBufferedReader = new BufferedReader(
        		new InputStreamReader(getServerConnection.getInputStream()));
        
        JsonReader jsonReader = Json.createReader(getServerBufferedReader);
        JsonObject getServerJsonObj = jsonReader.readObject().getJsonObject("response");
        
        System.out.println(getServerJsonObj.toString()); // debug
        
        if (!VkServer.jsonObjContainsKeys(getServerJsonObj, new String[] { "key", "server", "ts" })) 
        	throw new JsonException(getServerJsonObj.toString() + " : getServerJsonObj does not " + 
        			"contain needed keys"); 
        	
        
        
        getServerBufferedReader.close();
	}
	
	private static boolean jsonObjContainsKeys(JsonObject jsonObj, String[] keys) {
		for (String key : keys)
			if (!jsonObj.containsKey(key))
				return false;
		return true;
	}
}
