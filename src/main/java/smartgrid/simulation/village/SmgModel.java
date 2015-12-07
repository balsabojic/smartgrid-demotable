package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import akka.systemActors.GlobalTime;
import helper.lastProfil.ConsumptionProfleFortiss;
import helper.lastProfil.ProductionProfleFortiss;
import resultSaving.NoSave;
import smartgrid.simulation.village.answers.SmgAnswer;
import topology.ActorTopology;

public class SmgModel extends BasicVillageModel {
	
	private String name;
	private LocalDateTime time;
	private SmgAnswer answer;
	
	public SmgModel(String name, String smgUrl) {
		this.name = name;
		this.answer = new SmgAnswer(name, smgUrl);
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
		// Test URL
		//String requestURL = "/api/health/online";
		
//		String requestConsumption = "/api/openhab/items/dummy.wrapper.dummy_consumption/type/ConsumptionPowermeter";
//		String result = answer.sendRequest(requestConsumption);
//		if (result != null && !result.equals("")) {
//			answer.setConsumption(Double.valueOf(result) / 1000);
//		}
//		else {
//			answer.setConsumption(0.0);
//		}
//		
//		
//		String requestProduction = "/api/openhab/items/dummy.wrapper.dummy_generation/type/ProductionPowermeter";
//		result = answer.sendRequest(requestProduction);
//		if (result != null && !result.equals("")) {
//			answer.setProduction(Double.valueOf(result) / 1000);
//		}
//		else {
//			answer.setProduction(0.0);
//		}
//		
//		String requestBattery = "/api/openhab/items/dummy.wrapper.dummy_battery/type/Battery";
//		result = answer.sendRequest(requestBattery);
//		if (result != null && !result.equals("")) {
//			answer.setBatteryCapacity(Double.valueOf(result));
//		}
//		else{
//			answer.setBatteryCapacity(0.0);
//		}
		// SMG Profiles when not using live data
		LocalDateTime time = GlobalTime.currentTime;
		Double production = ProductionProfleFortiss.getLoadFortissProduction(time);
		answer.setProduction(production / 1000);		
		Double consumption = ConsumptionProfleFortiss.getLoadFortissConsumption(time);
		answer.setConsumption(consumption / 1000);
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
