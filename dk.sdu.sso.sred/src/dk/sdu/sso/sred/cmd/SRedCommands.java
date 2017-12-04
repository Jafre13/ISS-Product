package dk.sdu.sso.sred.cmd;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.service.component.annotations.Component;

// http://enroute.osgi.org/appnotes/gogo.html
// http://enroute.osgi.org/appnotes/gogo-cmd.html

@Component(
  service = SRedCommands.class,
  property = {
	// Scope
    Command.SCOPE + "sred", 
    // Functions
    Command.FUNCTION + "test"
})
/**
 * OSGi Based Command Line Commands
 * @author Robert Koszewski
 */
public class SRedCommands {
	
	@Descriptor("Description of the command")
    public String test(
    		@Descriptor("Just an optional parameter") @Parameter(names = {"-e", "--enabled"}, absentValue = "false", presentValue = "true") boolean enabled
    		) {
       return "IT WORKS! " + enabled;
    }
	
}
