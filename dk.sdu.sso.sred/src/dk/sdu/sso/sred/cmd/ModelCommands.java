package dk.sdu.sso.sred.cmd;

import java.io.File;

import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;

import dk.sdu.sso.sred.lingpipe.LinguisticClassification;

// http://enroute.osgi.org/appnotes/gogo.html
// http://enroute.osgi.org/appnotes/gogo-cmd.html

@Component(
  service = ModelCommands.class,
  property = {
	// Scope
    Command.SCOPE + "model", 
    // Functions
    Command.FUNCTION + "train",
    Command.FUNCTION + "trainfromfolder",
    Command.FUNCTION + "classify",
    Command.FUNCTION + "resetmodel",
})
/**
 * OSGi Based Command Line Commands
 * @author Robert Koszewski
 */
public class ModelCommands {
	
	private static LinguisticClassification classifier = null;
	
	// Train: Train the Model
	@Descriptor("Train the Model")
    public String resetmodel(@Descriptor("Categories") String[] categories) {
		// Initialize new Model
		classifier = new LinguisticClassification(categories);
		return "Created new Model with the categories: " + categories.toString();
    }
	
	
	// Train: Train the Model
	@Descriptor("Train the Model")
    public void train(
    		@Descriptor("Input Text") String input_text,
    		@Descriptor("Category") String category
    		) {
		LinguisticClassification classifier = getModel();
		classifier.train(input_text, category);
    }
	
	// Train: Train the Model
	@Descriptor("Train the Model")
    public void trainfromfolder(@Descriptor("Folder Path") String path) {
		LinguisticClassification classifier = getModel();
		classifier.train(new File(path));
    }
	
	// Classify: Classify Input Text
	@Descriptor("Classify Input Text")
    public String classify(@Descriptor("Input Text") String input_text) {
		LinguisticClassification classifier = getModel();
		return classifier.classify(input_text);
    }
	
	
	// Helper Methods
	/**
	 * Returns Model and initializes it if necessary
	 * @return
	 */
	private LinguisticClassification getModel() {
		if(classifier == null) {
			classifier = new LinguisticClassification(new String[] {"work", "personal", "spam", "social"});
		}
		return classifier;
	}
}
