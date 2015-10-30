package smartgrid.simulation.factory;

import java.time.LocalDateTime;

import smartgrid.simulation.vpp.BiogasModel;
import smartgrid.simulation.vpp.SolarModel;
import smartgrid.simulation.vpp.VppProfileAggregator;
import smartgrid.simulation.vpp.WindModel;
import topology.ActorTopology;

public class VppTwo implements Vpp {
	
	private VppProfileAggregator vppAggregator;
	private String name;
	private ActorTopology topology;
	
	public VppTwo(String name, ActorTopology topology) {
		this.topology = topology;
		this.name = name + "/vpp";
		vppAggregator = new VppProfileAggregator(this.name);
		
		// 200 kw - initial power of the sloar
		vppAggregator.addProfile(new SolarModel(this.name + "/solar", 100));
		// 2 mw - initial power of the wind
		vppAggregator.addProfile(new WindModel(this.name + "/wind", 4000));
		// 100 kw - initial power of the biogas
		vppAggregator.addProfile(new BiogasModel(this.name + "/biogas", 100));
		
	}
	
	public void init() {
		vppAggregator.init(LocalDateTime.of(2013, 8, 7, 12, 0));
	}
	
	public void startActors() {
		vppAggregator.addActor(topology);
	}

	public ActorTopology getTopology() {
		return topology;
	}
	
}
