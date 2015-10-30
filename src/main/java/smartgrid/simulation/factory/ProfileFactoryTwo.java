package smartgrid.simulation.factory;

import topology.ActorTopology;

public class ProfileFactoryTwo implements ProfileFactory {

	public Vpp createVPP(String name, ActorTopology topology) {
		return new VppTwo(name, topology);
	}

	public Village createVillage(String name, ActorTopology topology) {
		return new VillageTwo(name, topology);
	}

}
