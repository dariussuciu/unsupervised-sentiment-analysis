package com.unsupervisedsentiment.analysis.model;

public class DoublePropagationElement {
	private String word;
	private ElementType type;
	private Quadruple quadruple;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoublePropagationElement other = (DoublePropagationElement) obj;
		if (type != other.type)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public ElementType getType() {
		return type;
	}
	public void setType(ElementType type) {
		this.type = type;
	}
	public Quadruple getQuadruple() {
		return quadruple;
	}
	public void setQuadruple(Quadruple quadruple) {
		this.quadruple = quadruple;
	}
}
