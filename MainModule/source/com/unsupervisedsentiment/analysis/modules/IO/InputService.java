package com.unsupervisedsentiment.analysis.modules.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.constants.Constants;

public class InputService {

	private Config config;
	private static InputService inputService;

	public static InputService getInstance(final Config config) {
		if (inputService == null)
			inputService = new InputService(config);
		return inputService;
	}

	private InputService(final Config config) {
		this.config = config;
	}

	public List<String> getSeedWordsFromFile() {
		final List<String> seedWords = new ArrayList<String>();

		try {
			File file = new File(config.getPositiveSeedWordsFile());
			Scanner scanner = new Scanner(new FileReader(file.getPath()));
			List<String> positiveSeedWords = getSeedWords(scanner);

			file = new File(config.getNegativeSeedWordsFile());
			scanner = new Scanner(new FileReader(file.getPath()));
			final List<String> negativeSeedWords = getSeedWords(scanner);

			String numberOfSeeds = config.getNumberOfSeeds();

			// add all seeds

			if (numberOfSeeds.equals(Constants.MAX)
					|| !numberOfSeeds.matches("\\d+")) {
				seedWords.addAll(positiveSeedWords);
				seedWords.addAll(negativeSeedWords);
			} else {
				int nrOfSeeds = Integer.valueOf(numberOfSeeds);

				seedWords.addAll(getSeedWords(positiveSeedWords,
						negativeSeedWords, nrOfSeeds));

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return seedWords;
	}

	private Collection<? extends String> getSeedWords(
			List<String> positiveSeedWords, List<String> negativeSeedWords,
			int nrOfSeeds) {
		List<String> seeds = new ArrayList<String>();

		// we try to keep the ratio between the positive an negative seed words
		// as if they were all added
		int nrPosSeedWords = positiveSeedWords.size();
		int nrNegSeedWords = negativeSeedWords.size();
		if (nrOfSeeds >= (nrPosSeedWords + nrNegSeedWords)) {
			seeds.addAll(negativeSeedWords);
			seeds.addAll(positiveSeedWords);
			return seeds;
		}

		double ratio = ((double) nrPosSeedWords / (double) nrNegSeedWords);

		// do the math. it's good, trust me.
		nrNegSeedWords = (int) (nrOfSeeds / (1 + ratio));
		nrPosSeedWords = nrOfSeeds - nrNegSeedWords;

		Collections.shuffle(positiveSeedWords);
		Collections.shuffle(negativeSeedWords);

		for (int i = 0; i < nrNegSeedWords; i++) {
			seeds.add(negativeSeedWords.get(i));
		}
		for (int i = 0; i < nrPosSeedWords; i++) {
			seeds.add(positiveSeedWords.get(i));
		}

		return seeds;
	}

	public List<InputWrapper> getTextFromFile() {
		File folder = new File(config.getInputDirectory());
		// File folder = new File(
		// "C:\\Users\\Alex\\Desktop\\Research\\Project\\Input");

		final File[] listOfFiles = folder.listFiles();
		final List<InputWrapper> input = new ArrayList<InputWrapper>();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile()) {
					input.add(getInputFromFile(file));
				}
			}
		}
		return input;
	}

	private InputWrapper getInputFromFile(File file) {
		try {
			Scanner in = new Scanner(new FileReader(file.getPath()));
			InputWrapper iWrapper = new InputWrapper();
			//getHeaders(in, iWrapper);
			getContent(in, iWrapper);
			iWrapper.setFilename(file.getName());
			return iWrapper;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void getContent(final Scanner in, final InputWrapper iWrapper) {
		final StringBuilder content = new StringBuilder("");
		while (in.hasNextLine()) {
			content.append(in.nextLine());
			content.append(" ");
		}
		iWrapper.setContent(content.toString());
	}

	public List<String> getSeedWords(final Scanner in) {
		final List<String> seedWords = new ArrayList<String>();
		while (in.hasNextLine()) {
			seedWords.add(in.nextLine());
		}
		return seedWords;
	}

	private void getHeaders(final Scanner in, final InputWrapper iWrapper) {
		if (in.hasNextLine()) {
			final String author = in.nextLine();
			iWrapper.setAuthor(getFormattedHeaderInfo(author));
		}
		if (in.hasNextLine()) {
			final String source = in.nextLine();
			iWrapper.setSource(getFormattedHeaderInfo(source));
		}
	}

	private String getFormattedHeaderInfo(String header) {
		final String[] splittedHeader = header.split("//");
		if (splittedHeader.length > 1) {
			header = header.split("//")[1];
			if (header.charAt(0) == ' ')
				header = header.substring(1, header.length());
			return header;
		}
		return null;
	}
}
