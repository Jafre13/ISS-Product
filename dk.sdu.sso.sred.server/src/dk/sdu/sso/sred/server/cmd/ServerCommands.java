package dk.sdu.sso.sred.server.cmd;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;

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
	
	// Start the REST API server
	@Descriptor("Start the REST API server")
    public String start(@Descriptor("Server Port") int port) {
       return "Server started on port: " + port;
    }

	// Stop the REST API Server
	@Descriptor("Stop the REST API Server")
    public void stop() {
       System.exit(0);
    }
	
}
