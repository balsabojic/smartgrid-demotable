package smartgrid.simulation.factory;

import topology.ActorTopology;

public class ProfileFactoryOne implements ProfileFactory {

	public Vpp createVPP(String name, ActorTopology topology) {
		return new VppOne(name, topology);
	}

	@Override
	public Village createVillage(String name, ActorTopology topology) {
		return new VillageOne(name, topology);
	}

}
