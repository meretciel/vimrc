package com.meretciel.vimutils.blog;

import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeBlockAnnotationPattern extends BlockAnnotationPattern {

    public CodeBlockAnnotationPattern(final String start, final String end, final boolean canMergeWithFirstLine) {
        super(start, end, canMergeWithFirstLine);
    }

    @Override
    public String getNewStart(final String line) {

        final Pattern p = Pattern.compile("@code\\[lang=(.*?)\\]");
        final Matcher matcher = p.matcher(line);

        if (matcher.find()) {
            final String lang = matcher.group(1);
            return "<pre style=\"padding: 0px;\"><code class=\"" + lang + "\">";
        } else {
            return "<pre style=\"padding: 0px;\"><code>";
        }
    }

    @Override
    public String getNewEnd(final String line) {
        return "</code></pre>";
    }

    @Override
    public boolean isNewStartPresent(final String linePostChange) {
        return linePostChange.startsWith("<div class=\"codeblock\" ");
    }

    @Override
    public boolean isNewEndPresent(final String linePostChange) {
        return linePostChange.contains("</code></pre>");
    }

    @Override
    final public List<String> process(final List<String> lines) {

        int lineNumber = 0;
        int totalLine = lines.size();
        int linesInBlock = 0;
        int lineNumberOfStart = -1;

        while (lineNumber < totalLine) {
            String line = lines.get(lineNumber);

            if (isActive) {
                ++linesInBlock;
            }

            if (isActive && endsInThisLine(line)){
                final String prefix = generatePrefix(linesInBlock-1);
                lines.set(lineNumberOfStart, prefix + lines.get(lineNumberOfStart));
                lines.set(lineNumber, getNewEnd(line) + "</div></div>");
                linesInBlock = 0;
                lineNumberOfStart = 0;
                isActive = false;
            }
            else if (startsInThisLine(line)) {
                lineNumberOfStart = lineNumber;
                final String newStart = getNewStart(line);

                if (canMergeWithFirstLine()) {
                    Validate.isTrue(lineNumber  + 1 < totalLine );
                    String nextLine = HtmlUtil.escapeAngleBrackets(lines.get(lineNumber + 1));
                    lines.set(lineNumber, "");
                    lines.set(lineNumber + 1, newStart + nextLine);
                    freezeLine = lineNumber + 1;
                }
                else {
                    lines.set(lineNumber, newStart);
                }

                isActive = true;
            }
            else {
                if (lineNumber > freezeLine && isActive) {
                    lines.set(lineNumber, HtmlUtil.escapeAngleBrackets(line));
                }
            }

            ++lineNumber;
        }
        return lines;
    }


    private String generatePrefix(final int linesInBlock) {
        final int width = (int) Math.log10((linesInBlock)) + 1;
        final String template = "%" + width + "d";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"codeblock\" style=\"font-family:Courier; font-size:12px;\">\n" +
                "<div style=\"display: inline-block;vertical-align:top;position:relative;\">\n" +
                "<div style=\"color:#5DADE2;margin-top:1.15em; padding:0.5em;line-height:19px;padding-left:0px;padding-right:0px;margin-left:0px;margin-right:0px;\">\n");

        for (int i = 0; i < linesInBlock; ++i) {
            sb.append(String.format(template, i + 1).replace(" ", "&nbsp;") + "<br/>");
        }

        sb.append("</div></div>\n" +
                "<div style=\"display: inline-block;vertical-align:top;position:relative;left:-3px\">");

        return sb.toString();

    }
}
