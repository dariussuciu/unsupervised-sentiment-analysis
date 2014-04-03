package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.lang.reflect.Field;
import java.util.Date;

import com.unsupervisedsentiment.analysis.core.constants.Constants;

public class EvaluationMetadata {
	private String date;
	private final String seedType;
	private final String filename;
	private final String numberOfSeeds;
	private final String numberOfIterations;
	private final String durationMilliseconds;
	private final String precision;
	private final String recall;
	private final String threshold;
	private final String totalRelationsUsed;

	public EvaluationMetadata(String date, final String seedType,
			final String filename, final String numberOfSeeds,
			final String numberOfIterations, final String durationMilliseconds,
			final String precision, final String recall,
			final String threshold, final String totalRelationsUsed) {
		this.date = date;
		this.seedType = seedType;
		this.filename = filename;
		this.numberOfSeeds = numberOfSeeds;
		this.numberOfIterations = numberOfIterations;
		this.durationMilliseconds = durationMilliseconds;
		this.precision = precision;
		this.recall = recall;
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

	public String getPrecision() {
		return precision;
	}

	public String getRecall() {
		return recall;
	}

	public String getThreshold() {
		return threshold;
	}

	public String getTotalRelationsUsed() {
		return totalRelationsUsed;
	}

	public String[] getCSVdata() {
		int numberOfFields = this.getClass().getDeclaredFields().length;
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
		s[6] = getPrecision();
		s[7] = getRecall();
		s[8] = getThreshold();
		s[9] = getTotalRelationsUsed();
		return s;
	}

	@Override
	public String toString() {
		return Constants.sdf.format(date) + "," + seedType + "," + filename
				+ "," + String.valueOf(numberOfSeeds) + ","
				+ String.valueOf(numberOfIterations) + ","
				+ String.valueOf(durationMilliseconds) + ","
				+ String.valueOf(precision) + "," + String.valueOf(recall)
				+ "," + totalRelationsUsed;
	}

}
