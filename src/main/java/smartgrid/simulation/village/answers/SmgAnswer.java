package smartgrid.simulation.village.answers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import akka.basicMessages.AnswerContent;

public class SmgAnswer implements AnswerContent {
	
	private String name;
	
	private double consumption;
	private double production;
	private double batteryCapacity;
	
	public class DobuleCommand {
		public class Command {
			public String time;
			public String value;
		}
		@SerializedName("double")
		public Command command = new Command();
	}
	
	public SmgAnswer(String name) {
		this.name = name;
	}
	
	public Double parseJsonToObject(String json) {
		Gson gson = new Gson();
		DobuleCommand doubleCommand = gson.fromJson(json, DobuleCommand.class);
		String timeString = doubleCommand.command.time;
		String valueString = doubleCommand.command.value;
		Double value = Double.parseDouble(valueString) / 1000;
		return value;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(String json) {
		this.consumption = parseJsonToObject(json);
	}
	
	public double getProduction() {
		return production;
	}

	public void setProduction(String json) {
		this.production = parseJsonToObject(json);
	}

	public double getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(String json) {
		this.batteryCapacity = parseJsonToObject(json);
	}

	public String getName() {
		return name;
	}
}
