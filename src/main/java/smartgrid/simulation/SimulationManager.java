package smartgrid.simulation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

import akka.actor.ActorSystem;
import simulation.SimulationStarter;
import topology.ActorTopology;

public class SimulationManager implements Runnable{

	private ActorTopology topology;
	
	private LinkedList<Integer> listProduction;
	private LinkedList<Double> listConsumption;
	
	// 01 Juli 2014, 0:00 
	public static LocalDateTime startTime = LocalDateTime.of(2014,7,1,12,0);
	// 01 Juli 2014, 12:00
	public static LocalDateTime endTime = LocalDateTime.of(2014,7,1,20,0);
	public static Duration timeInterval = Duration.ofMinutes(5);
	
	public SimulationManager(LinkedList<Integer> listProduction, 
			LinkedList<Double> listConsumption) {
		this.listConsumption = listConsumption;
		this.listProduction = listProduction;
	}
	
	public void startSimulation() {
		topology = Topology.createTopology(listConsumption, listProduction);		
		SimulationStarter.saveGridTopologyPlot(topology);   
		ActorSystem actorSystem = SimulationStarter.initialiseActorSystem(topology);
        SimulationStarter.startSimulation(actorSystem, startTime, endTime, timeInterval);
	}

	public LinkedList<Integer> getListProduction() {
		return listProduction;
	}

	public LinkedList<Double> getListConsumption() {
		return listConsumption;
	}

	@Override
	public void run() {
		startSimulation();
	}
	
	
}
