package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;

public class EvaluationService {

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

	private void evaluate() {
		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;

		for (final Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Seed)
					|| tuple.getElements(ElementType.OPINION_WORD).size() <= 0)
				continue;

			for (final Word opinionWord : tuple
					.getElements(ElementType.OPINION_WORD)) {
				boolean found = false;
				for (final EvaluationModel model : evaluationModels) {
					if (model.getSentenceIndex() == tuple.getSentenceIndex()) {
						if (opinionWord.getValue().equals(
								model.getOpinionWord())) {
							truePositive++;
							found = true;
							break;
						}
					}
				}

				if (!found) {
					// System.out.println(tuple.getOpinionWord().getValue() +
					// tuple.getOpinionWord().getPosTag() + " - " +
					// tuple.getSentence());
					falsePositive++;
				}
			}
		}

		for (final EvaluationModel model : evaluationModels) {
			boolean found = false;
			for (final Tuple tuple : tuples) {
				if (tuple.getTupleType().equals(TupleType.Seed)
						|| tuple.getElements(ElementType.OPINION_WORD).size() <= 0)
					continue;
				if (model.getSentenceIndex() == tuple.getSentenceIndex()) {
					for (final Word opinionWord : tuple
							.getElements(ElementType.OPINION_WORD)) {
						if (opinionWord.getValue().equals(
								model.getOpinionWord())) {
							found = true;
							break;
						}
					}
				}
			}

			if (!found) {
				System.out.println(model.getOpinionWord() + " (" + model.getSentenceIndex() + ") " + " - "
						+ model.getCleanSentence());
				falseNegative++;
			}
		}
	}
}
