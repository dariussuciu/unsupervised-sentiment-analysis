package com.unsupervisedsentiment.analysis.model;

import com.unsupervisedsentiment.analysis.core.constants.relations.RelationsContainer;

public class Word {
	private String posTag;
	private String value;
	private ElementType type;
	private double score;
	private double sentiWordScore;
	private int sentenceIndex;
	private int numberOfInstances;

	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}

	public String getValue() {
		return value;
	}
	
	public String getPattern() {
		String pattern;
		if(value.contains("*"))
			pattern = value.replace("*", "\\*");
		else pattern = value;
		return pattern;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Word(String posTag, String value, ElementType type) {
		super();
		this.posTag = posTag;
		this.value = value;
		this.type = type;
		this.numberOfInstances = 0;
	}

	public Word(String posTag) {
		super();
		this.posTag = posTag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (posTag == null) {
			if (other.posTag != null)
				return false;
		} else if (!posTag.equals(other.posTag)) {
			if (!RelationsContainer.arePosEquivalent(posTag, other.posTag)) {
				return false;
			}
		}
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getSentiWordScore() {
		return sentiWordScore;
	}

	public void setSentiWordScore(double sentiWordScore) {
		this.sentiWordScore = sentiWordScore;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	public int getNumberOfInstances() {
		return numberOfInstances;
	}

	public void setNumberOfInstances(int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}
}
