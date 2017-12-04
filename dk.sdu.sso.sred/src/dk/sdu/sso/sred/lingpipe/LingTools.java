/*******************************************************************************
 * Copyright (c) 2016 Robert Koszewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dk.sdu.sso.sred.lingpipe;

import java.io.File;

import dk.sdu.sso.sred.App;

/**
 * Quick Loading of Models
 */
public class LingTools {

	private static LinguisticClassification lp_instance = null;
	private static String model_filename = "classification.model";
	
	synchronized public static LinguisticClassification getIntance(){
		if(lp_instance == null){
			lp_instance = new LinguisticClassification(new String[] {"work", "personal", "spam", "social"}); // Define the Classes Here
		}
		return lp_instance;
	}
	
	synchronized public static LinguisticClassification getTrainedIntance(String id) throws Exception{
		LinguisticClassification lp = getIntance();
		
		if(lp.isTrained())
			return lp;
		
		// Setup Working Directory
		String working_dir;
        if(System.getProperty("user.home") != null && !System.getProperty("user.home").equals(""))
        	working_dir = System.getProperty("user.home") + File.separatorChar + "." + App.id + File.separatorChar;
        else
        	working_dir = "."+File.separatorChar;
		
        File model = new File(working_dir + model_filename);
        boolean model_loaded = false;
        
        if(model.exists()){
        	try {
				lp.loadModel(model);
				model_loaded = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        if(!model_loaded){
        	// Auto train model??
        	throw new Exception("Model could not be loaded");
        }
        
        return lp;
	}
	
}
