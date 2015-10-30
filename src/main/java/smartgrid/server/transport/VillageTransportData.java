package smartgrid.server.transport;

import java.util.HashMap;
import java.util.Map.Entry;

import smartgrid.simulation.Simulation;

public class VillageTransportData {
	
	// Village consumption data that will be sent
	public HashMap<String, Integer> village;
	
	public VillageTransportData(Simulation simulation) {
		double house = 0;
		double farm = 0;
		double commercial = 0;
		double streetLight = 0;
		this.village = new HashMap<String, Integer>();
		for (Entry<String, Double> entry: simulation.getVillageTransportData().entrySet()) {
			if (entry != null && entry.getKey() != null && entry.getValue() != null) {
//				village.put(entry.getKey(), entry.getValue().intValue());
				if (entry.getKey().contains("house")) {
					house += entry.getValue();
				}
				else if (entry.getKey().contains("farm")) {
					farm += entry.getValue();
				}
				else if (entry.getKey().contains("commercial")) {
					commercial += entry.getValue();
				}
				else if (entry.getKey().contains("streetLight")) {
					streetLight += entry.getValue();
				}
			}
		}
		village.put("house", (int) house);
		village.put("farm", (int) farm);
		village.put("commercial", (int) commercial);
		village.put("streetLight", (int) streetLight);
	}
}
