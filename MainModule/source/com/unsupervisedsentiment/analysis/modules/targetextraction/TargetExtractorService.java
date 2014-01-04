package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.ArrayList;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Tuple;

public class TargetExtractorService implements ITargetExtractorService {
	

	@Override
	public List<Tuple> ExtractTargetUsingR1(String sentence) {
		List<Tuple> targetList = new ArrayList<Tuple>();
		return targetList;
	}

	@Override
	public List<Tuple> ExtractTargetUsingR3(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tuple> ExtractTargetUsingR11(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tuple> ExtractTargetUsingR12(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tuple> ExtractTargetUsingR31(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tuple> ExtractTargetUsingR32(String sentence) {
		// TODO Auto-generated method stub
		return null;
	}
}
