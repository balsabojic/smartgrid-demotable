package smartgrid.simulation.strategy;

import java.util.HashMap;
import java.util.Map.Entry;

import smartgrid.simulation.village.answers.SmgAnswer;

public class StrategyManager {

	private int solarSensor;
	private int bioSensor;
	private int windSensor;
	
	private double solarProduction;
	private double windProduction;
	private double bioProduction;
	
	private double consumption;
	
	private int evSensor;
	private int streetSensor;
	
	private HashMap<String, SmgAnswer> smgData;
	
	public StrategyManager(int solarSensor, double solarProduction, 
			double windProduction, double bioProduction, 
			double consumption, HashMap<String, SmgAnswer> smgData) {
		this.solarSensor = solarSensor; 
		this.solarProduction = solarProduction;
		this.windProduction = windProduction;
		this.bioProduction = bioProduction;
		this.consumption = consumption;
		this.smgData = smgData;
		this.windSensor = 100;
		this.bioSensor = 100;
		
		// Sensor for electric vehicles, if we have overproduction we are charging vehicles, if not we need to turn off
		// default value is 1,  and if we have underproduction we need to turn it off 
		this.evSensor = 1;
		// Sensor for street lights that can be controlled
		// if 1 then we have enough energy, if 0 then we need to turn off them - reduce consumption also!
		this.streetSensor = 1;
	}
	
	public void optimizeProduction() {
		solarProduction = solarProduction * ((double)solarSensor / 100);
		
		// Ask SMG node to connect again in the network
		double smgProduction = 0;
		if (smgData != null) {
			for (Entry<String, SmgAnswer> smg : smgData.entrySet()) {
				smgProduction += smg.getValue().getProduction();
			}
		}
		
		double production = windProduction + bioProduction + solarProduction + smgProduction;
		
		// TODO we have overproduction - need to reduce the production (we are considering always 95% of production,
		// because we want to be sure that it will always be +5% energy in network 
		// priority is bio < wind < sun, so we are first turning off bio
		if (production > consumption) {
			double difference = production - consumption;
			
			// We have overproduction we can call smg to connect to grid normally
			if (smgData != null) {
				for (Entry<String, SmgAnswer> smg : smgData.entrySet()) {
					smg.getValue().sendData("/api/openhab/dummy.wrapper.dummy_switchgridconnected/1");
				}
			}
			
			// if overproduction is lower then bioProduction then reduce the production of bioProduction
			if (difference < bioProduction) {
				double newBioProduction = bioProduction - difference;
				// calculating new percent of production for BioPlant sensor
				bioSensor = (int)((newBioProduction * 100) / bioProduction);
				bioProduction = newBioProduction;
			}
			// turn off complete bio and reduce wind
			else if (difference < (bioProduction + windProduction)){
				// turn off completely bioProduction
				difference = difference - bioProduction;
				bioProduction = 0;
				bioSensor = 0;
				// reduce wind production
				double newWindProduction = windProduction - difference;
				windSensor = (int) ((newWindProduction * 100) / windProduction);
				windProduction = newWindProduction;
			}
			// turn off bio and wind and take a bit of sun
			else {
				//turn off completely bioProduction
				difference = difference - bioProduction;
				bioProduction = 0;
				bioSensor = 0;
				// turn off completely windProduction
				difference = difference - windProduction;
				windProduction = 0;
				windSensor = 0;
				// reduce solar production
				solarProduction = solarProduction - difference;
			}
		}
		else {
			// Set maximum production
			windSensor = 100;
			bioSensor = 100;
			
			// When we have underproduction turn off the EV first
			evSensor = 0;
			
			double difference = consumption - production;
			
			// Ask SMG node to disconnect from a network
			if (smgData != null) {
				for (Entry<String, SmgAnswer> smg: smgData.entrySet()) {
					double smgConsumption = smg.getValue().getConsumption();
					if (difference > smgConsumption) {
						smg.getValue().sendData("/api/openhab/dummy.wrapper.dummy_switchgridconnected/0");
						difference -= smgConsumption;
						smg.getValue().setConsumption(0.0);
						smg.getValue().setProduction(0.0);
						consumption -= smgConsumption;
					}
					else if (difference < smgConsumption && difference > 0){
						smg.getValue().sendData("/api/openhab/dummy.wrapper.dummy_switchgridconnected/0");
						difference = 0;
						smg.getValue().setConsumption(0.0);
						smg.getValue().setProduction(0.0);
						consumption -= smgConsumption;
					}
				}
			}
			
			if (difference > 0) {
				streetSensor = 0;
			}
		}
	}

	public int getBioSensor() {
		return bioSensor;
	}

	public void setBioSensor(int bioSensor) {
		this.bioSensor = bioSensor;
	}

	public int getWindSensor() {
		return windSensor;
	}

	public void setWindSensor(int windSensor) {
		this.windSensor = windSensor;
	}

	public double getSolarProduction() {
		return solarProduction;
	}

	public void setSolarProduction(double solarProduction) {
		this.solarProduction = solarProduction;
	}

	public double getWindProduction() {
		return windProduction;
	}

	public void setWindProduction(double windProduction) {
		this.windProduction = windProduction;
	}

	public double getBioProduction() {
		return bioProduction;
	}

	public void setBioProduction(double bioProduction) {
		this.bioProduction = bioProduction;
	}

	public int getSolarSensor() {
		return solarSensor;
	}

	public void setSolarSensor(int solarSensor) {
		this.solarSensor = solarSensor;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public int getEvSensor() {
		return evSensor;
	}

	public void setEvSensor(int evSensor) {
		this.evSensor = evSensor;
	}

	public int getStreetSensor() {
		return streetSensor;
	}

	public void setStreetSensor(int streetSensor) {
		this.streetSensor = streetSensor;
	}

	public HashMap<String, SmgAnswer> getSmgData() {
		return smgData;
	}

	public void setSmgData(HashMap<String, SmgAnswer> smgData) {
		this.smgData = smgData;
	}
	
}
