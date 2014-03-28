package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.Date;

import com.unsupervisedsentiment.analysis.core.constants.Constants;

public class EvaluationMetadata {
	private final Date date;
	private final String seedType;
	private final String filename;
	private final Integer numberOfSeeds;
	private final Integer numberOfIterations;
	private final Long durationMilliseconds;
	private final Double precision;
	private final Double recall;
	private final String totalRelationsUsed;

	public EvaluationMetadata(final Date date, final String seedType,
			final String filename, final Integer numberOfSeeds,
			final Integer numberOfIterations, final Long durationMilliseconds,
			final Double precision, final Double recall,
			final String totalRelationsUsed) {
		this.date = date;
		this.seedType = seedType;
		this.filename = filename;
		this.numberOfSeeds = numberOfSeeds;
		this.numberOfIterations = numberOfIterations;
		this.durationMilliseconds = durationMilliseconds;
		this.precision = precision;
		this.recall = recall;
		this.totalRelationsUsed = totalRelationsUsed;
	}

	public Date getDate() {
		return date;
	}

	public String getSeedType() {
		return seedType;
	}

	public String getFilename() {
		return filename;
	}

	public Integer getNumberOfSeeds() {
		return numberOfSeeds;
	}

	public Integer getNumberOfIterations() {
		return numberOfIterations;
	}

	public Long getDurationMilliseconds() {
		return durationMilliseconds;
	}

	public Double getPrecision() {
		return precision;
	}

	public Double getRecall() {
		return recall;
	}

	public String getTotalRelationsUsed() {
		return totalRelationsUsed;
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
