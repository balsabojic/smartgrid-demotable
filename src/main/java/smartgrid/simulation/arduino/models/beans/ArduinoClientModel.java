package smartgrid.simulation.arduino.models.beans;



import java.util.ArrayList;

/**
 * ArduinoClientModel is a model that represents our Arduino object obtained from the JSON configuration file
 * @author Balsa
 *
 */
public class ArduinoClientModel {

	// Name of the Arduino microcontroller 
    private String name;
    // List of all sensors and components attached to the microcontroller
    private ArrayList<ComponentModel> components;
    // List of the communication types
    private CommunicationModel communication;
    // All information that we need about the sensors wrapped into the file that will be later parsed on Arduino
    // side in order to setup all sensors and thresholds
    private String arduinoWrappedData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ComponentModel> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<ComponentModel> components) {
        this.components = components;
    }

    public CommunicationModel getCommunication() {
        return communication;
    }

    public void setCommunication(CommunicationModel communication) {
        this.communication = communication;
    }

	public String getArduinoWrappedData() {
		return arduinoWrappedData;
	}

	public void setArduinoWrappedData(String arduinoWrappedData) {
		this.arduinoWrappedData = arduinoWrappedData;
	}

}
