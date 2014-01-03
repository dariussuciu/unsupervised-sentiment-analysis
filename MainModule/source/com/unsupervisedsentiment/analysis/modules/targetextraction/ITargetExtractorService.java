package com.unsupervisedsentiment.analysis.modules.targetextraction;
import java.util.List;

import  com.unsupervisedsentiment.analysis.model.*;

public interface ITargetExtractorService {
	public List<DoublePropagationElement> ExtractTargetUsingR1(String sentence);
	
	public List<DoublePropagationElement> ExtractTargetUsingR3(String sentence);
	
	public List<DoublePropagationElement> ExtractTargetUsingR11(String sentence);
	
	public List<DoublePropagationElement> ExtractTargetUsingR12(String sentence);
	
	public List<DoublePropagationElement> ExtractTargetUsingR31(String sentence);
	
	public List<DoublePropagationElement> ExtractTargetUsingR32(String sentence);
}
