package com.unsupervisedsentiment.analysis.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		init2();
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
	
	public void init2(){
		_dict = new HashMap<String, Double>();

		// From String to list of doubles.
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

		BufferedReader csv = null;
		try {
			config = Initializer.getConfig();
			final String pathToSWN = config.getSWNPath();

			csv = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;

			String line;
			while ((line = csv.readLine()) != null) {
				lineNumber++;

				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// We use tab separation
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];

					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, through exception.
					if (data.length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Double.parseDouble(data[2])
							- Double.parseDouble(data[3]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].split(" ");

					// Go through all terms of current synset.
					for (String synTermSplit : synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm,
									new HashMap<Integer, Double>());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

			// Go through all the terms.
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				// Calculate weighted average. Weigh the synsets according to
				// their rank.
				// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
				// Sum = 1/1 + 1/2 + 1/3 ...
				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap
						.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score /= sum;

				_dict.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Possible pos parameters #n, #a, #r, #v
	 * 
	 * @param word
	 * @param pos
	 * @return
	 */
	public Double extract2(final String word, final String[] pos) {
		Double total = new Double(0);
		for (int i = 0; i < pos.length; i++) {
			if (_dict.get(word + pos[i]) != null)
				total = _dict.get(word + pos[i]) + total;
		}
		return total;

	}
	
	public Double extract(String word, String[] pos) {
		Double total = new Double(0);
		if (_dict.get(word + pos[0]) != null)
			total = _dict.get(word + pos[0]) + total;
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
