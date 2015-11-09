package smartgrid.simulation.arduino.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import smartgrid.simulation.arduino.models.beans.ArduinoClientModel;
import smartgrid.simulation.arduino.models.beans.CommunicationModel;
import smartgrid.simulation.arduino.models.beans.ComponentModel;


/**
 * CommunicationManager as a Component pattern, responsible for loading all communications, as well as setups and runs
 * @author Balsa
 *
 */
public class CommunicationManager implements Communication {
	
	// ArduinoClientModel object obtained from the arduino configuration file
    private ArduinoClientModel arduinoClientModel;
    
    // List of all device's communications
    List<Communication> listCommunication = new ArrayList<Communication>();
    
    // ConcurrentHashMap for all devices and their values that are read from the sensors as well as signals if sensor needs update
 	// This hash map is thread safe
 	private ConcurrentHashMap<String, TransferData> transferData = new ConcurrentHashMap<String, TransferData>();
    
    /**
     * The constructor for creating the CommunicationManager
     * @param arduinoClientModels the list of all arduino client models obtained by config file
     */
	public CommunicationManager(ArduinoClientModel arduinoClientModel) {
		super();
		this.arduinoClientModel = arduinoClientModel;
		loadCommunications();
	}

	/**
	 * Method for creating all types of communications: bluetooth or ethernet
	 * Going through all arduino devices, storing all their sensors inside of the global hashmap,
	 * and also putting connection inside of the CommunicationManager list of communications
	 */
	private void loadCommunications() {
		if (arduinoClientModel != null) {
			System.out.println("==============================================");
			System.out.println(arduinoClientModel.getArduinoWrappedData());
			System.out.println("==============================================");
			CommunicationModel communicationModel = arduinoClientModel.getCommunication();
			ArrayList<String> connectedSensors = new ArrayList<String>();
			for (ComponentModel component : arduinoClientModel.getComponents()) {
				connectedSensors.add(component.getName());
				transferData.put(component.getName(), new TransferData(component.getName(), "0", -1));
			}
			if (communicationModel.getName().equals("ethernet")) {
				EthernetCommunication ethernetCommunication = new EthernetCommunication(communicationModel.getAddress(),
						communicationModel.getPort(), connectedSensors, 
						arduinoClientModel.getArduinoWrappedData(),
						transferData);
				listCommunication.add(ethernetCommunication);
			}
		}
	}
	
	/**
	 * Method for setting up all communications
	 */
	@Override
	public void setup() {
		for (Communication communication: listCommunication) {
			communication.setup();
		}
	}

	/**
	 * Method for running all communications
	 */
	@Override
	public void run() {
		for (Communication communication: listCommunication) {
			communication.run();
		}
	}

	/**
	 * Method for closing all communications
	 */
	@Override
	public void close() {
		for (Communication communication: listCommunication) {
			communication.close();
		}
	}
	
	// Asynchronous call to update sensor
	/**
	 * Method for asynchronous call from the ActuatorMaster to update state of the devices
	 * This method is updating entry in the hash-map for that sensor, and setting status 0 or 1,
	 * so that particular connector can see that sensors that is attached to it should be changed
	 * @param deviceId the name of the sensor that we want to update
	 * @param value the value that we want to set (0 - turn off, 1 - turn on)
	 */
	public void updateSensorValue(String deviceId, int value) {
		TransferData data = transferData.get(deviceId);
		data.setSignal(value);
		data.setValue(Integer.toString(value));
		transferData.put(deviceId, data);
	}

	public ConcurrentHashMap<String, TransferData> getTransferData() {
		return transferData;
	}

	public void setTransferData(ConcurrentHashMap<String, TransferData> transferData) {
		this.transferData = transferData;
	}
	
}
