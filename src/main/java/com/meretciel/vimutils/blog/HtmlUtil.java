package com.meretciel.vimutils.blog;

public class HtmlUtil {
    public static String escapeAngleBrackets(final String line) {
        String result = line.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        return result;
    }

    private HtmlUtil(){};
}
