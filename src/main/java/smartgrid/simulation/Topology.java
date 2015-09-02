package smartgrid.simulation;


import java.util.LinkedList;

import smartgrid.server.Server;
import topology.ActorTopology;

/**
 * 
 * This is the topology for the simulation
 * 
 * @author bytschkow
 *
 */

public class Topology {
	
	private static String simulationName = "Simulation";
	
	public static ActorTopology createTopology(LinkedList<Double> listConsumption,
			 LinkedList<Integer> listProduction){
		
		ActorTopology top = new ActorTopology(simulationName);
				
		/*
		 *  Actor Topology
		 */
		top.addActor("master", ActorFactory.createAggregator(listProduction, listConsumption));
		String childName;
		
		for (int i = 1; i <= 10; i++) {
			childName = "master/vilage" + i;
			top.addActorAsChild(childName, ActorFactory.createChild());
		}
				
		return top;
	}
}
