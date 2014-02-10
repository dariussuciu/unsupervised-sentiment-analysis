package com.unsupervisedsentiment.analysis.modules.targetextraction;
import java.util.HashSet;
import java.util.List;

import  com.unsupervisedsentiment.analysis.model.*;

public interface ITargetExtractorService {
	public List<Tuple> ExtractTargetUsingR1(HashSet<Tuple> opinionWords);
	
	public List<Tuple> ExtractTargetUsingR3(HashSet<Tuple> targets);
	
	public List<Tuple> ExtractTargetUsingR11(HashSet<Tuple> opinionWords);
	
	public List<Tuple> ExtractTargetUsingR12(HashSet<Tuple> opinionWords);
	
	public List<Tuple> ExtractTargetUsingR31(HashSet<Tuple> targets);
	
	public List<Tuple> ExtractTargetUsingR32(HashSet<Tuple> targets);
}
