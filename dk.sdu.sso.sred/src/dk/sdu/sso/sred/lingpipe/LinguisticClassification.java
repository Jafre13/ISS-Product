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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.LMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.stats.MultivariateEstimator;
import com.aliasi.util.Files;

import dk.sdu.sso.sred.api.SRedAPI;

/**
 * Linguistic Polarity Processor
 * @author Robert Koszewski
 */
public class LinguisticClassification implements SRedAPI{
	
    private String[] mCategories;
    private LMClassifier<NGramProcessLM, MultivariateEstimator> mClassifier;
    private boolean isTrained = false;
	
	public LinguisticClassification(String[] mCategories){
		this.mCategories = mCategories;
	}
	
	/**
	 * Load Model From File
	 * @param model_file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void loadModel(File model_file) throws IOException, ClassNotFoundException{
		// Load Model From File
		System.out.println("Loading Training Model. Please wait.");
		FileInputStream fileIn = new FileInputStream(model_file);
		ObjectInputStream objIn = new ObjectInputStream(fileIn); 
		mClassifier = (LMClassifier<NGramProcessLM, MultivariateEstimator>) objIn.readObject();
		objIn.close();
		isTrained = true;
		System.out.println("Loading Model. Complete.");
	}
	
	/**
	 * Is Trained?
	 * @return
	 */
	public boolean isTrained(){
		return this.isTrained;
	}
	
	/**
	 * Save Generated Model to File
	 * @param model_file
	 * @throws Exception 
	 */
	public void saveModel(File model_file) throws Exception{
		// Write model to file
		//System.out.println(mClassifier.getClass());
		System.out.println("Saving Trained Model. Please wait.");
		if(mClassifier instanceof DynamicLMClassifier){
			FileOutputStream fileOut = new FileOutputStream(model_file);
	        ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
	        ((DynamicLMClassifier<NGramProcessLM>) mClassifier).compileTo(objOut);
	        objOut.close();
	        System.out.println("Saving Trained Model. Complete.");
		}else{
			throw new Exception("Cannot compile a non dynamic traning set");
		}
	}
	
	/**
	 * Train Model
	 * @param path
	 * @return 
	 */
	public int train(File path){
		int nGram = 8;
		DynamicLMClassifier<NGramProcessLM> tmClassifier;
		
		if(mClassifier instanceof DynamicLMClassifier)
			tmClassifier = (DynamicLMClassifier<NGramProcessLM>) mClassifier;
		else
			tmClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);

		int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < mCategories.length; ++i) {
        	System.out.println("Training category: "+mCategories[i]);
            String category = mCategories[i];
            Classification classification = new Classification(category);
            File file = new File(path, mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                ++numTrainingCases;
                String review;
				try {
					System.out.println("Reading: " + trainFile.getName());
					review = Files.readFromFile(trainFile,"ISO-8859-1");
					numTrainingChars += review.length();
                    Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                    tmClassifier.handle(classified);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        
        mClassifier = tmClassifier;
        isTrained = true;
        
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
        
        return numTrainingCases;
	}
	
	/**
	 * Train with single input
	 * @param input
	 * @param category
	 */
	public void train(String input, String category) {
		int nGram = 8;
		if(mClassifier instanceof DynamicLMClassifier)
			mClassifier = (DynamicLMClassifier<NGramProcessLM>) mClassifier;
		else
			mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		
		Classification classification = new Classification(category);
		Classified<CharSequence> classified = new Classified<CharSequence>(input, classification);
		((DynamicLMClassifier<NGramProcessLM>)mClassifier).handle(classified);
	}
	
	/**
	 * Classify text to category
	 * @param text
	 * @return
	 */
	public String classify(String text){
		return mClassifier.classify(text).bestCategory();
	}
}
