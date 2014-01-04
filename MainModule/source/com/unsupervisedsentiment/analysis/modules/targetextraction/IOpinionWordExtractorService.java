package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.List;
import com.unsupervisedsentiment.analysis.model.Tuple;

public interface IOpinionWordExtractorService {
	public List<Tuple> ExtractOpinionWordR2(String sentence);
	
	public List<Tuple> ExtractOpinionWordR4(String sentence);
	
	public List<Tuple> ExtractOpinionWordUsingR21(String sentence);
	
	public List<Tuple> ExtractOpinionWordUsingR22(String sentence);
	
	public List<Tuple> ExtractOpinionWordUsingR41(String sentence);
	
	public List<Tuple> ExtractOpinionWordUsingR42(String sentence);
}
