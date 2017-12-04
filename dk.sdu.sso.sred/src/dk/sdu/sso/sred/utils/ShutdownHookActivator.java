package dk.sdu.sso.sred.utils;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Shutdown Hook for OSGi
 * Based on: https://stackoverflow.com/a/32216407/5284104
 */
@Component()
public class ShutdownHookActivator{

	@Activate
	public void start(/*ComponentContext cc, BundleContext bc, Map<String,Object> config*/) {
		/*
		Thread hook = new Thread() {
			@Override
			public void run() {
			    System.out.println("Stopping OSGi Framework.");
			try {
				// TODO: Commented out until fixed
			    //Framework systemBundle = bc.getBundle(0).adapt(Framework.class);
			    //systemBundle.stop();
			    //System.out.println("Waiting up to 2s for OSGi shutdown to complete...");
			    //systemBundle.waitForStop(2000);
			} catch (Exception e) {
			    System.err.println("Failed to cleanly shutdown OSGi Framework: " + e.getMessage());
			    	e.printStackTrace();
		        }
		    }
		};
		
		System.out.println("Installing shutdown hook.");
	    Runtime.getRuntime().addShutdownHook(hook);
	    */
	}
}
