package smartgrid.server.transport;

import java.util.HashMap;
import java.util.Map.Entry;

import smartgrid.simulation.Simulation;

public class VppTransportData {

	// Vpp energy that is produced by the Vpp
	public HashMap<String, Integer> vppProduced;
	// Vpp energy that is used by village - optimized using StrategyManagers 
	public HashMap<String, Integer> vppUsed; 
	
	public VppTransportData(Simulation simulation) {
		this.vppProduced = new HashMap<String, Integer>();
		this.vppUsed = new HashMap<String, Integer>();
		for (Entry<String, Double> entry: simulation.getVppTransportData().entrySet()) {
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
				vppProduced.put(entry.getKey(), entry.getValue().intValue());
			}	
		}
		// TODO add used energy
		for (Entry<String, Double> entry: simulation.getVppUsedData().entrySet()) {
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
				vppUsed.put(entry.getKey(), entry.getValue().intValue());
			}	
		}
	}
}
