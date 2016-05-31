package services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.Pair;
import util.Helper;

import static java.awt.SystemColor.text;

public class ScoreAnnotatorService {

    private static final String SCORE_ANNOTATION = "@";
    private static final String OPINION_WORD_AND_ANNOTATION_REGEX = "\\#\\#\\#(.*)\\@";

    private SentiWordNetService sentiWordNetService;

    public ScoreAnnotatorService(SentiWordNetService sentiWordNetService) {
        this.sentiWordNetService = sentiWordNetService;
    }

    public String appendScoresToAnnotation(List<String> lines) {
        StringBuilder annotatedTextBuilder = new StringBuilder();
        Map<String, Pair<Double, Double>> knownModifiers = sentiWordNetService.getModifiers();
        for (String phrase : lines) {
            StringBuilder annotatedSentenceBuilder = new StringBuilder();
            String[] words = phrase.split(" ");
            for (String word : words) {
                if (word.contains(SCORE_ANNOTATION)) {
                    String appendedWord = "";
                    if (word.contains("-")) {
                        String[] splitted = word.split("-"); //we want to retain the @
                        word = splitted[0];
                        if (splitted.length > 0) {
                            appendedWord = splitted[1];
                        }
                    }
                    String cleanWord = Helper.extractByRegexOneGroup(word, OPINION_WORD_AND_ANNOTATION_REGEX);
                    String df = String.format(Locale.US, "%.2f", sentiWordNetService.extract(cleanWord, new String[]{SentiWordNetService.SWNPos.Adjective.toString(), SentiWordNetService.SWNPos.Adverb.toString()}));
                    double score = Double.parseDouble(df);
                    word = word + score + (!appendedWord.equals("") ? "-" + appendedWord : "");
                } else if (knownModifiers.containsKey(word)) {
                    word = "$$$" + word;
                }
                annotatedSentenceBuilder.append(word).append(" ");
            }
            annotatedTextBuilder.append(annotatedSentenceBuilder.toString()).append("\n");
        }
        return annotatedTextBuilder.toString();
    }

}
