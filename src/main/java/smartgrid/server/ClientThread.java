package smartgrid.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import smartgrid.simulation.SimulationManager;
import smartgrid.simulation.arduino.ArduinoClient;
import smartgrid.simulation.arduino.ArduinoConfig;
import smartgrid.simulation.arduino.models.beans.Subdevice;

public class ClientThread implements Runnable {

	// Client socket for establishing communication
	private Socket clientSocket;
	// Input stream for getting data from the server side
	private BufferedReader input;
	// Output stream for sending data to the server side
	private PrintWriter output;
	// Flag for checking if the thread should be stopped
	private boolean running;

	// String buffer for storing chars
	StringBuilder message = new StringBuilder();
	
	private SimulationManager simulationManager;
	
	private ArduinoClient arduinoClient;
	private ArduinoClient arduinoClientSmg;
	
	/**
	 * TODO
	 * @param clientSocket the socket that we will use for getting and sending data
	 */
	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.running = true;
	}

	@Override
	public void run() {
		try {
			System.out.println("Thread is started");
			
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			createArduinoClient();
			createArduinoClientSmg();
			
			while (!clientSocket.isClosed() && clientSocket.isConnected()) {
				String message = input.readLine();
//				System.out.println("Read message from the client is: "  + message);
				if (message != null) {
					switch (message) {
					case "simA":
						System.out.println("Choosen sim is A");
						simulationManager = new SimulationManager(output, message, arduinoClient, arduinoClientSmg);
						new Thread(simulationManager).start();
						break;
					case "simB":
						System.out.println("Choosen sim is B");
						simulationManager = new SimulationManager(output, message, arduinoClient, arduinoClientSmg);
						new Thread(simulationManager).start();
						break;
					case "simC":
						System.out.println("Choosen sim is C");
						simulationManager = new SimulationManager(output, message, arduinoClient, arduinoClientSmg);
						new Thread(simulationManager).start();
						break;
					case "stop":
						simulationManager.stopSimulation();
						break;
					default:
						System.out.println("Please choose another");
					}
				}				
			}
			close();
			System.out.println("Client is closed.");
		} catch (IOException ioe) {
			System.out.println("Error! Connection could not be established!");
			close();
		} 
	}

	/**
	 * Method for checking if the thread is running or should be terminated
	 * @return
	 */
	private boolean isRunning() {
		return this.running;
	}

	/**
	 * Method for stopping thread and closing all streams as well as a socket
	 */
	public void close() {
		running = false;
		if (clientSocket != null) {
			try {
				input.close();
				output.close();
				clientSocket.close();
				System.out.println("Ethernet communication successfully closed");
			} catch (IOException e) {
				System.out.println("Failed to close the ethernet connection");
				e.printStackTrace();
			}
		}
	}
	
	public void createArduinoClient() {
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
		
		Subdevice subdevice4 = new Subdevice();
		subdevice4.setDeviceCode(2);
		subdevice4.setPin("9");
		subdevice4.setThreshold(20);
		subdevices.add(subdevice4);
		
		arduinoConfig.setSubdevices(subdevices);
		arduinoClient = new ArduinoClient(arduinoConfig);
		arduinoClient.start();
	}
	
	public void createArduinoClientSmg() {
		ArduinoConfig arduinoConfig = new ArduinoConfig();
		arduinoConfig.setHost("192.168.21.235");
		arduinoConfig.setPort("82");
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
		subdevice3.setDeviceCode(3);
		subdevice3.setPin("2");
		subdevice3.setThreshold(20);
		subdevices.add(subdevice3);
		
		Subdevice subdevice4 = new Subdevice();
		subdevice4.setDeviceCode(4);
		subdevice4.setPin("3");
		subdevice4.setThreshold(20);
		subdevices.add(subdevice4);
		
		arduinoConfig.setSubdevices(subdevices);
		arduinoClientSmg = new ArduinoClient(arduinoConfig);
		arduinoClientSmg.start();
	}
}
