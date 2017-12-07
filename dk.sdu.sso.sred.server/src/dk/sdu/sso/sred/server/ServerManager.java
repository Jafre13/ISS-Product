package dk.sdu.sso.sred.server;

import java.io.IOException;

import org.osgi.service.component.annotations.*;

@Component
public class ServerManager {

	private Server server = null;
	
	//@Activate
	public void start() {
		try {
			server = new Server(8080);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server Started");
	}
	
	//@Deactivate
	public void stop() {
		if(server != null)
			server.stop();
		System.out.println("Server Stopped");
	}

}
