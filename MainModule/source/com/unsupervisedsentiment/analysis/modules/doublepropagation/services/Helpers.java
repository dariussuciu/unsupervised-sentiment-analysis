package com.unsupervisedsentiment.analysis.modules.doublepropagation.services;

import java.util.ArrayList;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.test.constants.relations.GenericRelation;

import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class Helpers {
	
	/**
	 * Gets the target edges based on the relation pos tag and the target pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdges(Iterable<SemanticGraphEdge> edges, GenericRelation targetType,
			GenericRelation relationType, boolean isSource) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			if (relationType.contains(relation.toString())) {
				if (!isSource && targetType.contains(edge.getTarget().tag())) {
					targetEdges.add(edge);
				}
				if (isSource && targetType.contains(edge.getSource().tag())) {
					targetEdges.add(edge);
				}
			}
		}
		return targetEdges;
	}
	/**
	 * Gets the target edges based only on the relation pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdges(Iterable<SemanticGraphEdge> edges, GenericRelation relationType) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			if (relationType.contains(relation.toString())) {
				targetEdges.add(edge);
			}
		}
		return targetEdges;
	}

	public static Pair getPair(String valueOpinion, String posOpinion, String valueTarget, String posTarget,
			String relation, Dependency dependency) {
		Word opinion = new Word(posOpinion, valueOpinion);
		Word target = new Word(posTarget, valueTarget);

		return new Pair(opinion, target, dependency, TupleType.Pair, relation);
	}

	public static Triple getTriple(String valueOpinion, String posOpinion, String valueTarget, String posTarget,
			String valueH, String posH, String relationHOpinion, String relationHTarget, Dependency dependency) {
		Word opinion = new Word(posOpinion, valueOpinion);
		Word target = new Word(posTarget, valueTarget);
		Word H = new Word(posH, valueH);

		return new Triple(opinion, target, H, dependency, TupleType.Triple, relationHOpinion, relationHTarget);
	}
}
