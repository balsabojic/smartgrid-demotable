package smartgrid.simulation.factory;

import java.time.LocalDateTime;

import smartgrid.simulation.village.FarmModel;
import smartgrid.simulation.village.HouseModel;
import smartgrid.simulation.village.VillageProfileAggregator;
import topology.ActorTopology;

public class VillageTwo implements Village {
	
	private VillageProfileAggregator villageAggregator;
	private String name;
	private ActorTopology topology;
	
	
	
	public VillageTwo(String name, ActorTopology topology) {
		this.topology = topology;
		
		this.topology = topology;
		this.name = name + "/village";
		villageAggregator = new VillageProfileAggregator(this.name);
		
		// village has 100 houses and each has 6000kw - yearly consumption, returning current consumption in kw
		for (int i = 0; i < 20; i++) {
			villageAggregator.addProfile(new HouseModel(this.name + "/house" + i, 6000));
		}
		for (int i = 0; i < 10; i++) {
			villageAggregator.addProfile(new FarmModel(this.name + "/farm" + i, 50000));
		}
	}
	
	public void init() {
		villageAggregator.init(LocalDateTime.of(2013, 8, 7, 12, 0));
	}
	
	public void startActors() {
		villageAggregator.addActor(topology);
	}

	public ActorTopology getTopology() {
		return topology;
	}
	

}
