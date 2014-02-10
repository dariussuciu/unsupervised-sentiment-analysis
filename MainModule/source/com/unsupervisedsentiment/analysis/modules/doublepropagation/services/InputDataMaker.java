package com.unsupervisedsentiment.analysis.modules.doublepropagation.services;

import java.util.HashSet;
import java.util.Set;

import com.unsupervisedsentiment.analysis.model.Dependency;
import com.unsupervisedsentiment.analysis.model.Tuple;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class InputDataMaker {
	
	public HashSet<Tuple> OpinionWords;
	public HashSet<Tuple> Targets;
	
	public InputDataMaker(){
		OpinionWords = new HashSet<Tuple> ();
		Targets = new HashSet<Tuple> ();
	}
	
	public InputDataMaker(SemanticGraph graph){
		OpinionWords = new HashSet<Tuple> ();
		Targets = new HashSet<Tuple> ();
		Convert(graph);
	}
	
	public void Reset() {
		OpinionWords = new HashSet<Tuple> ();
		Targets = new HashSet<Tuple> ();
	}
	
	public void Convert(SemanticGraph graph)
	{
		Set<SemanticGraphEdge> edgeSet = graph.getEdgeSet();

		for (SemanticGraphEdge edge : edgeSet) {
			String sourcePartOfSpeech = edge.getSource().get(PartOfSpeechAnnotation.class);
			String destinationPartOfSpeech = edge.getTarget().get(PartOfSpeechAnnotation.class);

			ProcessEdge(edge,sourcePartOfSpeech,false);
			ProcessEdge(edge,destinationPartOfSpeech,true);
		}
	}
	
	private void ProcessEdge(SemanticGraphEdge edge, String partOfSpeech, boolean reverse)
	{
		if (partOfSpeech.equals("NN"))
		{
			Targets.add(GetTupleFromEdge(edge,reverse));
		}
		else if (partOfSpeech.equals("NNS"))
		{
			Targets.add(GetTupleFromEdge(edge,reverse));
		}
		else if (partOfSpeech.equals("JJ"))
		{
			OpinionWords.add(GetTupleFromEdge(edge,reverse));
		}
		else if (partOfSpeech.equals("JJS"))
		{
			OpinionWords.add(GetTupleFromEdge(edge,reverse));
		}
		else if (partOfSpeech.equals("JJR"))
		{
			OpinionWords.add(GetTupleFromEdge(edge,reverse));
		}
//		switch(partOfSpeech)
//		{
//			case "NN":
//				Targets.add(GetTupleFromEdge(edge,reverse));
//				break;
//			case "NNS":
//				Targets.add(GetTupleFromEdge(edge,reverse));
//			case "JJ":
//				OpinionWords.add(GetTupleFromEdge(edge,reverse));
//			case "JJS":
//				OpinionWords.add(GetTupleFromEdge(edge,reverse));
//			case "JJR":
//				OpinionWords.add(GetTupleFromEdge(edge,reverse));
//			default:
//				break;
//		}
	}
	
	private Tuple GetTupleFromEdge(SemanticGraphEdge edge, boolean reverse) {
		Tuple tuple = new Tuple();
		tuple.setDependency(Dependency.DIRECT_DEPENDENCY);
		if(!reverse)
		{
			tuple.setWord_x(edge.getSource().get(TextAnnotation.class));
			tuple.setPosTag_x(edge.getSource().get(PartOfSpeechAnnotation.class));
			tuple.setWord_y(edge.getTarget().get(TextAnnotation.class));
			tuple.setPosTag_y(edge.getTarget().get(PartOfSpeechAnnotation.class));
		}
		else 
		{
			tuple.setWord_y(edge.getSource().get(TextAnnotation.class));
			tuple.setPosTag_y(edge.getSource().get(PartOfSpeechAnnotation.class));
			tuple.setWord_x(edge.getTarget().get(TextAnnotation.class));
			tuple.setPosTag_x(edge.getTarget().get(PartOfSpeechAnnotation.class));
		}

		tuple.setRelation(edge.getRelation().toString());
		return tuple;
	}	
}
