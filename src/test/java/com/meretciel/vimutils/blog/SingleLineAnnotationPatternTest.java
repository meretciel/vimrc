package com.meretciel.vimutils.blog;



import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;


public class SingleLineAnnotationPatternTest {
    @Test
    public void testProcess() {
        AnnotationPattern p = Config.HEADER_1;
        List<String> lines = new ArrayList<>();
        lines.add("@h1 This is a header");
        List<String> output = p.process(lines);

        String expected = "<h1> This is a header</h1>";
        String result   = output.get(0);

        assertThat(result).isEqualTo(expected);
    }
}
