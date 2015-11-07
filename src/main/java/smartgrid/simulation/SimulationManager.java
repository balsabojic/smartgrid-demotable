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
import akka.systemActors.GlobalTime;
import behavior.BehaviorModel;
import resultSaving.NoSave;
import simulation.SimulationStarter;
import smartgrid.server.transport.TransportData;
import smartgrid.simulation.arduino.ArduinoClient;
import smartgrid.simulation.factory.ProfileFactory;
import smartgrid.simulation.factory.ProfileFactoryOne;
import smartgrid.simulation.factory.ProfileFactoryThree;
import smartgrid.simulation.factory.ProfileFactoryTwo;
import smartgrid.simulation.factory.Village;
import smartgrid.simulation.factory.Vpp;
import smartgrid.simulation.strategy.StrategyManager;
import smartgrid.simulation.village.answers.VillageAnswer;
import smartgrid.simulation.vpp.answers.VppAnswer;
import topology.ActorTopology;

public class SimulationManager extends BehaviorModel implements Runnable{

	private Simulation simulation;
	private ActorTopology topology;
	private String simulationName;
	private ProfileFactory factory;
	
	private PrintWriter output;
	
	private ArduinoClient arduinoClient;
	
	public SimulationManager(PrintWriter output, String simulationName, ArduinoClient arduinoClient) {
		this.output = output;
		this.simulationName = simulationName;
		this.topology = new ActorTopology("Simulation");
		this.arduinoClient = arduinoClient;
	}
	
	public void stopSimulation() {
		SimulationStarter.stopSimulation();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Stop Arduino devices
		sendArduinoSignal("1.3", 0);
		sendArduinoSignal("0.2", 0);
		sendArduinoSignal("2.4", 0);
		
		// Sometimes Arduino doesn't stop sensors because of the delay and therefore we are sending two requests
		sendArduinoSignal("1.3", 0);
		sendArduinoSignal("0.2", 0);
		sendArduinoSignal("2.4", 0);
		
	}
	
	private void sendArduinoSignal(String name, int value) {
		try {
			Thread.sleep(400);
			arduinoClient.setSensorValue(name, value);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		topology.addActor(simulationName, this.createActor());
		
		switch (simulationName)  {
		case "simA":
		    simulation = new Simulation(simulationName, LocalDateTime.of(2013,8,6,12,0), 
		    		LocalDateTime.of(2013,8,6,20,0), Duration.ofMinutes(5));
			factory = new ProfileFactoryOne();
			break;
		case "simB":
			simulation = new Simulation(simulationName, LocalDateTime.of(2014,11,1,12,0), 
		    		LocalDateTime.of(2014,11,1,20,0), Duration.ofMinutes(5));
			factory = new ProfileFactoryTwo();
			break;
		case "simC":
			simulation = new Simulation(simulationName, LocalDateTime.of(2013,8,6,20,00), 
		    		LocalDateTime.of(2013,8,7,6,30), Duration.ofMinutes(15));
			factory = new ProfileFactoryThree();
			break;
		}
		
		Vpp vpp = factory.createVPP(simulationName, topology);
		vpp.init();
		vpp.startActors();
		
		Village village = factory.createVillage(simulationName, topology);
		village.init();
		village.startActors();
		
		simulation.setCurrentTime(GlobalTime.currentTime);
		
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
		
		double solarProduction = 0;
		double windProduction = 0;
		double biogasProduction = 0;
		
		int streetValue = 0;
		
		for (BasicAnswer profile: super.answerListReceived) {
			if (profile.answerContent instanceof VppAnswer) {
				VppAnswer answer = (VppAnswer) profile.answerContent;
				HashMap<String, Double> map = answer.getDataMap();
				simulation.setVppData(map);
				for (Entry<String, Double> entry: map.entrySet()) {
					System.out.println("-----------" + entry.getKey() + " : " + entry.getValue() + "-------------");
					production += entry.getValue();
					if (entry.getKey().contains("solar")) {
						solarProduction = entry.getValue();
					}
					else if (entry.getKey().contains("wind")) {
						windProduction = entry.getValue();
					}
					else if (entry.getKey().contains("bio")) {
						biogasProduction = entry.getValue();
					}
				}
			}
			else if (profile.answerContent instanceof VillageAnswer) {
				VillageAnswer answer = (VillageAnswer)profile.answerContent;
				HashMap<String, Double> map = answer.getDataMap();
				simulation.setVillageData(map);
				simulation.setSmgData(answer.getSmgMap());
				for (Entry<String, Double> entry: map.entrySet()) {
					if (entry.getKey().contains("street")) {
						streetValue = entry.getValue().intValue();
					}
					consumption += entry.getValue();
				}
			}
		}
		
		int lightSensor = (int)arduinoClient.getSensorValue("12.1");
		
		StrategyManager strategyManager = new StrategyManager(lightSensor, solarProduction, windProduction, biogasProduction, consumption);
		strategyManager.optimizeProduction();
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Solar production: " + strategyManager.getSolarProduction());
		System.out.println("Solar sensor: " + strategyManager.getSolarSensor());
		System.out.println("Wind production: " + strategyManager.getWindProduction());
		System.out.println("Wind sensor: " + strategyManager.getWindSensor());
		System.out.println("Bio production: " + strategyManager.getBioProduction());
		System.out.println("Bio sensor: " + strategyManager.getBioSensor());
		System.out.println("Consumption: " + strategyManager.getConsumption());
		System.out.println("++++++++++++++++++++++++++++++++++++++++");
		
		if (streetValue == 0) {
			sendArduinoSignal("2.4", 0);
		}
		else {
			sendArduinoSignal("2.4", 1);
		}
		
		sendArduinoSignal("1.3", (int)(strategyManager.getWindSensor()/10));
		sendArduinoSignal("0.2", (int)(strategyManager.getBioSensor()/10));
		
		HashMap<String, Double> vppData = simulation.getVppData();
		for (Entry<String, Double> entry: vppData.entrySet()) {
			if (entry.getKey().contains("solar")) {
				entry.setValue(strategyManager.getSolarProduction());
			}
		}
		simulation.setVppData(vppData);

		HashMap<String, Double> updatedVppData = new HashMap<String, Double>();
		updatedVppData.put("solar", strategyManager.getSolarProduction());
		updatedVppData.put("wind", strategyManager.getWindProduction());
		updatedVppData.put("biogas", strategyManager.getBioProduction());
		simulation.setVppUsedData(updatedVppData);
		
		TransportData transportData = new TransportData(simulation, production, consumption);
		production = 0;
		consumption = 0;
		
		Gson gson = new Gson();
		String sendData = gson.toJson(transportData);
		System.out.println(sendData);
		output.println(sendData);
		
		if (simulation.getProgress() >= 97) {
			// Turn off arduino when simulation is about to over
			sendArduinoSignal("1.3", 0);
			sendArduinoSignal("0.2", 0);
			sendArduinoSignal("2.4", 0);
		}
		
		try {
			Thread.sleep(1000);
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
	
}
