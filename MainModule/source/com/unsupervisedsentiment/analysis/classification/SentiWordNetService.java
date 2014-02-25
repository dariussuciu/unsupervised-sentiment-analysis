package com.unsupervisedsentiment.analysis.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;

public class SentiWordNetService implements ISentimentScoreSource{

	private HashMap<String, Double> _dict;
	Config config;

	public SentiWordNetService() {
		init();
	}
	
	public void init() {
		config = Initializer.getConfig();
		String pathToSWN = config.getSWNPath();

		_dict = new HashMap<String, Double>();
		try {
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");
				for (String w : words) {
					String[] w_n = w.split("#");
					w_n[0] += "#" + data[0];
					if (_dict.containsKey(w_n[0])) {
						_dict.put(w_n[0], score);
					} else {
						_dict.put(w_n[0], score);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Double extract(String word) {
		Double total = new Double(0);
		if (_dict.get(word + "#n") != null)
			total = _dict.get(word + "#n") + total;
		if (_dict.get(word + "#a") != null)
			total = _dict.get(word + "#a") + total;
		if (_dict.get(word + "#r") != null)
			total = _dict.get(word + "#r") + total;
		if (_dict.get(word + "#v") != null)
			total = _dict.get(word + "#v") + total;
		return total;

	}

	public HashMap<String, Double> getSeedWordsWithScores(){
		HashMap<String, Double> hash = new HashMap<String, Double>();
		ArrayList<String> seeds = config.getSeedWords();
		for (String seedName : seeds ){
			double score = extract (seedName);
			hash.put(seedName, score);
		}
		return hash;
	}
	
}
