package com.meretciel.vimutils.helper;

import javax.management.relation.RelationNotFoundException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiffTool {
    final public static String FIRST_FILE_START_PATTERN = "@first";
    final public static String SECOND_FILE_START_PATTERN = "@second";
    final public static String FIRST_FILE = "/Users/Ruikun/workspace/Programs/tmp/DiffTool-first";
    final public static String SECOND_FILE = "/Users/Ruikun/workspace/Programs/tmp/DiffTool-second";

    public static int skipEmptyLine( int ln, final List<String> lines) {
        while (ln < lines.size() && lines.get(ln).isEmpty()) {
            ++ln;
        }
        return ln;
    }

    public static void process(List<String> lines) {

        boolean hasFirst = false;
        boolean hasSecond = false;

        for (String line : lines ) {
            if (line.startsWith(FIRST_FILE_START_PATTERN)) {
                hasFirst = true;
            }

            if (line.startsWith(SECOND_FILE_START_PATTERN)){
                hasSecond = true;
            }
        }

        if (!(hasFirst && hasSecond)) {
            throw new RuntimeException("Invalid input. Start patterns are missing.");
        }

        int ln = 0;

        while (ln < lines.size() && !lines.get(ln).startsWith(FIRST_FILE_START_PATTERN)) {
            ++ln;
        }

        ++ln;
        int startingLineOfFirstPart = ln;
        int startingLineOfSecondPart = 0;
        boolean hasError = false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FIRST_FILE))) {
            ln = skipEmptyLine(ln, lines);

            startingLineOfSecondPart = ln;

            while (startingLineOfFirstPart < lines.size() &&
                    !lines.get(startingLineOfSecondPart).startsWith(SECOND_FILE_START_PATTERN)) {
                ++startingLineOfSecondPart;
            }

            int stopLine = startingLineOfSecondPart - 1;
            while (stopLine >= ln && lines.get(stopLine).isEmpty()) {
                --stopLine;
            }

            for (int i = ln; i <= stopLine; ++i) {
                writer.write(lines.get(i));
                writer.write("\n");
            }

        }
        catch(IOException e) {
            System.out.println("Failed to write the first part.");
            System.out.println(e.getMessage());
            hasError = true;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SECOND_FILE))) {
            ++startingLineOfSecondPart; // skip the start pattern of the second part.

            startingLineOfSecondPart = skipEmptyLine(startingLineOfSecondPart, lines);

            int stopLine = lines.size() - 1;

            while (stopLine >= startingLineOfSecondPart && lines.get(stopLine).isEmpty()) {
                --stopLine;
            }

            for (int i = startingLineOfSecondPart; i <= stopLine; ++i) {
                writer.write(lines.get(i));
                writer.write("\n");
            }

        }
        catch(IOException e) {
            System.out.println("Failed to write the second part.");
            System.out.println(e.getMessage());
            hasError = true;
        }

        if (!hasError) {
            try {

                for (String line : lines) {
                    System.out.println(line);
                }

                System.out.println();
                System.out.println("<<<");

                ProcessBuilder builder = new ProcessBuilder("diff", "-u", FIRST_FILE, SECOND_FILE);

                Process p = builder.start();
                p.waitFor();

                BufferedReader readerOfOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line;
                while ((line = readerOfOutput.readLine()) != null) {
                    System.out.println(line);
                }

                BufferedReader readerOfError = new BufferedReader((new InputStreamReader(p.getErrorStream())));

                while ((line = readerOfError.readLine()) != null) {
                    System.out.println(line);
                }

                System.out.println(">>>");
                System.out.println();

            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
            catch(InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            List<String> lines = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            process(lines);

        }
        catch (IOException e) {
            e.getMessage();
        }
    }
}
