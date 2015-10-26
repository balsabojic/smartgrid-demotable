package smartgrid.simulation;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gson.Gson;

import akka.actor.ActorSystem;
import akka.advancedMessages.ErrorAnswerContent;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;
import akka.basicMessages.AnswerContent;
import akka.basicMessages.BasicAnswer;
import akka.basicMessages.RequestContent;
import behavior.BehaviorModel;
import resultSaving.NoSave;
import simulation.SimulationStarter;
import smartgrid.server.transport.TransportData;
import smartgrid.simulation.factory.ProfileFactory;
import smartgrid.simulation.factory.ProfileFactoryOne;
import smartgrid.simulation.factory.Village;
import smartgrid.simulation.factory.Vpp;
import smartgrid.simulation.village.answers.VillageAnswer;
import smartgrid.simulation.vpp.answers.VppAnswer;
import topology.ActorTopology;

public class SimulationManager extends BehaviorModel implements Runnable{

	private Simulation simulation;
	private ActorTopology topology;
	private String simulationName;
	private ProfileFactory factory;
	
	private LinkedList<PrintWriter> outputList;
	
	public SimulationManager(String simulationName) {
		this.simulationName = simulationName;
		this.topology = new ActorTopology("Simulation");
		this.outputList = new LinkedList<PrintWriter>();	
	}
	
	public void stopSimulation() {
		SimulationStarter.stopSimulation();
	}

	@Override
	public void run() {
		topology.addActor(simulationName, this.createActor());
		
		switch (simulationName)  {
		case "simA":
		    simulation = new Simulation(simulationName, LocalDateTime.of(2014,7,1,12,0), 
		    		LocalDateTime.of(2014,7,1,20,0), Duration.ofMinutes(5));
			factory = new ProfileFactoryOne();
			break;
		case "simB":
			break;
		case "simC":
			break;
		}
		
		Vpp vpp = factory.createVPP(simulationName, topology);
		vpp.init();
		vpp.startActors();
		
		Village village = factory.createVillage(simulationName, topology);
		village.init();
		village.startActors();
		
		simulation.setVpp(vpp);
		simulation.setVillage(village);
		
		SimulationStarter.saveGridTopologyPlot(topology);   
		ActorSystem actorSystem = SimulationStarter.initialiseActorSystem(topology);
        SimulationStarter.startSimulation(actorSystem, simulation.getStartDate(), simulation.getEndDate(), simulation.getTimeInterval());
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
		double production = 0;
		double consumption = 0;
		for (BasicAnswer profile: super.answerListReceived) {
			if (profile.answerContent instanceof VppAnswer) {
				VppAnswer answer = (VppAnswer) profile.answerContent;
				HashMap<String, Double> map = answer.getDataMap();
				simulation.setVppData(map);
				for (Entry<String, Double> entry: map.entrySet()) {
					System.out.println("-----------" + entry.getKey() + " : " + entry.getValue() + "-------------");
					production += entry.getValue();
				}
			}
			else if (profile.answerContent instanceof VillageAnswer) {
				VillageAnswer answer = (VillageAnswer)profile.answerContent;
				HashMap<String, Double> map = answer.getDataMap();
				simulation.setVillageData(map);
				for (Entry<String, Double> entry: map.entrySet()) {
					System.out.println("-----------" + entry.getKey() + " : " + entry.getValue() + "-------------");
					consumption += entry.getValue();
				}
			}
		}
		
		TransportData transportData = new TransportData(simulation, 
				simulation.getOverallProduction(), simulation.getOverallConsumption());
		
		Gson gson = new Gson();
		String sendData = gson.toJson(transportData);
		System.out.println(sendData);
		for (PrintWriter output: outputList) {
			output.println(sendData);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ActorOptions createActor() {
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				this, new NoSave());		
		return result;
	}

	public Simulation getSimulation() {
		return simulation;
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public void addOutput(PrintWriter output) {
		outputList.add(output);
	}
	
}
