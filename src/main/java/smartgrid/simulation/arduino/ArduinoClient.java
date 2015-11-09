package smartgrid.simulation.arduino;



import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import smartgrid.simulation.arduino.connectors.CommunicationManager;
import smartgrid.simulation.arduino.connectors.TransferData;
import smartgrid.simulation.arduino.models.DeviceManager;
import smartgrid.simulation.arduino.models.beans.ArduinoClientModel;
import smartgrid.simulation.arduino.models.beans.Subdevice;


public class ArduinoClient {
    
    // ArduinoClientModel object obtained from the arduino configuration file
    private ArduinoClientModel arduinoClientModel;

    private ScheduledExecutorService executor;
    private CommunicationManager communicationManager;
    
    /**
     * Constructor for creating ActuatorClientImpl
     * @param arduinoConfig the arduinoCofing object obtained from the configuration file
     */
	public ArduinoClient(ArduinoConfig arduinoConfig) {
		DeviceManager deviceManager = new DeviceManager();
		deviceManager.loadDevices(arduinoConfig);
		arduinoClientModel = deviceManager.getArduinoClientModel();
		communicationManager = new CommunicationManager(arduinoClientModel);
	}
	
	public void start() {
		 System.out.println("Arduino component is started!");
		 communicationManager.setup();
         executor = Executors.newSingleThreadScheduledExecutor();
 		 executor.scheduleAtFixedRate(communicationManager, 0,
 				1, TimeUnit.SECONDS);
	} 
	
	public double getSensorValue(String name) {
		TransferData data = communicationManager.getTransferData().get(name);
		return Double.parseDouble(data.getValue());
	}
	
	public void setSensorValue(String name, int value) {
		communicationManager.updateSensorValue(name, value);
	}

	/**
	 * This method should destroy all resources at the end and prevent memory leaking
	 * Should destroy all started threads 
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
	
	public static void main(String[] args) {
		ArduinoConfig arduinoConfig = new ArduinoConfig();
		arduinoConfig.setHost("192.168.21.234");
		arduinoConfig.setPort("80");
		arduinoConfig.setPolling_frequency(1);
		ArrayList<Subdevice> subdevices = new ArrayList<Subdevice>();
		
		Subdevice subdevice1 = new Subdevice();
		subdevice1.setDeviceCode(12);
		subdevice1.setPin("A0");
		subdevice1.setThreshold(20);
		subdevices.add(subdevice1);
		
		Subdevice subdevice2 = new Subdevice();
		subdevice2.setDeviceCode(0);
		subdevice2.setPin("8");
		subdevice2.setThreshold(20);
		subdevices.add(subdevice2);
		
		Subdevice subdevice3 = new Subdevice();
		subdevice3.setDeviceCode(1);
		subdevice3.setPin("3");
		subdevice3.setThreshold(20);
		subdevices.add(subdevice3);
		
		arduinoConfig.setSubdevices(subdevices);
		
		ArduinoClient actuator = new ArduinoClient(arduinoConfig);
		actuator.start();
	}
}
