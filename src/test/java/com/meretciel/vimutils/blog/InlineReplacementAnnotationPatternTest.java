package com.meretciel.vimutils.blog;

import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class InlineReplacementAnnotationPatternTest {

    @Test
    public void testProcess() {
        AnnotationPattern p = Config.INFO;

        List<String> lines = new ArrayList<>();
        lines.add("This is \\info{info pattern\\} and this is \\command{command pattern\\}.");

        final String expected = "This is <span style=\"color:cyan;\">info pattern</span> and this is \\command{command pattern\\}.";

        List<String> output = p.process(lines);
        assertThat(output.get(0)).isEqualTo(expected);

    }
}
