package smartgrid;

import smartgrid.server.Server;

public class SmartGridDemo {
		
	Server server;

	public void run(){			
		server = new Server();
		new Thread(server).start();
	}
	
	public static void main(String[] args){
		new SmartGridDemo().run();
	}
}
