package smartgrid.simulation.arduino.commands;


import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import smartgrid.simulation.arduino.connectors.CommunicationManager;
import smartgrid.simulation.arduino.connectors.TransferData;

/**
 * Responsible for sending commands to the actuator master
 * @author Balsa
 *
 */
public class CommandFactory implements Runnable {
	
	// ScheduleExecutorService for calling threads every interval
	private ScheduledExecutorService executor;
	
	// CommunicationManager is used for sending data and getting data from the devices
	private CommunicationManager communicationManager;
	
	/**
	 * CommandFactory is responsible for sending all data to the actuator master
	 * @param devices list of sensors attached to the Arduino 
	 * @param master ActuatorMaster responsible for receiving data from ActuatorClient
	 * @param clientId
	 * @param communicationManager
	 */
	public CommandFactory(CommunicationManager communicationManager) {
		super();
		this.communicationManager = communicationManager;
	}
	
	/**
	 * Method for setting up the communication manager, that is later setting up all connectors.
	 * It is every second calling method for updating all values from sensors
	 */
	public void setupCommunicationManager() {
		communicationManager.setup();
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(communicationManager, 0,
				1, TimeUnit.SECONDS);
	}

	
	/**
	 * Method for destroying all resources, closing communication manager and turning off executor
	 */
	public void destroy() {
		communicationManager.close();
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		ConcurrentHashMap<String, TransferData> trasferData = communicationManager.transferData;
		for (Entry<String, TransferData> entry : trasferData.entrySet()) {
			String key = entry.getKey();
			TransferData value = entry.getValue();
			if (value.getUpdate() == 1 && key.equals("12.1")) {

				System.out.println("UPDATE: " + key + " : " + value.getValue() + " / " + value.getSignal());
				
				
				String sensorData = value.getValue();
				float sensorDataFloar = Float.parseFloat(sensorData);				
				if (sensorDataFloar > 50.0) {
					communicationManager.updateSensorValue("0.2", 1);
					System.out.println("TURN ON LED  BAR");
					
					communicationManager.updateSensorValue("1.3", 1);
					System.out.println("TURN ON MOTOR");
				}
				else {
					communicationManager.updateSensorValue("0.2", 0);
					System.out.println("TURN OFF LED  BAR");
					
					communicationManager.updateSensorValue("1.3", 0);
					System.out.println("TURN OFF MOTOR");
				}
				
				value.setUpdate(0);
				entry.setValue(value);
				
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}

	}
}
