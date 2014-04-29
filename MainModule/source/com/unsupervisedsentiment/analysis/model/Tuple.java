package com.unsupervisedsentiment.analysis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Tuple contains a source word and a target. The source is the starting
 * word/point and the target is found through this source.
 * 
 */
public class Tuple {

	private Word source;
	private Word target;

	private Dependency dependency;

	private TupleType tupleType;
	private int sentenceIndex;
	private String sentence;

	public Tuple() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dependency == null) ? 0 : dependency.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((tupleType == null) ? 0 : tupleType.hashCode());
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Tuple other = (Tuple) obj;
		
		if(this.getTupleType().equals(TupleType.Seed) && other.getTupleType().equals(TupleType.Seed))
		{
			return this.source.equals(other.source);

		}
		else 
		{
			if(this.getSentenceIndex() != (other.getSentenceIndex()))
				{
					return false;
				}
					
			if(this.source == null && this.target == null) {
				if(other.source != null || other.target != null)
					return false;
			}
			else {
				if(this.source == null)
				{
					if(other.source == null && !other.target.equals(this.target))
						return false;
					if(other.target == null && !other.source.equals(this.target))
						return false;
				}
				
				if(this.target == null)
				{
					if(other.source == null && !other.target.equals(this.source))
						return false;
					if(other.target == null && !other.source.equals(this.source))
						return false;
				}
				
				if(this.source.equals(other.source))
				{
					if(!this.target.equals(other.target))
						return false;
				}
				
				if(this.target.equals(other.target))
				{
					if(!this.source.equals(other.source))
						return false;
				}
				
				if(this.source.equals(other.target))
				{
					if(!this.target.equals(other.source))
						return false;
				}
				
				if(this.target.equals(other.source))
				{
					if(!this.source.equals(other.target))
						return false;
					
				}			
			}
		}
		return true;
	}

	public Tuple(Word source, Word target, Dependency dependency, TupleType tupleType, int sentenceIndex, String sentence) {
		super();
		this.source = source;
		this.target = target;
		this.dependency = dependency;
		this.tupleType = tupleType;
		this.sentenceIndex = sentenceIndex;
		this.sentence = sentence;
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

	public TupleType getTupleType() {
		return tupleType;
	}

	public void setTupleType(TupleType tupleType) {
		this.tupleType = tupleType;
	}

	public Word getTarget() {
		return target;
	}

	public void setTarget(Word target) {
		this.target = target;
	}

	public Word getSource() {
		return source;
	}

	public void setSource(Word source) {
		this.source = source;
	}

	public List<Word> getElements(ElementType elementType){
		List<Word> elements = new ArrayList<Word>();
		if(getSource().getType().equals(elementType))
			elements.add(getSource());
		if(getTarget() != null && getTarget().getType().equals(elementType))
			elements.add(getTarget());
		return elements;
	}
	
//	public Word getOpinionWord(){
//		Word word = getSource().getType().equals(ElementType.OPINION_WORD) ? getSource() : getTarget();
//		return word;
//	}
	
//	public Word getFeatureWord(){
//		Word word = getSource().getType().equals(ElementType.FEATURE) ? getSource() : getTarget();
//		return word;
//	}
	
	public List<Word> getFeatureWords(){
		List<Word> targets = new ArrayList<Word>();
		if(getSource().getType().equals(ElementType.FEATURE))
			targets.add(getSource());
		if(getTarget().getType().equals(ElementType.FEATURE))
			targets.add(getTarget());
		return targets;
	}

	public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
}
