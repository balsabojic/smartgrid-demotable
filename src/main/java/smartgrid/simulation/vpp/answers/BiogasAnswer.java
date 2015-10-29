package smartgrid.simulation.vpp.answers;

import akka.basicMessages.AnswerContent;

public class BiogasAnswer implements AnswerContent {
	
	private String name;
	private double production;
	
	public BiogasAnswer(String name) {
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
