package com.unsupervisedsentiment.analysis.core;

import javax.swing.SwingUtilities;

import com.unsupervisedsentiment.analysis.modules.services.wordnet.WordNetService;
import com.unsupervisedsentiment.analysis.visualization.FileChooser;

import edu.smu.tspell.wordnet.SynsetType;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final AlgorithmRunner algorithmRunner = new AlgorithmRunner();

		Config config = Initializer.getConfig();

		//WordNetService wordNetService = new WordNetService();
		//String result = wordNetService.searchByWord("flash drive", SynsetType.NOUN);
		
		algorithmRunner.runAlgorithm();

//		Double polarityThreshold = Double.valueOf(config.getPolarityThreshold());
//
//		for (double d = polarityThreshold; d <= 0.21; d += 0.01) {
//			if (d > 0.01) {
//				d += 0.05;
//			}
//
//			Integer targetThreshold = Integer.valueOf(config.getTargetFrequencyThreshold());
//
//			for (int t = targetThreshold; t <= 4; t++) {
//
//				algorithmRunner.runAlgorithm();
//				config.setTargetFrequencyThreshold(String.valueOf(t));
//				Initializer.setConfig(config);
//			}
//			config.setPolarityThreshold(String.valueOf(d));
//			Initializer.setConfig(config);
//			config.setTargetFrequencyThreshold(String.valueOf(targetThreshold));
//			Initializer.setConfig(config);
//		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FileChooser(algorithmRunner.getFileVisualisationModels());
			}
		});
	}
}
