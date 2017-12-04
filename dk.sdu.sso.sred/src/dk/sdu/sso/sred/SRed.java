package dk.sdu.sso.sred;

import org.osgi.service.component.annotations.*;

import dk.sdu.sso.sred.api.SRedAPI;

@Component
/**
 * The SRed Implementation
 */
public class SRed implements SRedAPI {

	// TODO: class provided by template
	
	@Activate
	public void start() {
		System.out.println("Example got activated");
	}

}
