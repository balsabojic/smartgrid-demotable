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
import smartgrid.simulation.village.answers.SmgAnswer;
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
	private ArduinoClient arduinoClientSmg;
	
	public SimulationManager(PrintWriter output, String simulationName, 
			ArduinoClient arduinoClient, ArduinoClient arduinoClientSmg) {
		this.output = output;
		this.simulationName = simulationName;
		this.topology = new ActorTopology("Simulation");
		this.arduinoClient = arduinoClient;
		this.arduinoClientSmg = arduinoClientSmg;
	}
	
	public void stopSimulation() {
		SimulationStarter.stopSimulation();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Stop Arduino devices
		sendArduinoSignal(arduinoClient, "1.3", 0);
		sendArduinoSignal(arduinoClient, "0.2", 0);
		sendArduinoSignal(arduinoClient, "2.4", 0);
		
		// Sometimes Arduino doesn't stop sensors because of the delay and therefore we are sending two requests
		sendArduinoSignal(arduinoClient, "1.3", 0);
		sendArduinoSignal(arduinoClient, "0.2", 0);
		sendArduinoSignal(arduinoClient, "2.4", 0);
		
	}
	
	private void sendArduinoSignal(ArduinoClient arduino, String name, int value) {
		try {
			Thread.sleep(400);
			arduino.setSensorValue(name, value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		topology.addActor(simulationName, this.createActor());
		
		switch (simulationName)  {
		case "simA":
		    simulation = new Simulation(simulationName, LocalDateTime.of(2013,8,6,12,0), 
		    		LocalDateTime.of(2013,8,7,2,0), Duration.ofMinutes(30));
			factory = new ProfileFactoryOne();
			break;
		case "simB":
			simulation = new Simulation(simulationName, LocalDateTime.of(2013,8,6,12,0), 
		    		LocalDateTime.of(2013,8,6,20,0), Duration.ofMinutes(15));
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
	}

	@Override
	public void handleRequest() {
		
	}

	@Override
	public void makeDecision() {
		double production = 0;
		double consumption = 0;
		
		double solarProduction = 0;
		double windProduction = 0;
		double biogasProduction = 0;
		
		int streetValue = 0;
		double batteryStatus = 0;
		
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
		System.out.println("Light sensor: " + lightSensor);
		int lightSensorSmg = (int)arduinoClientSmg.getSensorValue("12.1");
		System.out.println("Light sensor SMG: " + lightSensorSmg);
		
		StrategyManager strategyManager = new StrategyManager(lightSensor, solarProduction, windProduction, 
				biogasProduction, consumption, simulation.getSmgData());
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
			sendArduinoSignal(arduinoClient, "2.4", 0);
			sendArduinoSignal(arduinoClientSmg, "3.3", 0);
		}
		else {
			// Check whether we have overproduction and need to turn on or off half of the street lights
			int streetSensor = strategyManager.getStreetSensor();
			if (streetSensor == 1) {
				// Turn on smgArdunio light
				sendArduinoSignal(arduinoClientSmg, "3.3", 1);
			}
			else {
				// Turn off smgArduino light
				// reduce the consumption - use just 50% of the light
				double streetConsumption = 0;
				for (Entry<String, Double> entry: simulation.getVillageData().entrySet()) {
					if (entry.getKey().contains("street")) {
						streetConsumption = entry.getValue();
						streetConsumption = streetConsumption / 2;
						simulation.getVillageData().put(entry.getKey(), streetConsumption);
						break;
					}
				}
				sendArduinoSignal(arduinoClientSmg, "3.3", 0);
			}
			sendArduinoSignal(arduinoClient, "2.4", 1);
		}
		
		// Set if we have EV working if we have overproduction turn it on
		if (strategyManager.getEvSensor() == 1) {
			sendArduinoSignal(arduinoClientSmg, "4.4", 1);
		}
		else {
			// If we have underproduction we need to cut the EV station from network
			for (Entry<String, Double> entry: simulation.getVillageData().entrySet()) {
				if (entry.getKey().contains("ev")) {
					simulation.getVillageData().put(entry.getKey(), 0.0);
					break;
				}
			}
			sendArduinoSignal(arduinoClientSmg, "4.4", 0);
		}
		
		// Update arduinoClient wind turbine speed
		sendArduinoSignal(arduinoClient, "1.3", (int)(strategyManager.getWindSensor()/10));
		// Update arduinoClient led bar
		sendArduinoSignal(arduinoClient, "0.2", (int)(strategyManager.getBioSensor()/10));
		
		// TODO update with the battery data from REST REQUEST
		for (Entry<String, SmgAnswer> entry: simulation.getSmgData().entrySet()) {
			if (entry.getValue() != null) {
				batteryStatus = entry.getValue().getBatteryCapacity();
				entry.getValue().sendData("/api/openhab/dummy.wrapper.dummy_generation/" + lightSensorSmg);
				break;
			}
		}
		sendArduinoSignal(arduinoClientSmg, "0.2", (int)(batteryStatus / 10));
		
		// Update simulation with new Solar VPP data depending on the light sensor from the strategy manager
		HashMap<String, Double> vppData = simulation.getVppData();
		for (Entry<String, Double> entry: vppData.entrySet()) {
			if (entry.getKey().contains("solar")) {
				entry.setValue(strategyManager.getSolarProduction());
			}
		}
		simulation.setVppData(vppData);

		// Updated simulation data with new updated VPP data (used energy) from strategy manager
		HashMap<String, Double> updatedVppData = new HashMap<String, Double>();
		updatedVppData.put("solar", strategyManager.getSolarProduction());
		updatedVppData.put("wind", strategyManager.getWindProduction());
		updatedVppData.put("biogas", strategyManager.getBioProduction());
		simulation.setVppUsedData(updatedVppData);
		
		// Update simulation data with new SMG data from strategy manager
		simulation.setSmgData(strategyManager.getSmgData());
		
		TransportData transportData = new TransportData(simulation, production, consumption);
		production = 0;
		consumption = 0;
		
		Gson gson = new Gson();
		String sendData = gson.toJson(transportData);
		System.out.println(sendData);
		output.println(sendData);
		
		if (simulation.getProgress() == 100) {
			// Turn off arduino when simulation is about to over
			sendArduinoSignal(arduinoClient, "1.3", 0);
			sendArduinoSignal(arduinoClient, "0.2", 0);
			sendArduinoSignal(arduinoClient, "2.4", 0);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public AnswerContent returnAnswerContentToSend() {
		return null;
	}

	@Override
	public RequestContent returnRequestContentToSend() {
		return null;
	}
	
	public ActorOptions createActor() {
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				this, new NoSave());		
		return result;
	}
	
}
