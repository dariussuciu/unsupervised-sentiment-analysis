package com.unsupervisedsentiment.analysis.modules.doublepropagation;

import java.util.HashSet;

import com.unsupervisedsentiment.analysis.model.DoublePropagationData;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.targetextraction.IOpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.ITargetExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.OpinionWordExtractorService;
import com.unsupervisedsentiment.analysis.modules.targetextraction.TargetExtractorService;

public class DoublePropagationAlgorithm {
	
	private IOpinionWordExtractorService opinionWordExtractorService;
	private ITargetExtractorService targetExtractorService;
	private DoublePropagationData data;
	private HashSet<Tuple> featuresIteration1;
	private HashSet<Tuple> opinionWordsIteration1;
	private HashSet<Tuple> featuresIteration2;
	private HashSet<Tuple> opinionWordsIteration2;
	
	public DoublePropagationAlgorithm(DoublePropagationData data) {
		opinionWordExtractorService = new OpinionWordExtractorService();
		targetExtractorService = new TargetExtractorService();
		this.data = data;
	}
	
	private void Initialize() {
		data.setExpandedOpinionWordDictionary(new HashSet<Tuple>());
		featuresIteration1 = new HashSet<Tuple>();
		opinionWordsIteration1 = new HashSet<Tuple>(); 
		featuresIteration2 = new HashSet<Tuple>();
		opinionWordsIteration2 = new HashSet<Tuple>();
	}
	
	public DoublePropagationData Execute(DoublePropagationData data) {
		Initialize();
		
		do
		{
			ExecuteStep();
		}
		while(featuresIteration1.size() > 0 && opinionWordsIteration1.size() > 0);
		
		return data;		
	}
	
	private void ExecuteStep(){
		//HashSet-ul ar trebui sa filtreze automat duplicatele, trebuie verificat sa fim siguri
		// si daca o face, are rost sa facem noi verificarea sau lasam asa ? 
		for(String sentence : data.getInputSentences()){
			featuresIteration1.addAll(targetExtractorService.ExtractTargetUsingR1(sentence));
			opinionWordsIteration1.addAll(opinionWordExtractorService.ExtractOpinionWordR2(sentence));
		}	
		
	    data.getFeaturesDictionary().addAll(featuresIteration1);
	    data.getExpandedOpinionWordDictionary().addAll(opinionWordsIteration1);
	    
		for(String sentence : data.getInputSentences()){
			featuresIteration2.addAll(targetExtractorService.ExtractTargetUsingR3(sentence));
			opinionWordsIteration2.addAll(opinionWordExtractorService.ExtractOpinionWordR4(sentence));
		}	
		
		featuresIteration1.addAll(featuresIteration2);
		opinionWordsIteration1.addAll(opinionWordsIteration1);
		data.getFeaturesDictionary().addAll(featuresIteration2);
		data.getExpandedOpinionWordDictionary().addAll(opinionWordsIteration2);
	}
}
