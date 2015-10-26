package smartgrid.server;

import java.io.PrintWriter;
import java.util.LinkedList;

import com.google.gson.Gson;

import smartgrid.server.transport.TransportData;
import smartgrid.simulation.SimulationManager;

public class Context {
	
	private LinkedList<ClientThread> listThreads;

	private SimulationManager simulationManager;
	private boolean simulationRunning = false;
	
	public Context() {
		listThreads = new LinkedList<ClientThread>();
	}
	
	public void addClientThread(ClientThread thread) {
		listThreads.add(thread);
	}
	
	public synchronized void startSimulation(String simulation) {
		if (simulationRunning == false) {
			simulationManager = new SimulationManager(simulation);
			for (ClientThread client: listThreads) {
				simulationManager.addOutput(client.getOutput());
			}
			new Thread(simulationManager).start();
			simulationRunning = true;
			System.out.println("Number of threads running: " + listThreads.size());
		} else {
			System.out.println("Simulation is already started by another thread");
		}
	}
	
	public synchronized void stopSimulation() {
		if (simulationRunning == true) {
			simulationManager.stopSimulation();
			simulationRunning = false;
		}
		else {
			System.out.println("Simulation cannot be stopped before beeing started");
		}
	}
	
	public void sendData(PrintWriter output) {
		if (simulationRunning == true && simulationManager != null && simulationManager.getSimulation() != null) {
			
		}
	}
}
