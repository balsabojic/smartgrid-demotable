package smartgrid.simulation.village.answers;

import java.util.HashMap;

import akka.basicMessages.AnswerContent;

public class VillageAnswer implements AnswerContent {
	
	private HashMap<String, Double> dataMap = new HashMap<String, Double>();
	
	public VillageAnswer() {
		dataMap = new HashMap<String, Double>();
	}

	public HashMap<String, Double> getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap<String, Double> dataMap) {
		this.dataMap = dataMap;
	}
	
	public void updateValue(String key, Double value) {
		dataMap.put(key, value);
	}

}