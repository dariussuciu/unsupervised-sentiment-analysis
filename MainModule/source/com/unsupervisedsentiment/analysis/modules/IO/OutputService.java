package com.unsupervisedsentiment.analysis.modules.IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.constants.relations.GeneralPosRelationContainer;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Pair;
import com.unsupervisedsentiment.analysis.model.Triple;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.modules.evaluation.EvaluationMetadata;

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

	public OutputWrapper createOutputWrapperFromInput(InputWrapper input, LinkedHashSet<Tuple> resultTuples) {
		OutputWrapper outputFile = new OutputWrapper();
		outputFile.setAuthor(input.getAuthor());
		outputFile.setFilename(input.getFilename());
		outputFile.setSource(input.getSource());
		outputFile.setTuples(resultTuples);

		return outputFile;
	}

	public void writeOutput(final List<OutputWrapper> outputWrappers) {
		try {
			final File folder = new File(config.getOutputDirectory());
			for (final OutputWrapper outputWrapper : outputWrappers) {
				final File file = new File(folder, outputWrapper.getFilename());
				file.createNewFile();

				BufferedWriter writer = new BufferedWriter(new FileWriter(file));

				writer.write("----------------------");
				writer.newLine();
				writer.write("------HEADER------");
				writer.newLine();
				writer.write("----------------------");
				writer.newLine();
				writer.write("Author : " + outputWrapper.getAuthor());
				writer.newLine();
				writer.write("Source : " + outputWrapper.getSource());
				writer.newLine();
				writer.write("----------------------");
				writer.newLine();
				writer.write("------CONTENT------");
				writer.newLine();
				writer.write("----------------------");
				writer.newLine();

				writeTuples(writer, outputWrapper.getTuples());
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeTuples(final BufferedWriter writer, final Set<Tuple> tuples) throws IOException {
		for (final Tuple tuple : tuples) {
			if (tuple.getTupleType().equals(TupleType.Pair)) {
				final Pair pair = (Pair) tuple;

				writer.write("Pair:  " + tuple.getSource().getValue() + "[" + tuple.getSource().getScore() + "]" + "{"
						+ tuple.getSource().getSentiWordScore() + "}" + "(" + tuple.getSource().getPosTag() + ")"
						+ " --(" + pair.getRelation() + ")--> " + tuple.getTarget().getValue() + "["
						+ tuple.getTarget().getScore() + "]" + "{" + tuple.getTarget().getSentiWordScore() + "}" + "("
						+ tuple.getTarget().getPosTag() + ")" + "   - sentence: " + tuple.getSentence() + "("
						+ tuple.getSentenceIndex() + ")");
				writer.newLine();
			} else if (tuple.getTupleType().equals(TupleType.Triple)) {
				final Triple triple = (Triple) tuple;
				writer.write("Triple:  " + tuple.getSource().getValue() + "[" + tuple.getSource().getScore() + "]"
						+ "{" + tuple.getSource().getSentiWordScore() + "}" + "(" + tuple.getSource().getPosTag() + ")"
						+ " --(" + triple.getRelationHOpinion() + ")--> " + triple.getH().getValue() + "("
						+ triple.getH().getPosTag() + ")" + " --(" + triple.getRelationHTarget() + ")--> "
						+ tuple.getTarget().getValue() + "(" + "[" + tuple.getTarget().getScore() + "]" + "{"
						+ tuple.getTarget().getSentiWordScore() + "}" + "(" + tuple.getTarget().getPosTag() + ")"
						+ "   - sentence: " + tuple.getSentence() + "(" + tuple.getSentenceIndex() + ")");
				writer.newLine();
			}
		}
	}

	public void writeEvaluationModels(final String fileName, final List<EvaluationModel> evaluationModels) {
		try {
			final File folder = new File(config.getEvaluationModelsDirectory());

			final File file = new File(folder, fileName);
			file.createNewFile();

			final BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (EvaluationModel evaluationModel : evaluationModels) {
				writer.write(evaluationModel.getElement() + "[" + evaluationModel.getSentenceIndex() + "]" + " - "
						+ evaluationModel.getSentence());
				writer.newLine();
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToEvaluationMetadataCsv(List<EvaluationMetadata> metadataResults) {

		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(config.getEvaluationMetadataFile(), true));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (EvaluationMetadata metadataResult : metadataResults) {
			try {
				writer.writeNext(metadataResult.getCSVdata());

			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.writeNext(new String[0]);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeMapToDetailedReportsFile(String filename, List<LinkedHashMap<String, String>> detailedReportsMaps) {
		try {
			final File file = new File(config.getDetailedEvaluationMetadataFile());
			if (!file.exists()) {
				file.createNewFile();
			}

			final BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

			writer.append("Filename: " + filename + " Number of seeds: "
					+ detailedReportsMaps.get(0).get("Number of Seeds") + " Target Threshold: "
					+ detailedReportsMaps.get(0).get("Target Frequency Threshold") + " Polarity Threshold: "
					+ detailedReportsMaps.get(0).get("Polarity Threshold") + " All relations: "
					+ GeneralPosRelationContainer.getAllEnumElementsAsString() + " Custom Text " + "\n");

			for (HashMap<String, String> map : detailedReportsMaps) {
				System.out.println(map.toString());
				writer.append(map.toString() + "\n");
			}
			writer.newLine();

			writer.flush();
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}