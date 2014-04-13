package com.unsupervisedsentiment.analysis.experiment.seed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Filters seed words.
 * 
 * @author Maria
 * 
 */
public class SeedWordsFilter {

	/**
	 * Returns the most frequent <code>maxCount</code> elements from <code>items</code>, in the specified files.
	 * 
	 * @param maxCount
	 *            max size of result
	 * @param items
	 *            words to filter
	 * @param textFiles
	 * @return
	 */
	public static List<String> mostFrequent(final int maxCount, final List<String> items, final List<File> textFiles) {
		final List<String> result = new ArrayList<String>();

		// map (key, value) : (seed word, SeedFrequency)
		final Map<String, SeedFrequency> seedMap = new HashMap<String, SeedFrequency>();

		// count seeds occurrence in all files
		try {
			for (File file : textFiles) {
				String content = FileUtils.readFileToString(file).toLowerCase();
				for (String seed : items) {
					int count = StringUtils.countMatches(content, seed);
					if (seedMap.containsKey(seed) && count > 0) {
						count += seedMap.get(seed).getCount();
						seedMap.get(seed).setCount(count);
					} else {
						seedMap.put(seed, new SeedFrequency(seed, count));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}

		// sort by occurrence
		List<SeedFrequency> seedList = new ArrayList<SeedFrequency>(seedMap.values());
		Collections.sort(seedList);

		// return maxCount words
		for (SeedFrequency seed : seedList.subList(0, Math.min(maxCount, seedList.size()))) {
			result.add(seed.getWord());
		}

		return result;
	}
}
