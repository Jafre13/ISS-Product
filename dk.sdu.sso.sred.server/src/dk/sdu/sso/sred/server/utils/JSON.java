package dk.sdu.sso.sred.server.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A Small fluid JSON Response Generator Class
 * @author Robert Koszewski
 */
public class JSON {

	private Map<String, Object> keys;

	// Constructor
	public JSON() {
		keys = new HashMap<String, Object>();
	};
	
	public JSON(String key, Object value) {
		keys = new HashMap<String, Object>();
		e(key, value);
	};
	
	/**
	 * Add Key and Value
	 * @param key
	 * @param value
	 * @return
	 */
	public JSON e(String key, Object value) {
		 keys.put(escapeString(key), value);
		 return this;
	}
	
	/**
	 * Return JSON formatted String
	 */
	@Override
	public String toString() {
		
		String json_output = "{";
		Iterator<Entry<String, Object>> it = keys.entrySet().iterator();
		boolean first = true;
		
		while(it.hasNext()) {
			Entry<String, Object> en = it.next();
			Object value = en.getValue();
			
			// Prepend comma
			if(!first) {
				json_output += ",";
			}else{
				first = false;
			}
			
			// Add Key (Always String)
			json_output += "\"" + en.getKey() + "\":"; // String Value
			
			// Add Value
			if(value == null) { // NULL Type
				json_output += "null";
				
			}else if(value instanceof String) { // STRING Type
				json_output += "\"" + escapeString((String) en.getValue()) + "\"";
				
			}else if(value instanceof Integer) { // NUMBER Type
				json_output += en.getValue();
				
			}else if(value instanceof Boolean) { // BOOLEAN Type
				json_output += ((Boolean) en.getValue() == true ? "true" : "false");
				
			}else{ // Unsupported Type (Treat as String)
				json_output += "\"" + escapeString(en.getValue().toString()) + "\"";
				
			}
			// Missing JSON Types: Array (An ordered sequence of values) and Object (An unordered collection of key:value pairs)
		}
		
		return json_output + "}";
	}
	
	// Helpers
	private String escapeString(String str) {
		return str.replace("\"", "\\\""); // Change " to \"
	}
	
}
