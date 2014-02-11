package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Tuple;

public interface IOpinionWordExtractorService {
	public List<Tuple> extractOpinionWordR2(HashSet<Tuple> targets);
	
	public List<Tuple> extractOpinionWordR4(HashSet<Tuple> opinionWords);
	
	public List<Tuple> extractOpinionWordUsingR21(HashSet<Tuple> targets);
	
	public List<Tuple> extractOpinionWordUsingR22(HashSet<Tuple> targets);
	
	public List<Tuple> extractOpinionWordUsingR41(HashSet<Tuple> opinionWords);
	
	public List<Tuple> extractOpinionWordUsingR42(HashSet<Tuple> opinionWords);
}
