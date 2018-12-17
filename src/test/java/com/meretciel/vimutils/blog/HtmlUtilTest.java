package com.meretciel.vimutils.blog;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class HtmlUtilTest {
    @Test
    public void testEscapeAngleBrackets() {
        final String input = "<pre> some <code> here </code> </pre>";
        final String expected="&lt;pre&gt; some &lt;code&gt; here &lt;/code&gt; &lt;/pre&gt;";
        final String output = HtmlUtil.escapeAngleBrackets(input);
        assertThat(output).isEqualTo(expected);
    }
}
