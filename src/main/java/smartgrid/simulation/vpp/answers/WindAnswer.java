package smartgrid.simulation.vpp.answers;

import akka.basicMessages.AnswerContent;

public class WindAnswer implements AnswerContent {
	
	private String name;
	private double production;
	
	public WindAnswer(String name) {
		this.name = name;
	}
	
	public double getProduction() {
		return production;
	}
	
	public void setProduction(double production) {
		this.production = production;
	}

	public String getName() {
		return name;
	}
}
