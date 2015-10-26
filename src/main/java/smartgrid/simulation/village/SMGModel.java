package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.Date;
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
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import topology.ActorTopology;

public class SMGModel extends BasicVillageModel {

	@Override
	public void init(LocalDateTime time) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActorOptions createActor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addActor(ActorTopology topology) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub

	}
	
	public static void main (String[] args){
		
//		String requestURL = "http://localhost:8091/api/health/online";
		String requestURL = "http://localhost:8091/api/openhab/items/sunny.wrapper.battery_voltage/state";
//		String requestURL = "http://localhost:8091/api/openhab/getJSONFile";
		
		String accessKey = "68a45057-5734-4dad-9f86-ab9e32c4506e";
		String secretKey = "jg9e65dui5272c45uds3qrf3b8gc71crjq4raq43";

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
			
			ClientResponse response = webResource.accept("application/json")
	                   .get(ClientResponse.class);

			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}

			String output = response.getEntity(String.class);

			System.out.println("Output from Server .... \n");
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }
	}
	
	protected static String getSignature(String secretKey, String requestURL) {
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
	public void makeDecision() {
		
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}

}
