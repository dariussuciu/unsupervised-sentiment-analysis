package com.unsupervisedsentiment.analysis.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.core.constants.relations.Pos_JJRel.JJ;
import com.unsupervisedsentiment.analysis.model.SeedScoreModel;
import com.unsupervisedsentiment.analysis.modules.IO.InputService;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class SentiWordNetService implements IPolarityLexion {

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

	private static IPolarityLexion instance;

	public static IPolarityLexion getInstance() {
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
		BufferedReader csv = null;
		try {

			HashMap<String, HashMap<Integer, Double>> allScoresHash = new HashMap<String, HashMap<Integer, Double>>();
			
			config = Initializer.getConfig();
			final String pathToSWN = config.getSWNPath();

			csv = new BufferedReader(new FileReader(pathToSWN));

			String line;
			while ((line = csv.readLine()) != null) {

				if (!line.trim().startsWith("#")) { 
					
					String[] data = line.split("\t"); 
					String posTag = data[0];

					String[] synsetList = data[4].split(" ");
					
					Double score = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
					
					for (String rankedWord : synsetList) {
						
						String word = rankedWord.split("#")[0];
						int rank = Integer.parseInt(rankedWord.split("#")[1]);
						
						String key = word + "#"	+ posTag;

						if (!allScoresHash.containsKey(key)) {
							allScoresHash.put(key, new HashMap<Integer, Double>());
						}

						allScoresHash.get(key).put(rank, score);
					}
				}
			}

			_dict = new HashMap<String, Double>();
			for (Map.Entry<String, HashMap<Integer, Double>> entry : allScoresHash
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap
						.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score = score / sum;

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
			total += _dict.get(word + pos[0]) + total;
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
	
	public List<String> getSeedWordsFromSemanticGraph(List<SemanticGraph> graphs){
		List<String> adjectives = new ArrayList<String>();
		for(SemanticGraph sentence : graphs){
			List<String> wordsInSentence = new ArrayList<String>();
			
			Collection<IndexedWord> rootNodes = sentence.getRoots();
		    for (IndexedWord root : rootNodes) {
		    	wordsInSentence.add(root.toString());
		    }
		    
		    ArrayList<IndexedWord> nodesList = new ArrayList<IndexedWord>(sentence.vertexSet());
		    for(IndexedWord word : nodesList){
		      wordsInSentence.add(word.toString());
		    }

            String previousWord = "";
		    for(int i = 0; i < wordsInSentence.size(); i++){
                String word = wordsInSentence.get(i);
                String unalteredWord = word;
                for (Classification.Modifier modifier : Classification.Modifier.values()) {
                    if (previousWord != null && previousWord.equals(modifier.toString() + "-RB")) {
                        word = modifier.toString() + " " + word;
                    }
                }
                previousWord = unalteredWord;
		    	if(word.contains("-" + JJ.JJ.toString())){
		    		adjectives.add(word.replace("-" + JJ.JJ.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", ""));
		    	}
		    	if(word.contains("-" + JJ.JJS.toString())){
		    		adjectives.add(word.replace("-" + JJ.JJS.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", ""));
		    	}
		    	if(word.contains("-" + JJ.JJR.toString())){
		    		adjectives.add(word.replace("-" + JJ.JJR.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", ""));
		    	}
                if(word.contains("-" + JJ.RB.toString())){
                    adjectives.add(word.replace("-" + JJ.RB.toString(), "").replaceAll("\\@.?\\d\\.?\\d*\\b", ""));
                }
		    }

        }
		return adjectives;
	}

}
