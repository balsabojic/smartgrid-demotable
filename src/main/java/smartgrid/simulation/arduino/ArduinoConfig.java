package smartgrid.simulation.arduino;



import java.util.ArrayList;

import smartgrid.simulation.arduino.models.beans.Subdevice;

public class ArduinoConfig {
	
	private String host;
	private String port;
	private int polling_frequency;
	private ArrayList<Subdevice> subdevices = new ArrayList<Subdevice>();
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public int getPolling_frequency() {
		return polling_frequency;
	}
	public void setPolling_frequency(int polling_frequency) {
		this.polling_frequency = polling_frequency;
	}
	public ArrayList<Subdevice> getSubdevices() {
		return subdevices;
	}
	public void setSubdevices(ArrayList<Subdevice> subdevices) {
		this.subdevices = subdevices;
	}
}
