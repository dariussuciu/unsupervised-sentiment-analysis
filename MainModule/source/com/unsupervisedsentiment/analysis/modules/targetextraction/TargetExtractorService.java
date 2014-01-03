package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.ArrayList;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.DoublePropagationElement;

public class TargetExtractorService implements ITargetExtractorService {
	

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR1(String sentence) {
		List<DoublePropagationElement> targetList = new ArrayList<DoublePropagationElement>();
		return targetList;
	}

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR3(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR11(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR12(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR31(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DoublePropagationElement> ExtractTargetUsingR32(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}
}
