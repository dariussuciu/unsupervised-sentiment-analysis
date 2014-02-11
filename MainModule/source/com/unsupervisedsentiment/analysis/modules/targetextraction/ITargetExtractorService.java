package com.unsupervisedsentiment.analysis.modules.targetextraction;
import java.util.HashSet;
import java.util.List;

import  com.unsupervisedsentiment.analysis.model.*;

public interface ITargetExtractorService {
	public List<Tuple> extractTargetUsingR1(HashSet<Tuple> opinionWords);
	
	public List<Tuple> extractTargetUsingR3(HashSet<Tuple> targets);
	
	public List<Tuple> extractTargetUsingR11(HashSet<Tuple> opinionWords);
	
	public List<Tuple> extractTargetUsingR12(HashSet<Tuple> opinionWords);
	
	public List<Tuple> extractTargetUsingR31(HashSet<Tuple> targets);
	
	public List<Tuple> extractTargetUsingR32(HashSet<Tuple> targets);
}
