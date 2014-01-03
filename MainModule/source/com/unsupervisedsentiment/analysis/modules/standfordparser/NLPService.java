package com.unsupervisedsentiment.analysis.modules.standfordparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.unsupervisedsentiment.analysis.model.Quadruple;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class NLPService {
	protected StanfordCoreNLP coreNlp;
	
	public NLPService() {
		Properties props = new Properties();
		props.put("annotators", StanfordCoreNLP.STANFORD_TOKENIZE + ","
				+ StanfordCoreNLP.STANFORD_SSPLIT + ","
				+ StanfordCoreNLP.STANFORD_POS + ","
				+ StanfordCoreNLP.STANFORD_LEMMA + ","
				+ StanfordCoreNLP.STANFORD_PARSE);
		coreNlp = new StanfordCoreNLP(props);
	}

	public List<Quadruple> GetQuadruplesFromSentence(String sentence) {
		List<Quadruple> quadruples = new ArrayList<Quadruple>();
		
		
		return quadruples;
	}
}
