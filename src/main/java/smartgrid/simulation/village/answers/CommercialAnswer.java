package smartgrid.simulation.village.answers;

import akka.basicMessages.AnswerContent;

public class CommercialAnswer implements AnswerContent {
	
	private double consumption;
	private String name;
	
	public CommercialAnswer(String name) {
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
