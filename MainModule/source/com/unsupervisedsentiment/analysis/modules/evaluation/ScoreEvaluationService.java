package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

public class ScoreEvaluationService extends EvaluationService {
	private double ACCEPTABLE_ERROR = 0.3;

	public ScoreEvaluationService(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples,double acceptableError) {
		super(evaluationModels, tuples);
		ACCEPTABLE_ERROR = acceptableError;
	}

	@Override
	protected void evaluate() {

		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;
		
		List<Word> opinionWords = Helpers.ExtractElements(tuples, ElementType.OPINION_WORD);
		for (final Word opinionWord : opinionWords) {
			boolean found = false;
			for (final EvaluationModel model : evaluationModels) {
				if (opinionWord.getSentenceIndex() == model.getSentenceIndex()) {
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
				
				falsePositive++;
			}
		}

		for (final EvaluationModel model : evaluationModels) {
			boolean found = false;
			for (final Word opinionWord : opinionWords) {
				if (model.getOpinionWordScore() == Classification.DEFAULT_SCORE)
					continue;
				double assignedScore = opinionWord.getScore();
				double annotatedScore = model.getOpinionWordScore();
				if (Math.abs(assignedScore - annotatedScore) < ACCEPTABLE_ERROR) {
					found = true;
					break;
				}
			}

			if (!found) {
				falseNegative++;
			}
		}
	}

	public static void performEvaluation(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		Config config = Initializer.getConfig();
		double acceptableError = Double.parseDouble(config.getScoringThreshold());
		ScoreEvaluationService scoreEvaluationService = new ScoreEvaluationService(evaluationModels, tuples, acceptableError);
		EvaluationResult scoreEvaluationResult = scoreEvaluationService.getResults();
		System.out.println("Score Precision : "
				+ scoreEvaluationResult.getPrecision());
		System.out.println("Score Recall : "
				+ scoreEvaluationResult.getRecall());
		System.out.println("-----------------------------------------");
	}
}
