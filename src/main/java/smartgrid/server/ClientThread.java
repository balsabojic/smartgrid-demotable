package smartgrid.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import smartgrid.server.transport.TransportData;
import smartgrid.simulation.SimulationManager;

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
	
	private Context context;
	
	/**
	 * TODO
	 * @param clientSocket the socket that we will use for getting and sending data
	 */
	public ClientThread(Socket clientSocket, Context context) {
		this.clientSocket = clientSocket;
		this.running = true;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			System.out.println("Thread is started");
			
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			while (!clientSocket.isClosed() && clientSocket.isConnected()) {
				if (input.ready()) {
					String message = input.readLine();
	//				System.out.println("Read message from the client is: "  + message);
					if (message != null) {
						switch (message) {
						case "simA":
							System.out.println("Choosen sim is A");
							context.startSimulation(message);
							break;
						case "simB":
							System.out.println("Choosen sim is B");
							break;
						case "simC":
							System.out.println("Choosen sim is C");
							break;
						case "stop":
							System.out.println("Simulation is stopped");
							context.stopSimulation();
							break;
						default:
							System.out.println("Please choose another");
						}
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
	
	public PrintWriter getOutput() {
		return output;
	}
}
