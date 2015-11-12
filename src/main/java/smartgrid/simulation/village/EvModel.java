package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import resultSaving.NoSave;
import smartgrid.simulation.village.answers.EvAnswer;
import topology.ActorTopology;

public class EvModel extends BasicVillageModel {
	
	private String name;
	private double initPower;
	private LocalDateTime time;
	private EvAnswer answer;
	
	public EvModel(String name, int initPower) {
		this.name = name;
		this.initPower = initPower;
		this.answer = new EvAnswer(name);
	}
	
	@Override
	public void init(LocalDateTime time) {
	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {
	}

	@Override
	public void handleRequest() {
	}

	@Override
	public void makeDecision() {
		// Here we don't have a profile, so we are always returning some value,
		// strategy manager will decide whether to use it or not depending on the overproduction
		answer.setConsumption(initPower);
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
