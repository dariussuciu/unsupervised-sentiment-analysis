package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;

public class EvaluationService {
	
	private List<EvaluationModel> evaluationModels;
	private Set<Tuple> tuples;	
	//correct
	private double truePositive;
	//incorect
	private double falsePositive;
	//correct
	private double falseNegative;
	
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

	//tp / (tp + fp)
	private double getPrecision() {
		return truePositive / (truePositive + falsePositive);
	}
	
	//tp / (tp + fn)
	private double getRecall() {
		return truePositive / (truePositive + falseNegative);
	}
	
	private void evaluate() {
		truePositive = 0;
		falsePositive = 0;
		falseNegative = 0;
		
		for(Tuple tuple : tuples)
		{
			if(tuple.getTupleType().equals(TupleType.Seed) || tuple.getOpinionWords().size() <= 0)
				continue;

			for(Word opinionWord : tuple.getOpinionWords())
			{
				boolean found = false;
				for(EvaluationModel model : evaluationModels)
				{	
					if(model.getSentenceIndex() == tuple.getSentenceIndex())
					{
						if(opinionWord.getValue().equals(model.getOpinionWord()))
						{
								truePositive++;
								found = true;
								break;
						}
					}
				}
				
				if(!found)
					falsePositive++;
			}
		}
		
		for(EvaluationModel model : evaluationModels)
		{
			boolean found = false;
			for(Tuple tuple : tuples)
			{
				if(tuple.getTupleType().equals(TupleType.Seed) || tuple.getOpinionWords().size() <= 0)
					continue;
				if(model.getSentenceIndex() == tuple.getSentenceIndex())
				{
					for(Word opinionWord : tuple.getOpinionWords())
					{
						if(opinionWord.getValue().equals(model.getOpinionWord()))
						{
							found = true;
							break;
						}
					}
				}
			}
			
			if(!found)
				falseNegative++;
		}	
	}
}
