package smartgrid.simulation.strategy;

public class StrategyManager {

	private int solarSensor;
	private int bioSensor;
	private int windSensor;
	
	private double solarProduction;
	private double windProduction;
	private double bioProduction;
	
	private double consumption;
	
	public StrategyManager(int solarSensor, double solarProduction, double windProduction, double bioProduction, double consumption) {
		this.solarSensor = solarSensor; 
		this.solarProduction = solarProduction;
		this.windProduction = windProduction;
		this.bioProduction = bioProduction;
		this.consumption = consumption;
		this.windSensor = 100;
		this.bioSensor = 100;
	}
	
	public void optimizeProduction() {
		solarProduction = solarProduction * ((double)solarSensor / 100);
		double production = windProduction + bioProduction + solarProduction;
		
		// TODO we have overproduction - need to reduce the production (we are considering always 95% of production,
		// because we want to be sure that it will always be +5% energy in network 
		// priority is bio < wind < sun, so we are first turning off bio
		if (production > consumption) {
			double difference = production - consumption;
			
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
			windSensor = 100;
			bioSensor = 100;
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
	
}
