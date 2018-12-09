package com.meretciel.vimutils.helper;

import org.apache.commons.lang3.Validate;

import javax.xml.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;
import java.util.StringJoiner;

public class ColumnsAlignment {
    final static int BUFFER_WIDTH = 2;

    public static String makeFormatTemplate(final int width) {
        return "%-" + width + "s";
    }

    public static List<String> process(final List<String> lines, final String delimiter) {

        List<List<String>> contents = new ArrayList<>();
        int maxNumberOfElements = 0;

        for (String line : lines) {
            String[] tokens = line.split(delimiter,-1);

            List<String> row = new ArrayList<>();
            for (String token : tokens) {
                row.add(token.trim());
            }

            contents.add(row);
            maxNumberOfElements = Math.max(maxNumberOfElements, tokens.length);
        }

        List<Integer> columnWidths = new ArrayList<>();

        for (int j = 0; j < maxNumberOfElements; ++j) { columnWidths.add(0); }

        for (int j = 0; j < maxNumberOfElements; ++j) {
            for (int i = 0; i < contents.size(); ++i) {
                if (j < contents.get(i).size()) {
                    columnWidths.set(j, Math.max( columnWidths.get(j), contents.get(i).get(j).length()));
                }
            }
        }

        List<String> outputs = new ArrayList<>();

        for (int i = 0; i < contents.size(); ++i) {
            StringBuilder sb = new StringBuilder();

            List<String> row = contents.get(i);

            for (int j = 0; j < row.size(); ++j) {
                final String formatTemplate = makeFormatTemplate(columnWidths.get(j) + BUFFER_WIDTH);
                sb.append(String.format(formatTemplate, row.get(j) + delimiter));
            }

            outputs.add(sb.toString());
        }

        return outputs;
    }


    public static void main(String[] args) {
        Validate.isTrue(args.length == 1, "Delimiter must be specified.");
        final String delimiter = args[0];

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) { lines.add(line); }

            List<String> outputs = process(lines, delimiter);

            for (String row : outputs) {
                System.out.println(row);
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
