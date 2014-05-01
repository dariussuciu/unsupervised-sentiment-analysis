package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.lang.reflect.Field;

import com.unsupervisedsentiment.analysis.core.constants.Constants;
import com.unsupervisedsentiment.analysis.model.ResultPrecRecall;

public class EvaluationMetadata {
	private String date;
	private final String seedType;
	private final String filename;
	private final String numberOfSeeds;
	private final String numberOfIterations;
	private final String durationMilliseconds;
	private final ResultPrecRecall targetsResult;
	private final ResultPrecRecall opinionWordsResult;
	private final String threshold;
	private final String totalRelationsUsed;

	public EvaluationMetadata(String date, final String seedType, final String filename, final String numberOfSeeds,
			final String numberOfIterations, final String durationMilliseconds, final ResultPrecRecall targetsResult,
			final ResultPrecRecall opinionWordsResult, final String threshold, final String totalRelationsUsed) {
		this.date = date;
		this.seedType = seedType;
		this.filename = filename;
		this.numberOfSeeds = numberOfSeeds;
		this.numberOfIterations = numberOfIterations;
		this.durationMilliseconds = durationMilliseconds;
		this.targetsResult = targetsResult;
		this.opinionWordsResult = opinionWordsResult;
		this.threshold = threshold;
		this.totalRelationsUsed = totalRelationsUsed;
	}

	public String getDate() {
		return date;
	}

	public String getSeedType() {
		return seedType;
	}

	public String getFilename() {
		return filename;
	}

	public String getNumberOfSeeds() {
		return numberOfSeeds;
	}

	public String getNumberOfIterations() {
		return numberOfIterations;
	}

	public String getDurationMilliseconds() {
		return durationMilliseconds;
	}

	public String getThreshold() {
		return threshold;
	}

	public String getTotalRelationsUsed() {
		return totalRelationsUsed;
	}

	public ResultPrecRecall getTargetsResult() {
		return targetsResult;
	}

	public ResultPrecRecall getOpinionWordsResult() {
		return opinionWordsResult;
	}

	public String[] getCSVdata() {
		int numberOfFields = this.getClass().getDeclaredFields().length + 2;
		Field[] fields = this.getClass().getDeclaredFields();
		String[] s = new String[numberOfFields];
		//
		// for (int i = 0; i < numberOfFields; i++) {
		// s[i] = this.getClass().getDeclaredField(fields[i].getName())
		// .get(this.getClass().getDeclaredField(fields[i].getName())).toString();
		// }

		s[0] = getDate();
		s[1] = getSeedType();
		s[2] = getFilename();
		s[3] = getNumberOfSeeds();
		s[4] = getNumberOfIterations();
		s[5] = getDurationMilliseconds();
		s[6] = opinionWordsResult.getPrecision();
		s[7] = opinionWordsResult.getRecall();
		s[8] = targetsResult.getPrecision();
		s[9] = targetsResult.getRecall();
		s[10] = getThreshold();
		s[11] = getTotalRelationsUsed();
		return s;
	}

	@Override
	public String toString() {
		return Constants.sdf.format(date) + "," + seedType + "," + filename + "," + String.valueOf(numberOfSeeds) + ","
				+ String.valueOf(numberOfIterations) + "," + String.valueOf(durationMilliseconds) + ","
				+ String.valueOf(opinionWordsResult.getPrecision()) + ","
				+ String.valueOf(opinionWordsResult.getRecall()) + String.valueOf(targetsResult.getPrecision()) + ","
				+ String.valueOf(targetsResult.getRecall()) + "," + totalRelationsUsed;
	}

}
