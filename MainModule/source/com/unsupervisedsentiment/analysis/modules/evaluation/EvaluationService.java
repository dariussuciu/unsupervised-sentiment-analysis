package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;

public abstract class EvaluationService {

	protected List<EvaluationModel> evaluationModels;
	protected Set<Tuple> tuples;
	// correct
	protected double truePositive;
	// incorect
	protected double falsePositive;
	// correct
	protected double falseNegative;

	public EvaluationService(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		this.evaluationModels = evaluationModels;
		this.tuples = tuples;
	}

	public EvaluationResult getResults() {
		evaluate();
		EvaluationResult result = new EvaluationResult();
		result.setPrecision(getPrecision());
		result.setRecall(getRecall());
		return result;
	}

	// tp / (tp + fp)
	protected double getPrecision() {
		return truePositive / (truePositive + falsePositive);
	}

	// tp / (tp + fn)
	protected double getRecall() {
		return truePositive / (truePositive + falseNegative);
	}

	protected abstract void evaluate();
}
