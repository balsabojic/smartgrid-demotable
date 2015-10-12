package smartgrid.simulation.vpp;

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
import smartgrid.simulation.vpp.answers.SolarAnswer;
import smartgrid.simulation.vpp.answers.VppAnswer;
import smartgrid.simulation.vpp.answers.WindAnswer;
import topology.ActorTopology;

public class VppProfileAggregator extends BasicVppModel {
	
	private String name;
	private VppAnswer answer;
	
	private LinkedList<BasicVppModel> listVppProfiles = new LinkedList<BasicVppModel>();
	
	public VppProfileAggregator(String name) {
		this.name = name;
		this.answer = new VppAnswer();
	}
	
	@Override
	public void init(LocalDateTime time) {
		for (BasicVppModel profile: listVppProfiles) {
			profile.init(time);
		}
	}
	
	public void addProfile(BasicVppModel profile) {
		listVppProfiles.add(profile);
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
		for (BasicAnswer vpp: super.answerListReceived) {
			if (vpp.answerContent instanceof SolarAnswer) {
				SolarAnswer answer = (SolarAnswer) vpp.answerContent;
				this.answer.updateValue(answer.getName(), answer.getProduction());
				System.out.println("******** 1 " + answer.getName() + " ******** " + answer.getProduction());
			}
			else if (vpp.answerContent instanceof WindAnswer) {
				WindAnswer answer = (WindAnswer) vpp.answerContent;
				this.answer.updateValue(answer.getName(), answer.getProduction());
				System.out.println("******** 2 " + answer.getName() + " ******** " + answer.getProduction());
			}
		}
	}

	public LinkedList<BasicVppModel> getListVppProfiles() {
		return listVppProfiles;
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
		for (BasicVppModel profile: listVppProfiles) {
			profile.addActor(topology);
		}
	}

}
