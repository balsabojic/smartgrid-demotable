package smartgrid.simulation.child;

import akka.basicMessages.AnswerContent;

/**
 * Child AnswerContent
 * 
 * @author Balsa Bojic
 * 
 */
public class ChildAnswer implements AnswerContent  {
	private int production;
	private double consumption;
	
	public int getProduction() {
		return production;
	}
	public void setProduction(int production) {
		this.production = production;
	}
	public double getConsumption() {
		return consumption;
	}
	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}
	
	
}
