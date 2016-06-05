package com.unsupervisedsentiment.analysis.modules.evaluation;

import java.util.List;
import java.util.Set;

import com.unsupervisedsentiment.analysis.classification.Classification;
import com.unsupervisedsentiment.analysis.core.Config;
import com.unsupervisedsentiment.analysis.core.Initializer;
import com.unsupervisedsentiment.analysis.model.ElementType;
import com.unsupervisedsentiment.analysis.model.EvaluationModel;
import com.unsupervisedsentiment.analysis.model.Tuple;
import com.unsupervisedsentiment.analysis.model.TupleType;
import com.unsupervisedsentiment.analysis.model.Word;
import com.unsupervisedsentiment.analysis.modules.standfordparser.NLPService;

public class ScoreEvaluationService extends EvaluationService {
    private double ACCEPTABLE_ERROR = 0.3;

    public ScoreEvaluationService(List<EvaluationModel> evaluationModels,
                                  Set<Tuple> tuples, double acceptableError) {
        super(evaluationModels, tuples);
        ACCEPTABLE_ERROR = acceptableError;
    }

    @Override
    protected void evaluate() {

        truePositive = 0;
        falsePositive = 0;
        falseNegative = 0;

        for (final Tuple tuple : tuples) {
            for (final EvaluationModel model : evaluationModels) {
                if (model.getSentenceIndex() == tuple.getSentenceIndex() && tuple.getSource().getValue().equals(model.getElement())) {
                    double assignedScore = tuple.getSource().getScore();
                    double annotatedScore = model.getOpinionWordScore();
                    falsePositive++;
                    if (Math.abs(assignedScore - annotatedScore) <= Math.abs(getDynamicThresholdBasedOnScore(assignedScore))) {
                        if (!(annotatedScore > 0 && assignedScore < 0) || (annotatedScore < 0 && assignedScore > 0)) {
                            truePositive++;
                            falsePositive--;

                            if (tuple.getSource() != null) {
                                Word initialWord = getInitialWordBySentenceIdx(tuple.getSentenceIndex(), tuple.getSource().getValue());
                                if (initialWord != null) {
                                    truePositive++;
                                }
                            }
                            if (tuple.getTarget() != null) {
                                Word initialWord = getInitialWordBySentenceIdx(tuple.getSentenceIndex(), tuple.getTarget().getInitialValue());
                                if (initialWord != null) {
                                    truePositive++;
                                }
                            }
                        }
                        break;
                    } else {
                        System.out.println("wrong value opinion word/score // annotated score:" + tuple.getSource().getValue() + "/" + assignedScore + "//" + annotatedScore + " sentence: " + model.getSentence());
                    }
                }
            }
        }

        for (final EvaluationModel model : evaluationModels) {
            for (final Tuple tuple : tuples) {
                if (model.getSentenceIndex() == tuple.getSentenceIndex() && model.getSentenceIndex() == tuple.getSentenceIndex()) {
                    double assignedScore = tuple.getSource().getScore();
                    double annotatedScore = model.getOpinionWordScore();
                    falseNegative++;
                    if (Math.abs(assignedScore - annotatedScore) < Math.abs(getDynamicThresholdBasedOnScore(assignedScore))) {
                        falseNegative--;
                        break;
                    } else {
                        boolean changedPOS = false;
                        if (tuple.getSource() != null) {
                            Word initialWord = getInitialWordBySentenceIdx(tuple.getSentenceIndex(), tuple.getSource().getValue());
                            if (initialWord != null) {
                                changedPOS = true;
                                falseNegative--;
                            }
                        }
                        if (tuple.getTarget() != null) {
                            Word initialWord = getInitialWordBySentenceIdx(tuple.getSentenceIndex(), tuple.getTarget().getInitialValue());
                            if (initialWord != null) {
                                changedPOS = true;
                                falseNegative--;
                            }
                        }
                        if (!changedPOS) {
                            System.out.println("RECALL wrong value opinion word/score // annotated score:" + tuple.getSource().getValue() + "/" + assignedScore + "//" + annotatedScore + " sentence: " + model.getSentence());
                        }
                    }
                }
            }
        }
    }

    private double getDynamicThresholdBasedOnScore(double score) {
        double threshold = 0.1;
        int percentage = 40;

        if (score > 0) {
            threshold = (percentage / 100.0) * (1.0 - score);
        }
        if (score < 0) {
            threshold = (percentage / 100.0) * (-1.0 - score);
        }

        return threshold;
    }

    private Word getInitialWordBySentenceIdx(int index, String tupleValue) {
        List<Word> initialWords = NLPService.getInstance().getInitialWords();
        for (Word initialWord : initialWords) {
            if (initialWord.getSentenceIndex() == index && initialWord.getValue().equals(tupleValue) && initialWord.getInitialValue().contains(tupleValue)) {
                return initialWord;
            }
        }
        return null;
    }

    public static void performEvaluation(
            List<EvaluationModel> evaluationModels, Set<Tuple> tuples) {
        Config config = Initializer.getConfig();
        double acceptableError = Double.parseDouble(config
                .getScoringThreshold());
        ScoreEvaluationService scoreEvaluationService = new ScoreEvaluationService(
                evaluationModels, tuples, acceptableError);
        if (config.getPrintEvaluationResultsToConsole()) {
            EvaluationResult scoreEvaluationResult = scoreEvaluationService
                    .getResults();
            System.out.println("Score Precision : "
                    + scoreEvaluationResult.getPrecision());
            System.out.println("Score Recall : "
                    + scoreEvaluationResult.getRecall());
            System.out.println("-----------------------------------------");
        }
    }
}
