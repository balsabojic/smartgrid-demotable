package smartgrid.simulation.factory;

import java.time.LocalDateTime;

import smartgrid.simulation.village.HouseModel;
import smartgrid.simulation.village.VillageProfileAggregator;
import topology.ActorTopology;

public class VillageOne implements Village {
	
	private VillageProfileAggregator villageAggregator;
	private String name;
	private ActorTopology topology;
	
	
	
	public VillageOne(String name, ActorTopology topology) {
		this.topology = topology;
		
		this.topology = topology;
		this.name = name + "/village";
		villageAggregator = new VillageProfileAggregator(this.name);
		
		villageAggregator.addProfile(new HouseModel(this.name + "/house"));
	}
	
	public void init() {
		villageAggregator.init(LocalDateTime.of(2013, 8, 7, 12, 0), 10);
	}
	
	public void startActors() {
		villageAggregator.addActor(topology);
	}

	public ActorTopology getTopology() {
		return topology;
	}
	

}
