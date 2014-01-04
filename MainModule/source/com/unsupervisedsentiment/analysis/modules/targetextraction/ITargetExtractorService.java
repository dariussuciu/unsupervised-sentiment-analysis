package com.unsupervisedsentiment.analysis.modules.targetextraction;
import java.util.List;

import  com.unsupervisedsentiment.analysis.model.*;

public interface ITargetExtractorService {
	public List<Tuple> ExtractTargetUsingR1(String sentence);
	
	public List<Tuple> ExtractTargetUsingR3(String sentence);
	
	public List<Tuple> ExtractTargetUsingR11(String sentence);
	
	public List<Tuple> ExtractTargetUsingR12(String sentence);
	
	public List<Tuple> ExtractTargetUsingR31(String sentence);
	
	public List<Tuple> ExtractTargetUsingR32(String sentence);
}
