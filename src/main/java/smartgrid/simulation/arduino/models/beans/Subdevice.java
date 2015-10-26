package smartgrid.simulation.arduino.models.beans;



public class Subdevice {
	
	private int deviceCode;
	private float threshold;
	private String pin;
	
	public Subdevice(int deviceCode, float threshold, String pin) {
		super();
		this.deviceCode = deviceCode;
		this.threshold = threshold;
		this.pin = pin;
	}
	
	public Subdevice() {}
	
	public int getDeviceCode() {
		return deviceCode;
	}
	public void setDeviceCode(int deviceCode) {
		this.deviceCode = deviceCode;
	}
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
}
