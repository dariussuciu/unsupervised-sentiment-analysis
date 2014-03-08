package com.unsupervisedsentiment.analysis.modules.IO;

import java.util.HashSet;

import com.unsupervisedsentiment.analysis.model.Tuple;

public class OutputWrapper extends FileWrapper {
	private HashSet<Tuple> tuples;

	public OutputWrapper() {
		super();
	}

	public OutputWrapper(String author, String source, String content,
			String filename, HashSet<Tuple> tuples) {
		super(author, source, content, filename);
		this.tuples = tuples;
	}

	public HashSet<Tuple> getTuples() {
		return tuples;
	}

	public void setTuples(HashSet<Tuple> tuples) {
		this.tuples = tuples;
	}
}
