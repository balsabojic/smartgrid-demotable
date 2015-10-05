package smartgrid.simulation.factory;

import java.time.LocalDateTime;

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
		
		vppAggregator.addProfile(new SolarModel(this.name + "/solar"));
		vppAggregator.addProfile(new WindModel(this.name + "/wind"));
		
	}
	
	public void init() {
		vppAggregator.init(LocalDateTime.of(2013, 8, 7, 12, 0), 10);
	}
	
	public void startActors() {
		vppAggregator.addActor(topology);
	}

	public ActorTopology getTopology() {
		return topology;
	}
	
}
