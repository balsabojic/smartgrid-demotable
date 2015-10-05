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
import smartgrid.simulation.vpp.answers.SolarAnswer;
import topology.ActorTopology;

public class SolarModel extends BasicVppModel{
	
	private LocalDateTime time;
	private double initPower;
	private SolarAnswer answer;
	private String name;
	
	public SolarModel(String name) {
		this.answer = new SolarAnswer(name);
		this.name = name;
	}
	
	// Set the power in KW 1000 x power
	@Override
	public void init(LocalDateTime time, int initPower) {
		this.time = time;
		this.initPower = initPower;
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
		double actualPower = initPower * Swmcsv.getSWMProfileSolar(time);
		//Get value in kW
		//actualPower *= 0.001;
		
		// Fluctuation for some percent (when we get value for one part of the time then we want to change it a bit.
    	double variation = 2 * Math.random() - 1.0; // also zwischen +1 und -1
    	double prozent = 0.02;
    	double currentPower =  actualPower + actualPower*variation*prozent;
		
		answer.setProduction(currentPower);
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
