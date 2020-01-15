package com.meretciel.vimutils.blog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static List<String> addNewLineHtmlTag(final List<String> lines) {
        int lineNumber = 0;
        int freezeLine = -1;
        int totalLineNumber = lines.size();
        boolean canAdd = true;
        boolean isPreviousLineSingleLinePattern = false;

        while (lineNumber < totalLineNumber - 1) {
            final String line = lines.get(lineNumber);
            if (!line.isEmpty() && !line.equals(Config.NEW_LINE_TAG)) {

                boolean hasSingleLinePattern = Config.SINGLE_LINE_PATTERNS.stream()
                        .anyMatch((p) -> line.contains(p.getNewStart()));

                boolean hasBlockPattern = Config.BLOCK_PATTERNS.stream()
                        .anyMatch((p) -> p.isNewStartPresent(line) || p.isNewEndPresent(line));

                if (!hasSingleLinePattern && !hasBlockPattern && canAdd
                        && (lineNumber > freezeLine)) {
                    lines.set(lineNumber, line + Config.NEW_LINE_TAG);
                }

                isPreviousLineSingleLinePattern = hasSingleLinePattern;

                if (hasBlockPattern) {
                    boolean hasStart = Config.BLOCK_PATTERNS.stream()
                            .anyMatch((p) -> p.isNewStartPresent(line));
                    canAdd = !hasStart;
                    freezeLine = lineNumber + 1;
                }
            } else {
                final String nextLine = lines.get(lineNumber + 1);
                boolean isNextLineEmpty = nextLine.isEmpty();
                boolean isNextLineBlock = Config.BLOCK_PATTERNS.stream()
                        .anyMatch((p) -> p.isNewStartPresent(nextLine));
                boolean isNextLineSingleLine = Config.SINGLE_LINE_PATTERNS.stream()
                        .anyMatch((p) -> nextLine.contains(p.getNewStart()));

                if (!isNextLineEmpty && !isNextLineBlock && !isNextLineSingleLine && canAdd
                        && !isPreviousLineSingleLinePattern
                        && (lineNumber > freezeLine)) {
                    lines.set(lineNumber, line + Config.NEW_LINE_TAG);
                } else {
                    lines.set(lineNumber, "");
                }
            }

            ++lineNumber;
        }


        return lines;
    }


    private static void loadBeforeContent() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Config.BEFORE_CONTENT_FILE))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to load BEFORE_CONTENT.");
        }
    }

    private static void loadAfterontent() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Config.AFTER_CONTENT_FILE))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to load AFTER_CONTENT.");
        }
    }



    public static void main(String[] args) throws IOException {

        // read the com.meretciel.vimutils.blog header and print to standard output
        loadBeforeContent();

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

        final String inputFile;
        if (args.length == 0) {
            inputFile = "/Users/Ruikun/workspace/Programs/vimutils/helper/java-process-redirection.blog";
        } else {
            inputFile = args[0];
        }

         try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            List<String> lines = new ArrayList<>();

            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // iterate each line and transform the tags
            final int nLines = lines.size();

            for (AnnotationPattern p : Config.ALL_PATTERNS) {
                lines = p.process(lines);
            }

            lines = addNewLineHtmlTag(lines);

            for (String l : lines) {
                System.out.println(l);
            }

            loadAfterontent();

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
