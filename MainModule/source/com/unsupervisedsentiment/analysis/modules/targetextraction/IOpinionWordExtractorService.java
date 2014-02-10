package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Tuple;

public interface IOpinionWordExtractorService {
	public List<Tuple> ExtractOpinionWordR2(HashSet<Tuple> targets);
	
	public List<Tuple> ExtractOpinionWordR4(HashSet<Tuple> opinionWords);
	
	public List<Tuple> ExtractOpinionWordUsingR21(HashSet<Tuple> targets);
	
	public List<Tuple> ExtractOpinionWordUsingR22(HashSet<Tuple> targets);
	
	public List<Tuple> ExtractOpinionWordUsingR41(HashSet<Tuple> opinionWords);
	
	public List<Tuple> ExtractOpinionWordUsingR42(HashSet<Tuple> opinionWords);
}
