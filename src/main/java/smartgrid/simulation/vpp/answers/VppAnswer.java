package smartgrid.simulation.vpp.answers;

import java.util.HashMap;

import akka.basicMessages.AnswerContent;
public class VppAnswer implements AnswerContent {
	
	private HashMap<String, Double> dataMap = new HashMap<String, Double>();
	
	public VppAnswer() {
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
