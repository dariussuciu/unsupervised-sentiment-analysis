package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.List;
import com.unsupervisedsentiment.analysis.model.DoublePropagationElement;

public interface IOpinionWordExtractorService {
	public List<DoublePropagationElement> ExtractOpinionWordR2(String sentence);
	
	public List<DoublePropagationElement> ExtractOpinionWordR4(String sentence);
	
	public List<DoublePropagationElement> ExtractOpinionWordUsingR21(String sentence);
	
	public List<DoublePropagationElement> ExtractOpinionWordUsingR22(String sentence);
	
	public List<DoublePropagationElement> ExtractOpinionWordUsingR41(String sentence);
	
	public List<DoublePropagationElement> ExtractOpinionWordUsingR42(String sentence);
}
