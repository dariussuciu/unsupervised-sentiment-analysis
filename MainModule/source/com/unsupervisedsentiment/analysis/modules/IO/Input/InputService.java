package com.unsupervisedsentiment.analysis.modules.IO.Input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.unsupervisedsentiment.analysis.modules.IO.Input.Models.Config;

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

	public List<InputWrapper> getTextFromFile() {
		 File folder = new File(config.getInputDirectory());
//		File folder = new File(
//				"C:\\Users\\Alex\\Desktop\\Research\\Project\\Input");

		File[] listOfFiles = folder.listFiles();
		List<InputWrapper> input = new ArrayList<InputWrapper>();
		if(listOfFiles != null)
		{
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
		header = header.split("//")[1];
		if (header.charAt(0) == ' ')
			header = header.substring(1, header.length());
		return header;
	}
}
