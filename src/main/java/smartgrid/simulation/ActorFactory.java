package smartgrid.simulation;


import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;

import smartgrid.simulation.aggregator.AggregatorModel;
import smartgrid.simulation.child.ChildModel;
import resultSaving.NoSave;
import akka.basicActors.ActorOptions;
import akka.basicActors.LoggingMode;

/**
 * 
 * The ActorFactory initializes the Actors with the corresponding BehaviorModels
 * 
 * @author bytschkow
 * 
 */
public abstract class ActorFactory {

	public static ActorOptions createChild(){
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				new ChildModel(),new NoSave());		
		return result;
	}
	
	public static ActorOptions createAggregator(PrintWriter output){
		ActorOptions result = new ActorOptions(LoggingMode.MINIMAL,							
				new HashSet<String>(),new HashSet<String>(),new HashSet<String>(),
				new AggregatorModel(output),new NoSave());		
		return result;		
	}
}
