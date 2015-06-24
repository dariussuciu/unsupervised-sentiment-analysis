package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.doublepropagation.Helpers;

public class TargetExtractionEvaluationService extends EvaluationService {

	public TargetExtractionEvaluationService(List<EvaluationModel> evaluationModels, Set<Tuple> tuples) {
		super(evaluationModels, tuples);
	}

	@Override
	protected void evaluate() {
		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;
		List<Word> targets = Helpers.ExtractElements(tuples, ElementType.FEATURE);
		List<Word> filteredTargets = GetFilteredTargets(targets);
		for (final Word target : filteredTargets) {
			boolean found = false;
			for (final EvaluationModel model : evaluationModels) {
				if (target.getSentenceIndex() == model.getSentenceIndex()) {
					if (target.getValue().equals(model.getElement())) {
						truePositive++;
						found = true;
						break;
					}
				}
			}

			if (!found) {
				for (Tuple tuple : tuples) {
					if (tuple.getSentenceIndex() == target.getSentenceIndex()) {
						// System.out.println(target.getValue() +
						// target.getPosTag() + " - " +
						// target.getSentenceIndex()
						// + " - " + tuple.getSentence());
						break;
					}
				}
				falsePositive++;
			}
		}

		for (final EvaluationModel model : evaluationModels) {
			boolean found = false;
			for (final Word target : filteredTargets) {
				if (target.getValue().equals(model.getElement())
						&& model.getSentenceIndex() == target.getSentenceIndex()) {
					found = true;
				}
			}

			if (!found) {
				// System.out.println(model.getElement() + " (" +
				// model.getSentenceIndex() + ") " + " - "
				// + model.getCleanSentence());
				falseNegative++;
			}
		}
	}

	private List<Word> GetFilteredTargets(List<Word> targets) {
		String targetFrequencyThresholdStr = Initializer.getConfig().getTargetFrequencyThreshold();
		int frequencyThreshold = 0;
		if (targetFrequencyThresholdStr.matches("\\d+")) {
			frequencyThreshold = Integer.valueOf(targetFrequencyThresholdStr);
		}
		List<Word> filteredTargets = new ArrayList<Word>();
		for (Word word : targets) {
			//frequency filtering
			if (word.getNumberOfInstances() >= frequencyThreshold)
			{
				//WordNet filtering
				if(word.getRankScore() > 0.1)
				{
					filteredTargets.add(word);

				}
				else 
				{
					//System.out.println(word.getValue() + " - " + word.getRankScore());
				}
			}		
		}
		return filteredTargets;
	}
}
