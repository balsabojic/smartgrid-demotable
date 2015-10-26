package smartgrid.simulation.arduino.models;

import java.util.ArrayList;

import smartgrid.simulation.arduino.ArduinoConfig;
import smartgrid.simulation.arduino.models.beans.ArduinoClientModel;
import smartgrid.simulation.arduino.models.beans.CommunicationModel;
import smartgrid.simulation.arduino.models.beans.ComponentModel;
import smartgrid.simulation.arduino.models.beans.Subdevice;

/**
 * Device manager responsible for parsing config file and creating list of DeviceContainer
 * @author Balsa
 *
 */
public class DeviceManager {
	
	// ArduinoClientModel object obtained from the arduino configuration file
    private ArduinoClientModel arduinoClientModel;
    
    public DeviceManager() {	
    }
    
    /**
     * The method responsible for storing all data regarding sensors and components 
     * in the list of DeviceContainer and then register them to the ContainerManager
     * @param arduinoConfig arduinoConfig is the configuration class obtained from config file
     */
    public void loadDevices(ArduinoConfig arduinoConfig) {
    	initializeModel(arduinoConfig);
    }
    
    /**
     * Method for reading all relevant information regarding devices and sensors from arduino configuration object
     * obtained from the global configuration file
     * @param arduinoConfig arduinoConfig is the configuration class obtained from config file
     */
    public void initializeModel(ArduinoConfig arduinoConfig) {
    	CommunicationModel communicationModel = new CommunicationModel();
    	communicationModel.setAddress(arduinoConfig.getHost());
    	communicationModel.setPort(arduinoConfig.getPort());
    	communicationModel.setName("ethernet");
    	
    	String arduinoWrappedData = "";
    	ArrayList<ComponentModel> components = new ArrayList<ComponentModel>();
    	int counter = 0;
    	for (Subdevice subdevice: arduinoConfig.getSubdevices()) {
    		ComponentModel component = new ComponentModel();
    		component.setCode(subdevice.getDeviceCode());
    		component.setName(subdevice.getDeviceCode() + "." + (++counter));
    		component.setThreshold(subdevice.getThreshold());
    		component.setPin(subdevice.getPin());
    		components.add(component);
    		
    		// Adding all relevant data regarding sensors to the wrapped data, that will be sent to Arduino
    		// in order to setup all sensors
    		arduinoWrappedData += "{";
    		arduinoWrappedData += "name:" + component.getName() + ",";
    		arduinoWrappedData += "code:" + component.getCode() + ",";
    		arduinoWrappedData += "threshold:" + component.getThreshold() + ",";
    		arduinoWrappedData += "pin:" + component.getPin();
    		arduinoWrappedData += "}";
    	}
    	
    	arduinoClientModel = new ArduinoClientModel();
    	arduinoClientModel.setCommunication(communicationModel);
    	arduinoClientModel.setComponents(components);
    	arduinoClientModel.setArduinoWrappedData(arduinoWrappedData);
    }

    
    public ArduinoClientModel getArduinoClientModel() {
		return arduinoClientModel;
	}

	public void setArduinoClientModel(ArduinoClientModel arduinoClientModel) {
		this.arduinoClientModel = arduinoClientModel;
	}


}
