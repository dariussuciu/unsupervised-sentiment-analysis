package com.unsupervisedsentiment.analysis.core;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AlgorithmRunner algorithmRunner = new AlgorithmRunner();

		Config config = Initializer.getConfig();
		
		algorithmRunner.runAlgorithm();
		
		Double polarityThreshold = Double.valueOf(config.getPolarityThreshold());

		for (double d = polarityThreshold; d <= 0.21; d += 0.01) {
			if (d > 0.01) {
				d += 0.05;
			}
			
			Integer targetThreshold = Integer.valueOf(config.getTargetFrequencyThreshold());

			for (int t = targetThreshold; t <= 4; t++) {

				algorithmRunner.runAlgorithm();
				config.setTargetFrequencyThreshold(String.valueOf(t));
				Initializer.setConfig(config);
			}
			config.setPolarityThreshold(String.valueOf(d));
			Initializer.setConfig(config);
			config.setTargetFrequencyThreshold(String.valueOf(targetThreshold));
			Initializer.setConfig(config);
		}
	}
}
