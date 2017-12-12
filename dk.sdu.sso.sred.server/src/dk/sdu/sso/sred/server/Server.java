package dk.sdu.sso.sred.server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.sdu.sso.sred.api.SRedAPI;
import dk.sdu.sso.sred.lingpipe.LinguisticClassification;
import dk.sdu.sso.sred.server.utils.JSON;
import fi.iki.elonen.NanoHTTPD;

/**
 * Rest Server
 * @author Robert Koszewski
 *
 */
@SuppressWarnings("restriction") // TODO: Implement this to retrieve it as a OSGi Service
public class Server extends NanoHTTPD {
	
	private SRedAPI net;
	private double training_samples = 0;
	private String[] current_categories;
	private final String MODEL_FILE_NAME = "rest_model.model";

	/**
	 * Start Server
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		super(port);
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		current_categories = new String[] {"work", "personal", "spam", "social"};
		net = new LinguisticClassification(current_categories);
        System.out.println("\nRunning! Point your browsers to http://localhost:"+port+"/ \n");
	}

	@Override
	/**
	 * Serve Response
	 */
	public Response serve(IHTTPSession session) {

		// Parse Parameters
		Map<String, List<String>> parameters = getPOSTData(session);

		// Show Debug Information in Console
		System.out.println(
				"[" + getCurrentTimeStamp() +"] "+ // Time-stamp
				session.getMethod().name() +": " + // Verb
				session.getUri() + // URI
				(parameters.size() != 0 ? " (" + parameters.toString() + ")" : "") + // Parameters
				" FROM IP: " + getRemoteIP(session)); // Remote IP

		// Response
		Response response = null;
		
		// Remove Trailing Slash
		String url = session.getUri();
		while(url.endsWith("/"))
			url = url.substring(0, url.length() - 1);
		
		// Process URLS
		switch(url.toLowerCase()) {
			case "/api/authenticate": 
				response = newFixedLengthResponse(authenticate(session, parameters));
				break;
			case "/api/train": 
				response = newFixedLengthResponse(train(session, parameters));
				break;
			case "/api/query": 
				response = newFixedLengthResponse(query(session, parameters));
				break;
				
			// Debug Methods -> Only accessible during development & debug
				
			case "/debug/resetmodel": 
				response = newFixedLengthResponse(debug_resetmodel(session, parameters));
				break;
			case "/debug/savemodel": 
				response = newFixedLengthResponse(debug_savemodel(session, parameters));
				break;
			case "/debug/systeminfo": 
				response = newFixedLengthResponse(debug_systeminfo(session, parameters));
				break;
		}
		
		// 404 Not Found
		if(response == null)
			response = newFixedLengthResponse(new JSON("error", "invalidapirequesturl") + "");

		// Add JSON Header
		response.addHeader("content-type", "application/json");
		
		return response;
	}
	
	// Helper Methods
	
	/**
	 * Get POST Data
	 * @param session
	 * @return
	 */
	private Map<String, List<String>> getPOSTData(IHTTPSession session){
		Map<String,String> postData = new HashMap<String,String>(); // Not sure why this has to be called before getParameters. Without it getParameters doesn't work. But hey.
		try {
			session.parseBody(postData);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ResponseException e) {
			e.printStackTrace();
		}
		return session.getParameters();
	}
	
	/**
	 * Convert Bytes to Human Readable Format
	 * Source: https://stackoverflow.com/a/3758880/5284104
	 * @param bytes
	 * @param si
	 * @return
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	/**
	 * Get Remote IP
	 * @param session
	 * @return
	 */
	private String getRemoteIP(IHTTPSession session) {
		Map<String, String> headers = session.getHeaders();
		
		// System.out.println("HEADER SIZE: " + headers.toString());

		// Return Proxied Header
		if(headers.containsKey("x-real-ip"))
			return headers.get("x-real-ip");
		
		// Return Connection IP
		return session.getRemoteIpAddress();
	}
	
	/**
	 * Get Current Time Stamp
	 * @return
	 */
	public String getCurrentTimeStamp() {
	    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	// REST API

	/**
	 * User Authentication (Disabled during prototype)
	 * URL: /api/authenticate
	 * @param session
	 * @param parameters 
	 * @return
	 */
	private String authenticate(IHTTPSession session, Map<String, List<String>> parameters) {
		return new JSON("error", "notimplemented") + "";
	}
	
	/**
	 * Train Model
	 * URL: /api/train
	 * @param session
	 * @return
	 */
	private String train(IHTTPSession session, Map<String, List<String>> postData) {
		
		// Check Parameters
		if(!postData.containsKey("TextMessage") || !postData.containsKey("DesiredCategory")) {
			return new JSON("error", "missingarguments").e("detail", "Missing TextMessage and-or DesiredCategory") + "";
		}
		
		try {
			net.train(postData.get("TextMessage").get(0), postData.get("DesiredCategory").get(0));
			training_samples++;
		}catch(Exception e) {
			return new JSON("error", "exception").e("detail", e.getMessage()) + "";
		}
		
		return new JSON("success", true) + "";
	}
	
	/**
	 * Query Message Categorization
	 * URL: /api/train
	 * @param session
	 * @return
	 */
	private String query(IHTTPSession session, Map<String, List<String>> postData) {

		// Check Parameters
		if(!postData.containsKey("TextMessage")) {
			return new JSON("error", "missingarguments").e("detail", "Missing TextMessage") + "";	
		}

		try {
			String category = net.classify(postData.get("TextMessage").get(0));
			return new JSON("category", category).e("priority", 1).e("relevance_time", "Mo-Su@0:00-23:59") + "";
		}catch(Exception e) {
			return new JSON("error", "exception").e("detail", e.getMessage() + " - You probably didn't train the model and its empty.") + "";
		}
	}
	
	
	// Debug Methods
	
	/**
	 * URL:
	 * @param session
	 * @return
	 */
	private String debug_resetmodel(IHTTPSession session, Map<String, List<String>> postData) {
		
		// Check Parameters
		if(!postData.containsKey("Categories")) {
			
			// Reset to Defaults
			if(postData.containsKey("Defaults")) {
				current_categories = new String[] {"work", "personal", "spam", "social"};
				net = new LinguisticClassification(current_categories);
				training_samples = 0;
				return new JSON("success", true).e("message", "Model got reset to its defaults: work, personal, spam and social.") + "";
				
			}
			
			return new JSON("error", "missingarguments").e("detail", "Missing Categories or Defaults") + "";	
		}
		
		List<String> categories_list = postData.get("Categories");
		
		try {
			String[] categories = categories_list.toArray(new String[0]);	
			net = new LinguisticClassification(categories);
			training_samples = 0;
			current_categories = categories;
			return new JSON("success", true).e("message", "Model got reset with the categories: " + Arrays.toString(categories)) + "";
		}catch(Exception e) {
			return new JSON("error", "exception").e("detail", e.getMessage()) + "";
		}
	}
	
	/**
	 * Save Trained Model
	 * @param session
	 * @return
	 */
	private String debug_savemodel(IHTTPSession session, Map<String, List<String>> parameters) {
		// Get POST data
		Map<String, List<String>> postData = getPOSTData(session);
		
		// Check Parameters
		if(!postData.containsKey("DebugAuth")) {
			return new JSON("error", "missingarguments") + "";	
		}

		// Save Model
		try {
			File model = new File(MODEL_FILE_NAME);
			net.saveModel(model);
			return new JSON("success", true).e("message", "Model got saved. Model file size: "+humanReadableByteCount(model.length(), false)) + "";
		} catch (Exception e) {
			return new JSON("error", "exception").e("detail", e.getMessage()) + " - You probably didn't train the model and its empty.";
		}
	}
	
	/**
	 * URL:
	 * @param session
	 * @return
	 */
	/*
	private String debug_pretraindata(IHTTPSession session, Map<String, List<String>> postData) {
		
		// Check Parameters
		if(!postData.containsKey("DebugAuth")) {
			return new JSON("error", "missingarguments") + "";	
		}

		List<String> categories_list = postData.get("Categories");
		
		try {
			// Pre-train
			training_samples =+ net.train(new File(""));

			return new JSON("success", true).e("message", "Model got reset with the categories: " + Arrays.toString(categories)) + "";
		}catch(Exception e) {
			return new JSON("error", "exception").e("detail", e.getMessage()) + "";
		}
	}
	*/
	
	/**
	 * Save Trained Model
	 * @param session
	 * @return
	 */
	private String debug_systeminfo(IHTTPSession session, Map<String, List<String>> parameters) {
		File model = new File(MODEL_FILE_NAME);
		return new JSON("TotalMemory", humanReadableByteCount(Runtime.getRuntime().totalMemory(), false))
					.e("FreeMemory", humanReadableByteCount(Runtime.getRuntime().freeMemory(), false)) 
					.e("MaxMemory", humanReadableByteCount(Runtime.getRuntime().maxMemory(), false)) 
					.e("ModelTrainingSamples", String.format("%.0f", training_samples)) 
					.e("ModelCategories", Arrays.toString(current_categories)) 
					.e("RequestParameters", parameters.toString()) 
					.e("UsedMemory", humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), false))
					.e("ModelFileSize", ( model.isFile() ? humanReadableByteCount(model.length(), false) : "NoModelFile")) + "";		
	}
}
