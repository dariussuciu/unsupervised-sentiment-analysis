package com.unsupervisedsentiment.analysis.experiment.seed;

import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Wrapper for a seed and its occurrence count in review files.
 * 
 * @author Maria
 *
 */
public class SeedFrequency implements Comparable<SeedFrequency> {

	private String word;
	private int count;

	public SeedFrequency(final String word, final int count) {
		this.word = word;
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(SeedFrequency other) {
		return new CompareToBuilder().append(this.count, other.getCount()).build();
	}

}
