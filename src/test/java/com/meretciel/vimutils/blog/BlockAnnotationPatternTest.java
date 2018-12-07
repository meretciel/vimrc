package com.meretciel.vimutils.blog;


import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;
public class BlockAnnotationPatternTest {

    @Test
    public void testCodeBlockPattern() {
        AnnotationPattern p = Config.CODE_BLOCK;

        List<String> lines = new ArrayList<>();
        lines.add("@code[lang=java] ");
        lines.add("here are some java code");
        lines.add("@end-code");

        List<String> expected = new ArrayList<>();
        expected.add("");
        expected.add("<pre style=\"padding: 0px;\"><code class=\"java\">here are some java code");
        expected.add("</code></pre>");

        List<String> output = p.process(lines);

        //assertThat(output.equals(expected)).isTrue();
        assertThat(output).isEqualTo(expected);
    }

}
