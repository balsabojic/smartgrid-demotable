package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import helper.standardLastProfil.StandardLastProfil;
import resultSaving.NoSave;
import smartgrid.simulation.village.answers.HouseAnswer;
import topology.ActorTopology;

public class HouseModel extends BasicVillageModel {
	
	private String name;
	private HouseAnswer answer;
	private LocalDateTime time;
	private double initPower;
	
	public HouseModel(String name) {
		this.name = name;
		answer = new HouseAnswer(name);
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
		answer.setConsumption(StandardLastProfil.getH0Demand(initPower, time));
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
	public void init(LocalDateTime time, int initPower) {
		this.time = time;
		this.initPower = initPower;		
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
