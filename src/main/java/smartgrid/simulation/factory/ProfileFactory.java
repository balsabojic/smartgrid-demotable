package smartgrid.simulation.factory;

import topology.ActorTopology;

public interface ProfileFactory {
	public Vpp createVPP(String name, ActorTopology topology);
	public Village createVillage(String name, ActorTopology topology);
}
