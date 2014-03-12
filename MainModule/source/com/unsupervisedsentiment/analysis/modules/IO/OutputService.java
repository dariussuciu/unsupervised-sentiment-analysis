package com.unsupervisedsentiment.analysis.modules.IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;

public class OutputService {

	private Config config;
	private static OutputService outputService;

	public static OutputService getInstance(Config config) {
		if (outputService == null)
			outputService = new OutputService(config);
		return outputService;
	}

	private OutputService(Config config) {
		this.config = config;
	}

	public void writeOutput(List<OutputWrapper> outputWrappers) {
		try
		{
			File folder = new File(config.getOutputDirectory());
			// File folder = new File(
			// "C:\\Users\\Alex\\Desktop\\Research\\Project\\Input");
			for(OutputWrapper outputWrapper : outputWrappers)
			{
			    File file = new File(folder, outputWrapper.getFilename());
			    file.createNewFile();
	
			    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			    
			    writer.write("----------------------" );
			    writer.newLine();
			    writer.write("------HEADER------" );
			    writer.newLine();
			    writer.write("----------------------" );
			    writer.newLine();
			    writer.write("Author : " + outputWrapper.getAuthor());
			    writer.newLine();
			    writer.write("Source : " + outputWrapper.getSource());
			    writer.newLine();
			    writer.write("----------------------" );
			    writer.newLine();
			    writer.write("------CONTENT------" );
			    writer.newLine();
			    writer.write("----------------------" );
			    writer.newLine();
			    
			    WriteTuples(writer, outputWrapper.getTuples());
			    writer.flush();
			    writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void WriteTuples(BufferedWriter writer, Set<Tuple> tuples) throws IOException{
		for (Tuple tuple : tuples) 
		{
			if (tuple.getTupleType().equals(TupleType.Pair)) {
				Pair pair = (Pair) tuple;

					writer.write("Pair:  " + tuple.getSource().getValue()
							+ "[" + tuple.getSource().getScore() + "]" 
							+ "{" + tuple.getSource().getSentiWordScore() + "}" 
							+ "(" + tuple.getSource().getPosTag() + ")" + " --("
							+ pair.getRelation() + ")--> "
							+ tuple.getTarget().getValue() 
							+ "[" + tuple.getTarget().getScore() + "]" 
							+ "{" + tuple.getTarget().getSentiWordScore() + "}" 
							+ "(" + tuple.getTarget().getPosTag() + ")");
			    writer.newLine();
			} 
			else if (tuple.getTupleType().equals(TupleType.Triple)) 
			{
				Triple triple = (Triple) tuple;
				writer.write("Triple:  " + tuple.getSource().getValue()
						+ "[" + tuple.getSource().getScore() + "]" 
						+ "{" + tuple.getSource().getSentiWordScore() + "}" 
						+ "(" + tuple.getSource().getPosTag() + ")" + " --("
						+ triple.getRelationHOpinion() + ")--> "
						+ triple.getH().getValue() + "("
						+ triple.getH().getPosTag() + ")" + " --("
						+ triple.getRelationHTarget() + ")--> "
						+ tuple.getTarget().getValue() + "("
						+ "[" + tuple.getTarget().getScore() + "]" 
						+ "{" + tuple.getTarget().getSentiWordScore() + "}" 
						+ tuple.getTarget().getPosTag() + ")");
			    writer.newLine();
			}
		}
	}
	
	public void WriteEvaluationModels(String fileName, List<EvaluationModel> evaluationModels)
	{
		try
		{
			File folder = new File(config.getEvaluationModelsDirectory());
			
		    File file = new File(folder, fileName);
		    file.createNewFile();

		    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			for(EvaluationModel evaluationModel : evaluationModels)
			{
				writer.write(evaluationModel.getOpinionWord()+"[" + evaluationModel.getSentenceIndex() + "]" + " - " + evaluationModel.getSentence());
			    writer.newLine();
			}
			
		    writer.flush();
		    writer.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}