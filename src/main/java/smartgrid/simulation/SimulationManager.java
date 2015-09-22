package smartgrid.simulation;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import akka.actor.ActorSystem;
import simulation.SimulationStarter;
import topology.ActorTopology;

public class SimulationManager extends Thread{

	private ActorTopology topology;
	
	private PrintWriter output;
	
	// 01 Juli 2014, 0:00 
	public static LocalDateTime startTime = LocalDateTime.of(2014,7,1,12,0);
	// 01 Juli 2014, 12:00
	public static LocalDateTime endTime = LocalDateTime.of(2014,7,1,20,0);
	public static Duration timeInterval = Duration.ofMinutes(5);
	
	public SimulationManager(PrintWriter output) {
		this.output = output;
	}
	
	public void startSimulation() {
		topology = Topology.createTopology(output);		
		SimulationStarter.saveGridTopologyPlot(topology);   
		ActorSystem actorSystem = SimulationStarter.initialiseActorSystem(topology);
        SimulationStarter.startSimulation(actorSystem, startTime, endTime, timeInterval);
	}
	
	public void stopSimulation() {
		SimulationStarter.stopSimulation();
	}

	@Override
	public void run() {
		startSimulation();
	}
	
	
}
