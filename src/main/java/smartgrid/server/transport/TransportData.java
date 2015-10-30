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
	
	public long progress;
	public double production;
	public double consumption;
	public LocalDateTime time;
	
	// VPP and Village production and consumption data
	public HashMap<String, Integer> vpp;
	public HashMap<String, Integer> village;
	
	public TransportData(Simulation simulation, double production, double consumption) {
		this.simulationName = simulation.getSimulationName();
		this.startDate = simulation.getStartDate();
		this.endDate = simulation.getEndDate();
		this.timeInterval = simulation.getTimeInterval();
		this.progress = simulation.getProgress();
		this.consumption = consumption;
		this.production = production;
		this.time = simulation.getCurrentTime();
		this.vpp = new HashMap<String, Integer>();
		this.village = new HashMap<String, Integer>();
		for (Entry<String, Double> entry: simulation.getVppTransportData().entrySet()) {
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
				vpp.put(entry.getKey(), entry.getValue().intValue());
			}	
		}
		for (Entry<String, Double> entry: simulation.getVillageTransportData().entrySet()) {
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
				village.put(entry.getKey(), entry.getValue().intValue());
			}
		}
	}
}
