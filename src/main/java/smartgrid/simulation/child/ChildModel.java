package smartgrid.simulation.child;

import helper.standardLastProfil.StandardLastProfil;

import java.util.LinkedList;
import java.util.Random;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.RequestContent;
import akka.systemActors.GlobalTime;
import behavior.BehaviorModel;

public class ChildModel extends BehaviorModel {
	
	public ChildAnswer answerContentToSend = new ChildAnswer();

	@Override
	public void handleRequest() {
	}

	@Override
	public void makeDecision() {
		double currentConsumption = StandardLastProfil.getH0Demand(9000, GlobalTime.currentTime);
		//int currentConsumption = (int) (Math.random() * 256);
		Random random = new Random();
		int currentProduction = random.nextInt(3 - 1) + 1;
		
		answerContentToSend.setConsumption(currentConsumption); 
		answerContentToSend.setProduction(currentProduction);
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		return answerContentToSend;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		return null;
	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {
		
	}
}
