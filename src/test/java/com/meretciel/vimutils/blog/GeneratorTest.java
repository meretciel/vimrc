package com.meretciel.vimutils.blog;



import org.junit.Ignore;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneratorTest {
    @Ignore
    @Test
    public void testAddTag() {
        List<String> lines = new ArrayList<>();
        lines.add("The code below shows the use of the two methods:");
        lines.add("");
        lines.add("");
        lines.add("<div class=\"codeblock\" ><pre style=\"padding: 0px;\"><code class=\"java\">package methodOverride.packageA;");
        lines.add("public class");
        lines.add("</code></pre></div>");

        List<String> expected = new ArrayList<>();
        expected.add("The code below shows the use of the two methods:</br>");
        expected.add("");
        expected.add("");
        expected.add("<div class=\"codeblock\" ><pre style=\"padding: 0px;\"><code class=\"java\">package methodOverride.packageA;");
        expected.add("public class");
        expected.add("</code></pre></div>");

        List<String> output = Generator.addNewLineHtmlTag(lines);

        assertThat(output).isEqualTo(expected);

    }
}
