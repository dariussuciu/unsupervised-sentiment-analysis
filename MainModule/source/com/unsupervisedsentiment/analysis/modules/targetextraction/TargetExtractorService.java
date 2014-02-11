package com.unsupervisedsentiment.analysis.modules.targetextraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.test.constants.relations.Dep_MRRel;
import com.unsupervisedsentiment.analysis.test.constants.relations.Pos_NNRel;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class TargetExtractorService implements ITargetExtractorService {

	@Override
	public Set<Tuple> extractTargetUsingR1(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		Set<Tuple> foundTargets = new HashSet<Tuple>();
		
		foundTargets.addAll(extractTargetUsingR11(semanticGraph, opinionWords));
		foundTargets.addAll(extractTargetUsingR12(semanticGraph, opinionWords));
		return foundTargets;
	}

	@Override
	public Set<Tuple> extractTargetUsingR3(SemanticGraph semanticGraph, Set<Word> targets) {
		
		Set<Tuple> foundTargets = new HashSet<Tuple>();
		
		foundTargets.addAll(extractTargetUsingR31(semanticGraph, targets));
		foundTargets.addAll(extractTargetUsingR32(semanticGraph, targets));
		return foundTargets;
	}

	public Set<Tuple> extractTargetUsingR11(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		
		Set<Tuple> targets = new HashSet<Tuple>();
		
		for(Word opinionWord : opinionWords)
		{
			final List<IndexedWord> vertexes = semanticGraph.getAllNodesByWordPattern(opinionWord.getValue());
			for(IndexedWord vertex : vertexes)
			{
				//for outgoing edges
				for (SemanticGraphEdge edge : semanticGraph.outgoingEdgeIterable(vertex))
				{
					GrammaticalRelation relation = edge.getRelation();
					if(Dep_MRRel.getInstance().contains(relation.toString()))
					{
						if(Pos_NNRel.getInstance().contains(edge.getTarget().tag()))
						{
							targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edge.getTarget().word(), edge.getTarget().tag(), relation.toString(), Dependency.DIRECT_DEPENDENCY));
						}
					}
				}
				//for incoming edges
				for (SemanticGraphEdge edge : semanticGraph.incomingEdgeIterable(vertex))
				{
					GrammaticalRelation relation = edge.getRelation();
					if(Dep_MRRel.getInstance().contains(relation.toString()))
					{
						if(Pos_NNRel.getInstance().contains(edge.getSource().tag()))
						{
							targets.add(getTuple(opinionWord.getValue(), opinionWord.getPosTag(), edge.getSource().word(), edge.getSource().tag(), relation.toString(), Dependency.DIRECT_DEPENDENCY));
						}
					}
				}
			}
		}
		
		return targets;
	}

	public Set<Tuple> extractTargetUsingR12(SemanticGraph semanticGraph, Set<Word> opinionWords) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR31(SemanticGraph semanticGraph, Set<Word> targets) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Tuple> extractTargetUsingR32(SemanticGraph semanticGraph, Set<Word> targets) {
		// TODO Auto-generated method stub
		return null;
	}
	private Tuple getTuple(String valueOpinion, String posOpinion, String valueTarget, String posTarget, String relation, Dependency dependency) {
		Word opinion = new Word(posOpinion,valueOpinion);
		Word target = new Word(posTarget, valueTarget);
		
		return new Tuple(opinion, target, dependency, relation);
	}
}
