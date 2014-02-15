package com.unsupervisedsentiment.analysis.modules.doublepropagation.services;

import java.util.ArrayList;
import java.util.List;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.test.constants.RelationEquivalency;
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
	public static List<SemanticGraphEdge> getTargetEdgesOnRelAndTarget(Iterable<SemanticGraphEdge> edges, GenericRelation targetType,
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
	public static List<SemanticGraphEdge> getTargetEdgesOnRel(Iterable<SemanticGraphEdge> edges, GenericRelation relationType) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			GrammaticalRelation relation = edge.getRelation();
			if (relationType.contains(relation.toString())) {
				targetEdges.add(edge);
			}
		}
		return targetEdges;
	}
	
	/**
	 * Gets the target edges based only on the target pos tag
	 * @param edges
	 * @param targetType
	 * @param relationType
	 * @param isSource
	 * @return
	 */
	public static List<SemanticGraphEdge> getTargetEdgesOnTarget(Iterable<SemanticGraphEdge> edges,
			GenericRelation targetType, boolean isSource) {
		List<SemanticGraphEdge> targetEdges = new ArrayList<SemanticGraphEdge>();

		for (SemanticGraphEdge edge : edges) {
			if (!isSource && targetType.contains(edge.getTarget().tag())) {
				targetEdges.add(edge);
			}
			if (isSource && targetType.contains(edge.getSource().tag())) {
				targetEdges.add(edge);
			}
				targetEdges.add(edge);
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
	
	public static boolean checkEquivalentRelations(GrammaticalRelation relationSourceH, GrammaticalRelation relationTargetH)
	{
		//equivalency from Stanford, keep for know, until we know more about how it decides when relations
		// are equivalent
		if(relationSourceH.equals(relationTargetH))
			return true;
		
		String relSourceHName = relationSourceH.toString();
		String relTargetHName = relationTargetH.toString();
		
		return compareRelations(relSourceHName, relTargetHName);
	}
	
	private static boolean compareRelations(String rel1, String rel2)
	{	
		if(rel1.equals(rel2))
			return true;
		
		RelationEquivalency relEquivalency1 = getRelationEquivalency(rel1);
		RelationEquivalency relEquivalency2 = getRelationEquivalency(rel2);
	
		if(relEquivalency1.equals(relEquivalency2))
			return true;
		
		return false;
	}
	
	private static RelationEquivalency getRelationEquivalency(String relation)
	{
		if(relation.equals("s") || relation.equals("subj") || relation.equals("obj"))
			return RelationEquivalency.Rule32;
		
		if(relation.equals("mod") || relation.equals("pmod"))
			return RelationEquivalency.Rule42;
		
		return RelationEquivalency.None;
	}
}
