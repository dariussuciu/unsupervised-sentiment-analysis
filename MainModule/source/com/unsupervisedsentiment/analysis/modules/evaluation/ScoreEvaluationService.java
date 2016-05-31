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
            if (tuple.getElements(ElementType.OPINION_WORD).size() <= 0)
                continue;

            List<Word> opinionWords = tuple
                    .getElements(ElementType.OPINION_WORD);
            for (final Word opinionWord : opinionWords) {
                for (final EvaluationModel model : evaluationModels) {
                    if (opinionWord.getValue().equals(model.getElement())) {
                        double assignedScore = opinionWord.getScore();
                        double annotatedScore = model.getOpinionWordScore();
                        falsePositive++;
                        if (Math.abs(assignedScore - annotatedScore) <= ACCEPTABLE_ERROR) {
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
                            break;
                        } else {
                            System.out.println("wrong value opinion word/score // annotated score:" + opinionWord.getValue() + "/" + assignedScore + "//" + annotatedScore + " sentence: " + model.getSentence());
                        }
                    }
                }
            }
        }

        System.out.println("truePositive:" + truePositive);
        System.out.println("falsePositive:" + falsePositive);

        for (final EvaluationModel model : evaluationModels) {
            boolean found = false;
            for (final Tuple tuple : tuples) {
                if (tuple.getTupleType().equals(TupleType.Seed)
                        || tuple.getElements(ElementType.OPINION_WORD).size() <= 0)
                    continue;
                for (final Word opinionWord : tuple
                        .getElements(ElementType.OPINION_WORD)) {
                    if (model.getSentenceIndex() == tuple.getSentenceIndex()) {
                        double assignedScore = opinionWord.getScore();
                        double annotatedScore = model.getOpinionWordScore();
                        if (Math.abs(assignedScore - annotatedScore) < ACCEPTABLE_ERROR) {
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (!found) {
                /*System.out.println(model.getElement()
                        + " - " + model.getSentence());*/
                falseNegative++;
            }
        }
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
