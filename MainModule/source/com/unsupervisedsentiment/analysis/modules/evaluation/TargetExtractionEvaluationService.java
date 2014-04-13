package com.unsupervisedsentiment.analysis.modules.evaluation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;

public class TargetExtractionEvaluationService extends EvaluationService {

	public TargetExtractionEvaluationService(List<EvaluationModel> evaluationModels,
			Set<Tuple> tuples) {
		super(evaluationModels, tuples);
	}

	@Override
	protected void evaluate() {
		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;
		List<Word> targets = ExtractElements(tuples, ElementType.FEATURE);
		List<Word> filteredTargets = GetFilteredTargets(targets);
		for (final Word target : filteredTargets) {
			boolean found = false;
			for (final EvaluationModel model : evaluationModels) {
				if (target.getSentenceIndex() == model.getSentenceIndex()) {
					if (target.getValue().equals(
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
					if(tuple.getSentenceIndex() == target.getSentenceIndex())
					{
						// System.out.println(target.getValue() +
						//		 target.getPosTag() + " - " +
						//		 target.getSentenceIndex() + " - " + tuple.getSentence());
						 break;
					}
				}
				falsePositive++;
			}
		}

		for (final EvaluationModel model : evaluationModels) {
			boolean found = false;
			for (final Word target : filteredTargets) {
				if (target.getValue().equals(
						model.getElement()) && model.getSentenceIndex() == target.getSentenceIndex()) {
					found = true;
				}
			}

			if (!found) {
				//System.out.println(model.getElement() + " (" + model.getSentenceIndex() + ") " + " - "
				//		+ model.getCleanSentence());
				falseNegative++;
			}
		}
	}
	
	private List<Word> ExtractElements(Set<Tuple> tuples, ElementType elementType) {
		List<Word> elements = new ArrayList<Word>();
		
		for(Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Seed)
					|| tuple.getElements(elementType).size() <= 0)
				continue;
			
			for(Word foundElement : tuple.getElements(elementType))
			{
				int numberOfInstances = 1;
				boolean isDuplicate = false;
				for(Word existingElement : elements) {
					if(existingElement.equals(foundElement) && existingElement.getSentenceIndex() == foundElement.getSentenceIndex())
					{
						isDuplicate = true;
					}
				}
				for(Tuple otherTuple : tuples) {
					for(Word otherElement : otherTuple.getElements(elementType)) {
						if(otherElement.equals(foundElement))
						{
							numberOfInstances++;
						}
					}
				}
				
				if(!isDuplicate)
				{
					foundElement.setNumberOfInstances(numberOfInstances);
					elements.add(foundElement);
				}
				else 
				{
					//System.out.println("Duplicate: " + foundOpinionWord.getValue() + " - " + foundOpinionWord.getSentenceIndex() );
				}
			}
		}
		
		return elements;
	}
	
	private List<Word> GetFilteredTargets(List<Word> targets) {
		List<Word> filteredTargets = new ArrayList<Word>();
		for(Word word : targets) 
		{
			if(word.getNumberOfInstances() > 0)
				filteredTargets.add(word);
		}
		return filteredTargets;
	}
}
