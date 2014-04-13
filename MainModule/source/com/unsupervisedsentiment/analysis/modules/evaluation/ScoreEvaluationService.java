package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;

public class ScoreEvaluationService extends EvaluationService {
	private final double ACCEPTABLE_ERROR = 0.3;

	public ScoreEvaluationService(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		super(evaluationModels, tuples);
	}

	@Override
	protected void evaluate() {

		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;

		for (final Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Seed)
					|| tuple.getElements(ElementType.OPINION_WORD).size() <= 0)
				continue;

			List<Word> opinionWords = tuple.getElements(ElementType.OPINION_WORD);
			for (final Word opinionWord : opinionWords) {
				boolean found = false;
				for (final EvaluationModel model : evaluationModels) {
					if (model.getSentenceIndex() == tuple.getSentenceIndex()) {
						if (model.getOpinionWordScore() == Classification.DEFAULT_SCORE)
							continue;
						double assignedScore = opinionWord.getScore();
						double annotatedScore = model.getOpinionWordScore();
						if (Math.abs(assignedScore - annotatedScore) < ACCEPTABLE_ERROR) {
							truePositive++;
							found = true;
							break;
						}
					}
				}

				if (!found) {
					 System.out.println(tuple.getElements(ElementType.OPINION_WORD).get(0).getValue()+ " - "+
					 tuple.getSentence());
					
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
					for (final Word opinionWord : tuple.getElements(ElementType.OPINION_WORD)) {
						if (model.getOpinionWordScore() == Classification.DEFAULT_SCORE)
							continue;
						double assignedScore = opinionWord.getScore();
						double annotatedScore = model.getOpinionWordScore();
						if (Math.abs(assignedScore - annotatedScore) < ACCEPTABLE_ERROR) {
							found = true;
							break;
						}
					}
				}
			}

			if (!found)
//				System.out.println(model.getOpinionWord() + " (" + model.getSentenceIndex() + ") " + " - "
//						+ model.getCleanSentence());
				falseNegative++;
		}
	}

	public static void performEvaluation(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		ScoreEvaluationService scoreEvaluationService = new ScoreEvaluationService(evaluationModels, tuples);
		EvaluationResult scoreEvaluationResult = scoreEvaluationService.getResults();
		System.out.println("Score Precision : "
				+ scoreEvaluationResult.getPrecision());
		System.out.println("Score Recall : "
				+ scoreEvaluationResult.getRecall());
		System.out.println("-----------------------------------------");
	}
}
