package smartgrid.simulation.vpp;

import java.time.LocalDateTime;

import akka.basicActors.ActorOptions;
import behavior.BehaviorModel;
import topology.ActorTopology;

public abstract class BasicVppModel extends BehaviorModel { 
	
	public abstract void init(LocalDateTime time);
	public abstract ActorOptions createActor();
	public abstract void addActor(ActorTopology topology);
}
