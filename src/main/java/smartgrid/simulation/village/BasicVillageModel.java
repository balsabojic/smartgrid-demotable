package smartgrid.simulation.village;

import java.time.LocalDateTime;

import akka.basicActors.ActorOptions;
import behavior.BehaviorModel;
import topology.ActorTopology;

public abstract class BasicVillageModel extends BehaviorModel { 
	
	public abstract void init(LocalDateTime time, int initPower);
	public abstract ActorOptions createActor();
	public abstract void addActor(ActorTopology topology);
}
