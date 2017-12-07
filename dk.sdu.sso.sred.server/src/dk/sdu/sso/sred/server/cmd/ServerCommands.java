package dk.sdu.sso.sred.server.cmd;

import java.io.IOException;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;

import dk.sdu.sso.sred.server.Server;

// http://enroute.osgi.org/appnotes/gogo.html
// http://enroute.osgi.org/appnotes/gogo-cmd.html

@Component(
  service = ServerCommands.class,
  property = {
	// Scope
    Command.SCOPE + "server", 
    // Functions
    Command.FUNCTION + "start",
    Command.FUNCTION + "stop"
})
/**
 * OSGi Based Command Line Commands
 * @author Robert Koszewski
 */
public class ServerCommands {
	
	private static Server server = null;
	
	// Start the REST API server
	@Descriptor("Start the REST API server")
    public String start(@Descriptor("Server Port") int port) {
		
		if(server != null)
			return "Server is already running. Please stop it first";
		
		try {
			server = new Server(port);
			return "Server started on port: " + port;
			
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR: Could not start server. Check the stack trace to find out what happened.";
			
		}
    }

	// Stop the REST API Server
	@Descriptor("Stop the REST API Server")
    public String stop() {
		if(server == null)
			return "ERROR: Server is already stopped";
		
		server.closeAllConnections();
		server.stop();
		server = null;
		return "Server Stopped";
    }
	
}
