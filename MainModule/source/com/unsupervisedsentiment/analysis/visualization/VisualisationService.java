package com.unsupervisedsentiment.analysis.visualization;

import java.util.Map;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.model.FileVisualisationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;

public class VisualisationService {

	private Config config;
	private static VisualisationService instance;

	public static VisualisationService getInstance(Config config) {
		if (instance == null)
			instance = new VisualisationService(config);

		return instance;
	}

	private VisualisationService(Config config) {
		this.config = config;
	}

	public FileVisualisationModel createFileVisualisationModel(Set<Tuple> tuples,
			Map<Double, Integer> polarityDistribution, EvaluationMetadata evaluationMetadataResults) {
		return new FileVisualisationModel(tuples, polarityDistribution, evaluationMetadataResults);
	}

}
