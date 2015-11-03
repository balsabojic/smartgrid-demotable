package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import resultSaving.NoSave;
import smartgrid.simulation.village.answers.SmgAnswer;
import topology.ActorTopology;

public class SmgModel extends BasicVillageModel {
	
	String accessKey = "68a45057-5734-4dad-9f86-ab9e32c4506e";
	String secretKey = "jg9e65dui5272c45uds3qrf3b8gc71crjq4raq43";
	
	private String name;
	private LocalDateTime time;
	private SmgAnswer answer;
	
	public SmgModel(String name) {
		this.name = name;
		this.answer = new SmgAnswer(name);
	}

	@Override
	public void init(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void makeDecision() {
		String requestConsumption = "http://localhost:8091/api/openhab/items/solarlog.wrapper.solar_feed-in_watt/state";
		String result = sendRequest(requestConsumption);
		System.out.println("SMG data: " + result);
		answer.setConsumption(result);
		
		String requestBattery = "http://localhost:8091/api/openhab/items/sunny.wrapper.battery_percentage/state";
		result = sendRequest(requestBattery);
		System.out.println("SMG data: " + result);
		answer.setBatteryCapacity(result);
		
		String requestProduction = "http://localhost:8091/api/openhab/items/solarlog.wrapper.solar_feed-in_watt/state";
		result = sendRequest(requestProduction);
		System.out.println("SMG data: " + result);
		answer.setProduction(result);
	}
	
	private String sendRequest(String requestURL) {
		
//		String requestURL = "http://localhost:8091/api/health/online";
		// String requestURL =
		// "http://localhost:8091/api/openhab/items/sunny.wrapper.battery_percentage/state";
		// String requestURL =
		// "http://localhost:8091/api/openhab/items/sunny.wrapper.solar_generator_watt/state";
//		String requestURL = "http://localhost:8091/api/openhab/items/solarlog.wrapper.solar_feed-in_watt/state";
		// String requestURL = "http://localhost:8091/api/openhab/getJSONFile";

		if (requestURL.contains("?")) {
			requestURL = requestURL + "&accesskey=" + accessKey;
		} else {
			requestURL = requestURL + "?accesskey=" + accessKey;
		}
		requestURL = requestURL + "&timestamp=" + new Date().getTime() / 1000;
		requestURL = requestURL + "&authversion=1";
		requestURL = requestURL + "&nonce=" + new Random().nextLong();

		String signature = getSignature(secretKey, requestURL);

		requestURL = requestURL + "&signature=" + signature;

		try {

			Client client = Client.create();

			WebResource webResource = client.resource(requestURL);

			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String output = response.getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println(output);
			return output;

		} catch (Exception e) {

			e.printStackTrace();

		}
		return "";
		
	}
	
	private static String getSignature(String secretKey, String requestURL) {
		String signature = "";
		String hash = "";
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
			sha256_HMAC.init(secret_key);
			hash = Base64.encodeBase64String(sha256_HMAC.doFinal(requestURL.getBytes()));
			
			signature = java.net.URLEncoder.encode(hash, "utf8");
			System.out.println(signature);
			
			return signature;
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return "";
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		return answer;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ActorOptions createActor() {
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				this, new NoSave());		
		return result;
	}

	@Override
	public void addActor(ActorTopology topology) {
		topology.addActorAsChild(name, this.createActor());
	}

}
