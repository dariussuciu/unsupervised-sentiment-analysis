package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

public class OpinionWordExtractionEvaluationService extends EvaluationService {

	public OpinionWordExtractionEvaluationService(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		super(evaluationModels, tuples);
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
					if (opinionWord.getValue().equals(
							model.getElement())) {
						truePositive++;
						found = true;
						break;
					}
				}
			}

			if (!found) {
				for(Tuple tuple : tuples) 
				{
					if(tuple.getSentenceIndex() == opinionWord.getSentenceIndex())
					{
//						 System.out.println(opinionWord.getScore() +
//								 opinionWord.getPosTag() + " - " +
//								 opinionWord.getSentenceIndex() + " - " + tuple.getSentence());
						 break;
					}
				}
				falsePositive++;
			}
		}

		for (final EvaluationModel model : evaluationModels) {
			boolean found = false;
			for (final Word opinionWord : opinionWords) {
				if (opinionWord.getValue().equals(
						model.getElement()) && model.getSentenceIndex() == opinionWord.getSentenceIndex()) {
					found = true;
				}
			}

			if (!found) {
				//System.out.println(model.getOpinionWord() + " (" + model.getSentenceIndex() + ") " + " - "
				//		+ model.getCleanSentence());
				falseNegative++;
			}
		}
		
//		for (final Word target : targets) {
//			boolean found = false;
//			for (final EvaluationModel model : evaluationModels) {
//				if (target.getSentenceIndex() == model.getSentenceIndex()) {
//					if (target.getScore().equals(
//							model.getTarget())) {
//						//truePositive++;
//						found = true;
//						break;
//					}
//				}
//			}
//
//			if (!found) {
//				// System.out.println(opinionWord.getScore() +
//				//		 opinionWord.getPosTag() + " - " +
//				//		 opinionWord.getSentenceIndex());
//				//falsePositive++;
//			}
//		}
	}
}
