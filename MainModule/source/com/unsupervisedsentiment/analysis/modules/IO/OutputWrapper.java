package com.unsupervisedsentiment.analysis.modules.IO;

import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Tuple;

public class OutputWrapper extends FileWrapper {
	private Set<Tuple> tuples;

	public OutputWrapper() {
		super();
	}

	public OutputWrapper(String author, String source, String content,
			String filename, Set<Tuple> tuples) {
		super(author, source, content, filename);
		this.tuples = tuples;
	}

	public Set<Tuple> getTuples() {
		return tuples;
	}

	public void setTuples(Set<Tuple> tuples) {
		this.tuples = tuples;
	}
}
