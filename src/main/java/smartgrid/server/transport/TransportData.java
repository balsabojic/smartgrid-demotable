package smartgrid.server.transport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map.Entry;

import smartgrid.simulation.Simulation;

public class TransportData {
	
	// Simulation information
	public String simulationName;
	public LocalDateTime startDate;
	public LocalDateTime endDate;
	public Duration timeInterval;
	public LocalDateTime currentTime;
	
	public long progress;
	public double production;
	public double consumption;
	public LocalDateTime time;
	
	// VPP and Village data
	public VppTransportData vpp;
	public VillageTransportData village;
	
	public TransportData(Simulation simulation, double production, double consumption) {
		this.simulationName = simulation.getSimulationName();
		this.startDate = simulation.getStartDate();
		this.endDate = simulation.getEndDate();
		this.timeInterval = simulation.getTimeInterval();
		this.progress = simulation.getProgress();
		this.currentTime = simulation.getCurrentTime();
		this.consumption = consumption;
		this.production = production;
		this.time = simulation.getCurrentTime();
		this.vpp = new VppTransportData(simulation);
		this.village = new VillageTransportData(simulation);
	}
}
