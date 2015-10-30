package smartgrid.simulation.factory;

import topology.ActorTopology;

public class ProfileFactoryThree implements ProfileFactory {

	public Vpp createVPP(String name, ActorTopology topology) {
		return new VppThree(name, topology);
	}

	public Village createVillage(String name, ActorTopology topology) {
		return new VillageThree(name, topology);
	}

}
