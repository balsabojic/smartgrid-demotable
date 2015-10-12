package smartgrid.simulation.vpp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import helper.Swmcsv;
import resultSaving.NoSave;
import smartgrid.simulation.vpp.answers.WindAnswer;
import topology.ActorTopology;

public class WindModel extends BasicVppModel {

	private LocalDateTime time;
	private double initPower;
	private WindAnswer answer;
	private String name;

	public WindModel(String name, int initPower) {
		this.answer = new WindAnswer(name);
		this.name = name;
		this.initPower = initPower;
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
		double actualPower = initPower * Swmcsv.getSWMProfileWind(time);
		//Get value in kW
//		actualPower *= 0.001;
		answer.setProduction(actualPower);
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
