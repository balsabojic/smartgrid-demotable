package smartgrid.simulation.village.answers;

import akka.basicMessages.AnswerContent;

public class HouseAnswer implements AnswerContent {
	
	private double consumption;
	private String name;
	
	public HouseAnswer(String name) {
		this.name = name;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public String getName() {
		return name;
	}
}
