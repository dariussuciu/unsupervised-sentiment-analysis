package com.unsupervisedsentiment.analysis.modules.IO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;

import edu.stanford.nlp.semgraph.SemanticGraph;

public class EvaluationOutputService {
	public static boolean existsEvaluationModelForFile(final String filename) {
		final String storedSemanticGraphsDirectory = Initializer.getConfig().getStoredSemanticGraphsDirectory();

		final String filePath = storedSemanticGraphsDirectory + "/" + filename + "-SemanticGraph";
		final File f = new File(filePath);
		return f.exists();
	}

	public static void saveEvaluationModelToFile(final List<SemanticGraph> semanticGraphsListForSentances,
			final String filename) {
		final String filePath = getEvaluationModelFilename(filename);
		try {
			// File f = new File(filename);
			// if (!f.exists())
			// f.createNewFile();
			final OutputStream file = new FileOutputStream(filePath);
			final OutputStream buffer = new BufferedOutputStream(file);
			final ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(semanticGraphsListForSentances);
			output.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public static List<EvaluationModel> getEvaluationModelFromFile(final String filename) {
		final String filePath = getEvaluationModelFilename(filename);
		try {
			final InputStream file = new FileInputStream(filePath);
			final InputStream buffer = new BufferedInputStream(file);
			final ObjectInput input = new ObjectInputStream(buffer);

			// deserialize the List
			@SuppressWarnings("unchecked")
			final List<EvaluationModel> evaluationModels = (List<EvaluationModel>) input.readObject();
			input.close();
			// display its data
			return evaluationModels;
		} catch (ClassNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	private static String getEvaluationModelFilename(final String filename) {
		final String storedSemanticGraphsDirectory = Initializer.getConfig().getStoredSemanticGraphsDirectory();
		final String filePath = storedSemanticGraphsDirectory + "/" + filename + "-SemanticGraph";
		return filePath;
	}
}
