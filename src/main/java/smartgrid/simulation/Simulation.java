package smartgrid.simulation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map.Entry;

import akka.systemActors.GlobalTime;
import smartgrid.simulation.village.answers.SmgAnswer;

public class Simulation {
	
	protected String simulationName;
	protected LocalDateTime startDate;
	protected LocalDateTime endDate;
	protected Duration timeInterval;
	protected LocalDateTime currentTime;
	
	private HashMap<String, Double> vppData;
	private HashMap<String, Double> vppUsedData;
	private HashMap<String, Double> villageData;
	private HashMap<String, SmgAnswer> smgData;
	
	public Simulation(String simulationName, LocalDateTime startDate, LocalDateTime endDate, 
			Duration timeInterval) {
		super();
		this.simulationName = simulationName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeInterval = timeInterval;
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
		long offset = timeInterval.getSeconds() * 1000;
		
		LocalDateTime currentDate = GlobalTime.currentTime;
		long currentTime = currentDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - startTime + offset;
		
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
	
	public HashMap<String, SmgAnswer> getSmgTransportData() {
		HashMap<String, SmgAnswer> data = new HashMap<String, SmgAnswer>();
		for (Entry<String, SmgAnswer> entry: smgData.entrySet()) {
			String name = entry.getKey();
			if (name.contains("/smg")) {
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

	public HashMap<String, Double> getVppUsedData() {
		return vppUsedData;
	}

	public void setVppUsedData(HashMap<String, Double> vppUsedData) {
		this.vppUsedData = vppUsedData;
	}

	public HashMap<String, SmgAnswer> getSmgData() {
		return smgData;
	}

	public void setSmgData(HashMap<String, SmgAnswer> smgData) {
		this.smgData = smgData;
	}

	public void setCurrentTime(LocalDateTime currentTime) {
		this.currentTime = currentTime;
	}
	
}
