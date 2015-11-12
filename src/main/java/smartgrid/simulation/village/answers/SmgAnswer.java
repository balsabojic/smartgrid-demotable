package smartgrid.simulation.village.answers;

import java.util.Date;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import akka.basicMessages.AnswerContent;

public class SmgAnswer implements AnswerContent {
	
	private String name;
	
	private String smgUrl;
	
	// Default admin user data taken from the database
	private String accessKey = "68a45057-5734-4dad-9f86-ab9e32c4506e";
	private String secretKey = "jg9e65dui5272c45uds3qrf3b8gc71crjq4raq43";
	
	private double consumption;
	private double production;
	private double batteryCapacity;
	
	public class DobuleCommand {
		public class Command {
			public String time;
			public String value;
		}
		@SerializedName("double")
		public Command command = new Command();
	}
	
	public SmgAnswer(String name, String smgUrl) {
		this.name = name;
		this.batteryCapacity = 0;
		this.smgUrl = smgUrl;
	}
	
	public Double parseJsonToObject(String json) {
		Gson gson = new Gson();
		DobuleCommand doubleCommand = gson.fromJson(json, DobuleCommand.class);
		String valueString = doubleCommand.command.value;
		Double value = Double.parseDouble(valueString);
		return value;
	}
	
	public void sendData(String requestURL) {
		requestURL = smgUrl + requestURL;
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

			ClientResponse response = webResource.accept("application/json").post(ClientResponse.class);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	public String sendRequest(String requestURL) {
		requestURL = smgUrl + requestURL;
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
			
			return signature;
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return "";
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(String json) {
		// Data in kW
		this.consumption = parseJsonToObject(json) / 1000;
	}
	
	public void setConsumption(Double consumption) {
		// Data in kW
		this.consumption = consumption;
	}
	
	public double getProduction() {
		return production;
	}

	public void setProduction(String json) {
		// Data in kW
		this.production = parseJsonToObject(json) / 1000;
	}
	
	public void setProduction(Double production) {
		// Data in kW
		this.production = production;
	}

	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(String json) {
		this.batteryCapacity = parseJsonToObject(json);
	}
	
	public void setBatteryCapacity(Double battery) {
		this.batteryCapacity = battery;
	}

	public String getName() {
		return name;
	}

	public String getSmgUrl() {
		return smgUrl;
	}

	public void setSmgUrl(String smgUrl) {
		this.smgUrl = smgUrl;
	}
}
