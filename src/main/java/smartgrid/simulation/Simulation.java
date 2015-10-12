package smartgrid.simulation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map.Entry;

import akka.systemActors.GlobalTime;
import smartgrid.simulation.factory.Village;
import smartgrid.simulation.factory.Vpp;

public class Simulation {
	
	protected String simulationName;
	protected LocalDateTime startDate;
	protected LocalDateTime endDate;
	protected Duration timeInterval;
	
	private Vpp vpp;
	private Village village;
	
	private HashMap<String, Double> vppData;
	private HashMap<String, Double> villageData;
	
	public Simulation(String simulationName, LocalDateTime startDate, LocalDateTime endDate, Duration timeInterval) {
		super();
		this.simulationName = simulationName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeInterval = timeInterval;
	}

	public void setVpp(Vpp vpp)  {
		this.vpp = vpp;
	}
	
	public void setVillage(Village village) {
		this.village = village;
	}

	public String getSimulationName() {
		return simulationName;
	}

	public void setSimulationName(String simulationName) {
		this.simulationName = simulationName;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Duration getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Duration timeInterval) {
		this.timeInterval = timeInterval;
	}
	
	public HashMap<String, Double> getVppData() {
		return vppData;
	}

	public void setVppData(HashMap<String, Double> vppData) {
		this.vppData = vppData;
	}

	public HashMap<String, Double> getVillageData() {
		return villageData;
	}

	public void setVillageData(HashMap<String, Double> villageData) {
		this.villageData = villageData;
	}

	public long getProgress() {
		long startTime = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long endTime = endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		
		long duration = endTime - startTime;
		
		LocalDateTime currentDate = GlobalTime.currentTime;
		long currentTime = currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - startTime;
		
		long progress = (currentTime * 100) / duration;

		return progress;
	}
	
	public LocalDateTime getCurrentTime() {
		return GlobalTime.currentTime;
	}
	
	public HashMap<String, Double> getVppTransportData() {
		HashMap<String, Double> data = new HashMap<String, Double>();
		for (Entry<String, Double> entry: vppData.entrySet()) {
			String name = entry.getKey();
			if (name.contains("/")) {
				String[] parts = name.split("/");
				name = parts[parts.length - 1];
				data.put(name, entry.getValue());
			}
			else {
				data.put(entry.getKey(), entry.getValue());
			}
		}
		return data;
	}
	
	public HashMap<String, Double> getVillageTransportData() {
		HashMap<String, Double> data = new HashMap<String, Double>();
		for (Entry<String, Double> entry: villageData.entrySet()) {
			String name = entry.getKey();
			if (name.contains("/")) {
				String[] parts = name.split("/");
				name = parts[parts.length - 1];
				data.put(name, entry.getValue());
			}
			else {
				data.put(entry.getKey(), entry.getValue());
			}
		}
		return data;
	}
}
