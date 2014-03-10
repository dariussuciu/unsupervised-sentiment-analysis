package com.unsupervisedsentiment.analysis.modules.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.unsupervisedsentiment.analysis.core.Config;

public class InputService {

	private Config config;
	private static InputService inputService;

	public static InputService getInstance(Config config) {
		if (inputService == null)
			inputService = new InputService(config);
		return inputService;
	}

	private InputService(Config config) {
		this.config = config;
	}
	
	public List<String> getSeedWordsFromFile() {
		List<String> seedWords = new ArrayList<String>();
		
		try 
		{
			File file = new File(config.getPositiveSeedWordsFile());
			Scanner positiveScanner = new Scanner(new FileReader(file.getPath()));
			List<String> positiveSeedWords = getSeedWords(positiveScanner);
			
			file = new File(config.getNegativeSeedWordsFile());
			Scanner negativeScanner = new Scanner(new FileReader(file.getPath()));
			List<String> negativeSeedWords = getSeedWords(negativeScanner);
			
			seedWords.addAll(positiveSeedWords);
			seedWords.addAll(negativeSeedWords);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return seedWords;
	}

	public List<InputWrapper> getTextFromFile() {
		File folder = new File(config.getInputDirectory());
		// File folder = new File(
		// "C:\\Users\\Alex\\Desktop\\Research\\Project\\Input");

		File[] listOfFiles = folder.listFiles();
		List<InputWrapper> input = new ArrayList<InputWrapper>();
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
			getHeaders(in, iWrapper);
			getContent(in, iWrapper);
			iWrapper.setFilename(file.getName());
			return iWrapper;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void getContent(Scanner in, InputWrapper iWrapper) {
		StringBuilder content = new StringBuilder("");
		while (in.hasNextLine()) {
			content.append(in.nextLine());
			content.append(" ");
		}
		iWrapper.setContent(content.toString());
	}
	
	private List<String> getSeedWords(Scanner in) {
		List<String> seedWords = new ArrayList<String>();
		while (in.hasNextLine()) {
			seedWords.add(in.nextLine());
		}
		return seedWords;
	}

	private void getHeaders(Scanner in, InputWrapper iWrapper) {
		if (in.hasNextLine()) {
			String author = in.nextLine();
			iWrapper.setAuthor(getFormattedHeaderInfo(author));
		}
		if (in.hasNextLine()) {
			String source = in.nextLine();
			iWrapper.setSource(getFormattedHeaderInfo(source));
		}
	}

	private String getFormattedHeaderInfo(String header) {
		String[] splittedHeader = header.split("//");
		if (splittedHeader.length > 1) {
			header = header.split("//")[1];
			if (header.charAt(0) == ' ')
				header = header.substring(1, header.length());
			return header;
		}
		return null;
	}
}
