package com.unsupervisedsentiment.analysis.core;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AlgorithmRunner algorithmRunner = new AlgorithmRunner();

		Config config = Initializer.getConfig();
		
		Integer targetThreshold = Integer.valueOf(config.getTargetFrequencyThreshold());
		Double polarityThreshold = Double.valueOf(config.getPolarityThreshold());

		for (double d = polarityThreshold; d < 0.5; d += 0.01) {
			if (polarityThreshold > 0.01) {
				polarityThreshold += 0.1;
			}

			for (int i = 0, t = targetThreshold; i < 5; i++) {

				if (i < 3)
					t++;
				else {
					t += 3;
				}

				algorithmRunner.runAlgorithm();
				config.setTargetFrequencyThreshold(String.valueOf(t));
				Initializer.setConfig(config);
			}
			config.setPolarityThreshold(String.valueOf(d));
			Initializer.setConfig(config);
		}
	}
}
