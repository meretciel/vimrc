package com.meretciel.vimutils.helper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class ColumnsAlignmentTest {
    @Test
    public void testProcess() {
        final String delimiter = ",";
        List<String> lines = new ArrayList<>();

        lines.add("Variable A, Variable B, Variable C");
        lines.add("");
        lines.add("10, 20, 30");
        lines.add("10,");
        lines.add("10,,30");

        List<String> expected = new ArrayList<>();
        expected.add("Variable A, Variable B, Variable C, ");
        expected.add(",           ");
        expected.add("10,         20,         30,         ");
        expected.add("10,         ,           ");
        expected.add("10,         ,           30,         ");
        
        List<String> outputs = ColumnsAlignment.process(lines, delimiter);

        assertThat(outputs).isEqualTo(expected);


    }
}
