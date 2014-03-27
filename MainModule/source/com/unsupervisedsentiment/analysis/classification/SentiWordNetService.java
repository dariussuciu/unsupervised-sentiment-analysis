package com.unsupervisedsentiment.analysis.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;

public class SentiWordNetService implements ISentimentScoreSource {

	public enum SWNPos {
		Adjective("#a"), Verb("#v"), Noun("#n"), Adverb("#r");

		private String value;

		private SWNPos(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}
	}

	private HashMap<String, Double> _dict;
	private Config config;

	private static ISentimentScoreSource instance;

	public static ISentimentScoreSource getInstance() {
		if (instance == null)
			instance = new SentiWordNetService();

		return instance;
	}

	private SentiWordNetService() {
		init();
	}

	public void init() {
		config = Initializer.getConfig();
		final String pathToSWN = config.getSWNPath();

		_dict = new HashMap<String, Double>();
		try {
			final BufferedReader csv = new BufferedReader(new FileReader(
					pathToSWN));
			String line = "";

			// headers...
			csv.readLine();
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2]) >= Double
						.parseDouble(data[3]) ? Double.parseDouble(data[2])
						: Double.parseDouble(data[3]);
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
			csv.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Possible pos parameters #n, #a, #r, #v
	 * 
	 * @param word
	 * @param pos
	 * @return
	 */
	public Double extract(final String word, final String[] pos) {
		Double total = new Double(0);
		for (int i = 0; i < pos.length; i++) {
			if (_dict.get(word + pos[i]) != null)
				total = _dict.get(word + pos[i]) + total;
		}
		return total;

	}

	public ArrayList<SeedScoreModel> getSeedWordsWithScores() {
		final InputService inputService = InputService.getInstance(config);
		config.setSeedWords(inputService.getSeedWordsFromFile());
		ArrayList<SeedScoreModel> hash = new ArrayList<SeedScoreModel>();
		ArrayList<String> seedsTemp = (ArrayList<String>) config.getSeedWords();
		ArrayList<String> seeds = new ArrayList<String>();

		for (String seed : seedsTemp) {
			seeds.add(seed.trim());
		}

		for (String seedName : seeds) {
			double score = extract(seedName, new String[]{SWNPos.Adjective.toString()});
			hash.add(new SeedScoreModel(seedName, score));
		}
		return hash;
	}

}
