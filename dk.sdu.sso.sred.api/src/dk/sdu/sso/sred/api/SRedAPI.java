package dk.sdu.sso.sred.api;

import java.io.File;
import java.io.IOException;

//import org.osgi.annotation.versioning.ConsumerType;
//import org.osgi.annotation.versioning.ProviderType;

//@ConsumerType
//@ProviderType
/**
 * The SRed API
 */
public interface SRedAPI {
	
	// Model Related API Calls
	public void loadModel(File model_file) throws IOException, ClassNotFoundException;
	public boolean isTrained();
	public void saveModel(File model_file) throws Exception;
	
	// Training Methods
	public int train(File path);
	public void train(String input, String category);
	
	// Classification Methods
	public String classify(String text);
}
