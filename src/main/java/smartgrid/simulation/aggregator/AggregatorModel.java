package smartgrid.simulation.aggregator;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.Gson;

import smartgrid.simulation.child.ChildAnswer;
import akka.advancedMessages.ErrorAnswerContent;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.BasicAnswer;
import akka.basicMessages.RequestContent;
import akka.systemActors.GlobalTime;
import behavior.BehaviorModel;

public class AggregatorModel extends BehaviorModel {

	// CPU rating per second
	public double cpuRating = 1337;
	
	private LinkedList<Integer> listProduction;
	private LinkedList<Double> listConsumption;
	
	private PrintWriter output;
	
	public AggregatorModel(PrintWriter output) {
		this.listConsumption = new LinkedList<Double>();
		this.listProduction = new LinkedList<Integer>();
		this.output = output;
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
		
		System.out.println("The overall production of the villages is: "  + overallProduction);
		System.out.println("The overall consumption of the villages is:  "  + overallConsumption);
		
		listConsumption.add(overallConsumption);
		listProduction.add(overallProduction); 
		
		demand = overallConsumption - overallProduction;
		System.out.println("Demand for additional energy is: " + demand);
		
		LocalDateTime time = GlobalTime.currentTime;
		Gson gson = new Gson();

		if (listConsumption.size() != 0 && listProduction.size() != 0) {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("consumption", listConsumption.getLast());
			data.put("production", listProduction.getLast());
			data.put("time", time);
			String sendData = gson.toJson(data);
			System.out.println(sendData);
			output.println(sendData);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
