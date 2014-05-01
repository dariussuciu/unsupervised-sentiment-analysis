package com.unsupervisedsentiment.analysis.core;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AlgorithmRunner algorithmRunner = new AlgorithmRunner();

		Double targetThreshold = Double.valueOf(Initializer.getConfig().getTargetFrequencyThreshold());
		Integer polarityThreshold = Integer.valueOf(Initializer.getConfig().getPolarityThreshold());

		for (double d = polarityThreshold; d < 1; d += 0.1) {
			for (double t = targetThreshold; t < 10; t++) {
				algorithmRunner.runAlgorithm();
				Initializer.getConfig().setTargetFrequencyThreshold(String.valueOf(t));
			}
			Initializer.getConfig().setPolarityThreshold(String.valueOf(d));
		}
	}

}
