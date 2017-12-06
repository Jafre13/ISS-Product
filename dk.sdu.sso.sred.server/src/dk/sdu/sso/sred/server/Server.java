package dk.sdu.sso.sred.server;

import org.osgi.service.component.annotations.*;
import static spark.Spark.*;

@Component
public class Server {

	// TODO: class provided by template
	
	@Activate
	public void start() {
		System.out.println("SERVER ACTIVATED");
		startServer(80);
	}
	
	
	public void startServer(int port) {
		
		port(12345);
		
		get("/hello", (req, res) -> {return "Hello World";});
		
		System.out.println("Server listening on 80 for /hello");
		
	}

}
