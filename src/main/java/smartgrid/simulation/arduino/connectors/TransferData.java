package smartgrid.simulation.arduino.connectors;



/**
 * TrasnferData responsible for storing information about the sensor name, value and signal
 * @author Balsa
 *
 */
public class TransferData {
	
	// Unique id of the sensor
	private String deviceId;
	// Value of the sensor
	private String value;
	// Flag showing if the sensor needs to be turned on (1), turned off (0), or nothing (-1) 
	private int signal;
	
	public TransferData(String deviceId, String value, int signal) {
		this.deviceId = deviceId;
		this.value = value;
		this.signal = signal;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getSignal() {
		return signal;
	}
	public void setSignal(int signal) {
		this.signal = signal;
	}
	
}
