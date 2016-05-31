import java.util.List;

import services.ScoreAnnotatorService;
import services.SentiWordNetService;
import util.Helper;

public class Main {

    private static final String PATH_TO_INPUT_FILE = "D:\\GitHub\\Score_benchmark_Annotated_No_Scores_No_Modifiers.txt";
    private static final String PATH_TO_OUTPUT_FILE = "D:\\GitHub\\Score_benchmark_Automated_Annotation.txt";
    private static final String PATH_TO_SWN = "D:\\GitHub\\SWN\\SentiWordNet.txt";

    private static SentiWordNetService sentiWordNetService;

    public static void main(String[] args) {
        sentiWordNetService = SentiWordNetService.getInstance(PATH_TO_SWN);

        List<String> lines = Helper.getLines(PATH_TO_INPUT_FILE);

        String annotatedText = new ScoreAnnotatorService(sentiWordNetService).appendScoresToAnnotation(lines);

        Helper.writeToFile(PATH_TO_OUTPUT_FILE, annotatedText);

    }

}
