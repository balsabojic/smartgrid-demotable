package smartgrid.simulation.factory;

import java.time.LocalDateTime;

import smartgrid.simulation.vpp.BiogasModel;
import smartgrid.simulation.vpp.SolarModel;
import smartgrid.simulation.vpp.VppProfileAggregator;
import smartgrid.simulation.vpp.WindModel;
import topology.ActorTopology;

public class VppOne implements Vpp {
	
	private VppProfileAggregator vppAggregator;
	private String name;
	private ActorTopology topology;
	
	public VppOne(String name, ActorTopology topology) {
		this.topology = topology;
		this.name = name + "/vpp";
		vppAggregator = new VppProfileAggregator(this.name);
		
		vppAggregator.addProfile(new SolarModel(this.name + "/solar", 10));
		vppAggregator.addProfile(new WindModel(this.name + "/wind", 1000));
		vppAggregator.addProfile(new BiogasModel(this.name + "/biogas", 1000));
		
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
