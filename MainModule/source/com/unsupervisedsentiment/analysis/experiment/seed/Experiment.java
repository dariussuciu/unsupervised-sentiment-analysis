package com.unsupervisedsentiment.analysis.experiment.seed;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;

/**
 * Some seed experiments.
 * 
 * @author Maria
 *
 */
public class Experiment {

	public static void main(final String[] args) throws FileNotFoundException {
		final Config config = Initializer.getConfig();
		final InputService inputService = InputService.getInstance(config);

		// read all positive seeds
		File file = new File(config.getPositiveSeedWordsFile());
		Scanner scanner = new Scanner(new FileReader(file.getPath()));
		List<String> positiveSeeds = inputService.getSeedWords(scanner);
		int nrPosSeeds = positiveSeeds.size();

		// read all negative seeds
		file = new File(config.getNegativeSeedWordsFile());
		scanner = new Scanner(new FileReader(file.getPath()));
		List<String> negativeSeeds = inputService.getSeedWords(scanner);
		int nrNegSeeds = negativeSeeds.size();

		// mumbo jumbo
		int nrOfSeeds = 4073;
		double ratio = ((double) nrPosSeeds / (double) nrNegSeeds);
		nrNegSeeds = (int) (nrOfSeeds / (1 + ratio));
		nrPosSeeds = nrOfSeeds - nrNegSeeds;

		// select most frequent positive and negative seeds
		final File folder = new File(config.getInputDirectory());
		FileFilter txtFileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".txt");
			}
		};
		final List<File> textFiles = Arrays.asList(folder.listFiles(txtFileFilter));

		negativeSeeds = SeedWordsFilter.mostFrequent(nrNegSeeds, negativeSeeds, textFiles);
		positiveSeeds = SeedWordsFilter.mostFrequent(nrPosSeeds, positiveSeeds, textFiles);

		if (CollectionUtils.isNotEmpty(negativeSeeds) && CollectionUtils.isNotEmpty(positiveSeeds)) {
			final String destination = "D:\\JDE\\Projects\\unsupervised-sentiment-analysis-io\\Experiments\\SeedWords";
			writeFileToDestination(destination, "positive-words.txt", positiveSeeds);
			writeFileToDestination(destination, "negative-words.txt", negativeSeeds);
		}
	}

	private static void writeFileToDestination(final String destination, final String fileName, final List<String> words) {
		PrintWriter printWriter = null;
		try {
			final File destinationFile = new File(destination);
			final File outFile = new File(destinationFile, fileName);
			printWriter = new PrintWriter(new FileWriterWithEncoding(outFile, Charset.forName("UTF-8")));
			for (String word : words) {
				printWriter.write(word + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
		}
	}
}
