package util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static String getFileContents(String filePath) {
        StringBuilder contents = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath));
            String line = "";
            while ((line = reader.readLine()) != null) {
                contents.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contents.toString();
    }

    public static List<String> getLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            String line = "";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    public static void writeToFile(String filePath, String text) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            out.write(text.getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String extractByRegexOneGroup(String from, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(from);

        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

}
