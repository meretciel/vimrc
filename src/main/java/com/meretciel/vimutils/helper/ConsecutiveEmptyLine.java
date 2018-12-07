package com.meretciel.vimutils.helper;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ConsecutiveEmptyLine {
    final static public String TMP_FILE = "/Users/Ruikun/workspace/Programs/tmp/consecutive-empty-line.log";

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            List<Integer> targets = new ArrayList<>();

            boolean isPreviousLineEmpty = false;
            int ln = 1;

            String line;
            while ((line = reader.readLine()) != null) {
                boolean isEmpty = line.isEmpty();

                if (isEmpty && isPreviousLineEmpty) {
                    targets.add(ln);
                }

                isPreviousLineEmpty = isEmpty;
                ++ln;
            }

            List<Integer> reversed = ImmutableList.copyOf(targets).reverse();

            for (Integer item : reversed) {
                System.out.println("ln: " + item);
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
