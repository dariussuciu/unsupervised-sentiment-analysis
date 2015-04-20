package com.unsupervisedsentiment.analysis.model;

import java.util.Map;
import java.util.Set;

import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;

public class FileVisualisationModel {

	private final Set<Tuple> tuples;
	private final Map<Double, Integer> polarityDistribution;
	private final EvaluationMetadata evaluationMetadataResults;

	public FileVisualisationModel(Set<Tuple> tuples, Map<Double, Integer> polarityDistribution,
			EvaluationMetadata evaluationMetadataResults) {
		super();
		this.tuples = tuples;
		this.polarityDistribution = polarityDistribution;
		this.evaluationMetadataResults = evaluationMetadataResults;
	}

	public Set<Tuple> getTuples() {
		return tuples;
	}

	public Map<Double, Integer> getPolarityDistribution() {
		return polarityDistribution;
	}

	public EvaluationMetadata getEvaluationMetadataResults() {
		return evaluationMetadataResults;
	}

	@Override
	public String toString() {
		return evaluationMetadataResults.getFilename();
	}
}
