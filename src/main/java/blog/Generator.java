package blog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;


public class Generator {
    final static public String BLOG_HEADER_FILE = "/Users/Ruikun/workspace/Programs/vimutils/blog-header.txt";
    final static public String TMP_FILE = "/Users/Ruikun/workspace/Programs/tmp/transformed.log";
    final static public String NEW_LINE_TAG = "</br>";


    public static List<String> addNewLineHtmlTag(final List<String> lines) {
        int lineNumber = 0;
        int totalLineNumber = lines.size();
        boolean canAdd = true;

        while (lineNumber < totalLineNumber - 1) {
            final String line = lines.get(lineNumber);
            if (!line.isEmpty() && !line.equals(NEW_LINE_TAG)) {

                boolean hasSingleLinePattern = Config.SINGLE_LINE_PATTERNS.stream()
                        .anyMatch((p) -> line.contains(p.getNewStart()));

                boolean hasBlockPattern = Config.BLOCK_PATTERNS.stream()
                        .anyMatch((p) -> p.isNewStartPresent(line) || p.isNewEndPresent(line));

                if (!hasSingleLinePattern && !hasBlockPattern && canAdd) {
                    lines.set(lineNumber, line + NEW_LINE_TAG);
                }

                canAdd = canAdd && !hasSingleLinePattern;

                if (hasBlockPattern) {
                    boolean hasStart = Config.BLOCK_PATTERNS.stream()
                            .anyMatch((p) -> p.isNewStartPresent(line));
                    canAdd = !hasStart;
                }
            }
            else {
                final String nextLine = lines.get(lineNumber + 1);
                boolean isNextLineEmpty = nextLine.isEmpty();
                boolean isNextLineBlock = Config.BLOCK_PATTERNS.stream()
                                            .anyMatch((p) -> p.isNewStartPresent(nextLine));

                if (!isNextLineEmpty && !isNextLineBlock && canAdd) {
                    lines.set(lineNumber, line + NEW_LINE_TAG);
                }
                else {
                    lines.set(lineNumber, "");
                }
            }

            ++lineNumber;
        }


        return lines;
    }


    public static void main(String[] args) throws IOException {

        // read the blog header and print to standard output
        try (BufferedReader reader = new BufferedReader(new FileReader(BLOG_HEADER_FILE));
             BufferedWriter writer = new BufferedWriter(new FileWriter(TMP_FILE))
        ) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
        }


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TMP_FILE, true))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.write("\n");
                }
            }
        }
        catch (RuntimeException e) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TMP_FILE, true))) {
                writer.write(e.getMessage());
            }
        }
    }
}
