package smartgrid.simulation.village.answers;

import java.util.HashMap;

import akka.basicMessages.AnswerContent;
import smartgrid.simulation.village.SmgModel;

public class VillageAnswer implements AnswerContent {
	
	private HashMap<String, Double> dataMap = new HashMap<String, Double>();
	private HashMap<String, SmgAnswer> smgMap = new HashMap<String, SmgAnswer>();
	
	public VillageAnswer() {
		dataMap = new HashMap<String, Double>();
		smgMap = new HashMap<String, SmgAnswer>();
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

	public HashMap<String, SmgAnswer> getSmgMap() {
		return smgMap;
	}

	public void setSmgMap(HashMap<String, SmgAnswer> smgMap) {
		this.smgMap = smgMap;
	}
	
	public void updateSmg(String key, SmgAnswer answer) {
		smgMap.put(key, answer);
	}
}