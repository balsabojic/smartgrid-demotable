package smartgrid.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import smartgrid.simulation.SimulationManager;

public class Server implements Runnable {

	private ServerSocket server;
	private Socket clientSocket;
	private ClientThread clientThread;
	
	private Context context;
	
	public Server() {
		try {
			server = new ServerSocket(4321);
			context = new Context();
			System.out.println("Server successfully started");
					
//			System.out.println(in.readLine());
//			//Send data back to client
//			while (true) {
//				System.out.print("Insert data for sending: ");
//				BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
//				String line = buffer.readLine();
//				if ("exit".equals(line)) break;
//				out.println(line);
//			}
		} catch (IOException e) {
			System.out.println("Problem with starting server");
			e.printStackTrace();
		} 
	}

	@Override
	public void run() {
		while (true) {
			try {
				clientSocket = server.accept();
				System.out.println("Client connected");
				clientThread = new ClientThread(clientSocket, context);				
				new Thread(clientThread).start();
				context.addClientThread(clientThread);
			} catch (IOException e) {
				System.out.println("Problem with accepting client");
				e.printStackTrace();
			}
		}	
	}
}
