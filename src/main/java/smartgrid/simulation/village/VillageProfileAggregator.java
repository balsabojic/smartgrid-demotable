package smartgrid.simulation.village;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;

import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.BasicAnswer;
import akka.basicMessages.RequestContent;
import resultSaving.NoSave;
import scala.collection.mutable.HashMap;
import smartgrid.simulation.village.answers.HouseAnswer;
import smartgrid.simulation.village.answers.VillageAnswer;
import topology.ActorTopology;

public class VillageProfileAggregator extends BasicVillageModel {
	
	private String name;
	private LinkedList<BasicVillageModel> listVillageProfiles = new LinkedList<BasicVillageModel>();
	private VillageAnswer answer;
	private HashMap<String, Double> dataMap = new HashMap<String, Double>();
	private double consumption = 0;
	
	public VillageProfileAggregator(String name) {
		this.name = name;
		this.answer = new VillageAnswer();
	}

	@Override
	public void init(LocalDateTime time) {
		for (BasicVillageModel profile: listVillageProfiles) {
			profile.init(time);
		}
	}
	
	public void addProfile(BasicVillageModel profile) {
		listVillageProfiles.add(profile);
	}

	@Override
	public void handleError(LinkedList<ErrorAnswerContent> errors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void makeDecision() {
		for (BasicAnswer village: super.answerListReceived) {
			if (village.answerContent instanceof HouseAnswer) {
				HouseAnswer answer = (HouseAnswer) village.answerContent;
				dataMap.put(answer.getName(), answer.getConsumption());
				consumption += answer.getConsumption();
				this.answer.updateValue(answer.getName(), answer.getConsumption());
				System.out.println("******** 3 " + answer.getName() + " ******** " + answer.getConsumption());
			}
		}
		
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		return answer;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HashMap<String, Double> getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap<String, Double> dataMap) {
		this.dataMap = dataMap;
	}

	public LinkedList<BasicVillageModel> getListVillageProfiles() {
		return listVillageProfiles;
	}

	public double getConsumption() {
		return consumption;
	}
	
	@Override
	public ActorOptions createActor() {
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				this, new NoSave());		
		return result;
	}

	@Override
	public void addActor(ActorTopology topology) {
		topology.addActorAsChild(name, this.createActor());
		for (BasicVillageModel profile: listVillageProfiles) {
			profile.addActor(topology);
		}
	}
}
