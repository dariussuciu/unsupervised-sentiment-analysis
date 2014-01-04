package com.unsupervisedsentiment.analysis.model;

public class Tuple {

	private String posTag_x;
	private String word_x;
	private ElementType type;
	private Dependency dependency;
	private String relation;
	private String posTag_y;
	private String word_y;
	

	/**
	 * @return the posTag_x
	 */
	public String getPosTag_x() {
		return posTag_x;
	}

	/**
	 * @param posTag_x
	 *            the posTag_x to set
	 */
	public void setPosTag_x(String posTag_x) {
		this.posTag_x = posTag_x;
	}

	/**
	 * @return the dependency
	 */
	public Dependency getDependency() {
		return dependency;
	}

	/**
	 * @param dependency
	 *            the dependency to set
	 */
	public void setDependency(Dependency dependency) {
		this.dependency = dependency;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * @return the posTag_y
	 */
	public String getPosTag_y() {
		return posTag_y;
	}

	/**
	 * @param posTag_y
	 *            the posTag_y to set
	 */
	public void setPosTag_y(String posTag_y) {
		this.posTag_y = posTag_y;
	}

	public String getWord_x() {
		return word_x;
	}

	public void setWord_x(String word_x) {
		this.word_x = word_x;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public String getWord_y() {
		return word_y;
	}

	public void setWord_y(String word_y) {
		this.word_y = word_y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((word_x == null) ? 0 : word_x.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		Tuple other = (Tuple) obj;
		if (type != other.type)
			return false;
		if (word_x == null) {
			if (other.word_x != null)
				return false;
		} else if (!word_x.equals(other.word_x))
			return false;
		return true;
	}

}
