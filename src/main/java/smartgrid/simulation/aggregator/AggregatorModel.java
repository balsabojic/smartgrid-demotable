package smartgrid.simulation.aggregator;

import java.util.LinkedList;

import smartgrid.simulation.child.ChildAnswer;
import akka.advancedMessages.ErrorAnswerContent;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.BasicAnswer;
import akka.basicMessages.RequestContent;
import behavior.BehaviorModel;

public class AggregatorModel extends BehaviorModel {

	// CPU rating per second
	public double cpuRating = 1337;
	
	private LinkedList<Integer> listProduction;
	private LinkedList<Double> listConsumption;
	
	public AggregatorModel(LinkedList<Integer> listProduction, 
			LinkedList<Double> listConsumption) {
		this.listConsumption = listConsumption;
		this.listProduction = listProduction;
	}

	@Override
	public void handleRequest() {
	}

	@Override
	public void makeDecision() {
		
		int overallProduction = 0;
		double overallConsumption = 0;
		double demand = 0;
		
		for (BasicAnswer village: super.answerListReceived) {
		
			ChildAnswer villageData = (ChildAnswer) village.answerContent;
			overallConsumption += villageData.getConsumption();
			overallProduction += villageData.getProduction();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The overall production of the villages is: "  + overallProduction);
		System.out.println("The overall consumption of the villages is:  "  + overallConsumption);
		
		listConsumption.add(overallConsumption);
		listProduction.add(overallProduction); 
		
		demand = overallConsumption - overallProduction;
		System.out.println("Demand for additional energy is: " + demand);
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		return null;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		return null;
	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {}
}
