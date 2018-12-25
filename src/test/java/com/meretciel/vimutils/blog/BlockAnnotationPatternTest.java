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
        StringBuilder sb = new StringBuilder();

        sb.append("<div class=\"codeblock\" style=\"font-family:Courier; font-size:12px;\">\n<div style=\"display: inline-block;vertical-align:top;position:relative;\">\n");
        sb.append("<div style=\"color:#5DADE2;margin-top:1.15em; padding:0.5em;line-height:19px;padding-left:0px;padding-right:0px;margin-left:0px;margin-right:0px;\">\n");
        sb.append("1<br/></div></div>\n");
        sb.append("<div style=\"display: inline-block;vertical-align:top;position:relative;left:-3px\">");

        expected.add(sb.toString());
        expected.add("<pre style=\"padding: 0px;\"><code class=\"java\">here are some java code");
        expected.add("</code></pre></div></div>");

        List<String> output = p.process(lines);

        //assertThat(output.equals(expected)).isTrue();
        assertThat(output).isEqualTo(expected);
    }

}
