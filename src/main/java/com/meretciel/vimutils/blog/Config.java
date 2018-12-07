package com.meretciel.vimutils.blog;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Config {

    final static public String PATTERN_END = "\\}";
    final static public AnnotationPattern HIGHLIGHT = new InlineReplacementAnnotationPattern("\\hl{", PATTERN_END, "<b>", "</b>");
    final static public AnnotationPattern NOTE = new InlineReplacementAnnotationPattern("\\note{", PATTERN_END, "", "");
    final static public AnnotationPattern COMMAND = new InlineReplacementAnnotationPattern("\\command{", PATTERN_END, "<code>", "</code>");
    final static public AnnotationPattern DANGER = new InlineReplacementAnnotationPattern("\\danger{", PATTERN_END, "<span style=\"color:red;\">", "</span>");
    final static public AnnotationPattern WARNING = new InlineReplacementAnnotationPattern("\\warning{", PATTERN_END, "<span style=\"color:orange;\">", "</span>");
    final static public AnnotationPattern INFO = new InlineReplacementAnnotationPattern("\\info{", PATTERN_END, "<span style=\"color:cyan;\">", "</span>");
    final static public AnnotationPattern RED_TEXT = new InlineReplacementAnnotationPattern("\\red{", PATTERN_END, "<span style=\"color:red;\">", "</span>");

    final static public AnnotationPattern HEADER_1 = new SingleLineAnnotationPattern("@h1", "<h1>", "</h1>");
    final static public AnnotationPattern HEADER_2 = new SingleLineAnnotationPattern("@h2", "<h2>", "</h2>");
    final static public AnnotationPattern HEADER_3 = new SingleLineAnnotationPattern("@h3", "<h3>", "</h3>");

    final static public AnnotationPattern HTML_ORDERED_LIST = new HtmlBlockPattern("<ol>", "</ol>");
    final static public AnnotationPattern HTML_UNORDERED_LIST = new HtmlBlockPattern("<ul>", "</ul>");

    final static public AnnotationPattern OUTPUT_BLOCK = new BlockAnnotationPattern("@output", "@end-output") {

        @Override
        public String getNewStart(final String line) {
            return "<pre>";
        }

        @Override
        public String getNewEnd(final String line) {
            return "</pre>";
        }

        @Override
        public boolean isNewStartPresent(final String linePostChange) {
            return linePostChange.equals("<pre>");
        }

        @Override
        public boolean isNewEndPresent(final String linePostChange) {
            return linePostChange.equals("</pre>");
        }
    };

    final static public AnnotationPattern CODE_BLOCK = new BlockAnnotationPattern("@code", "@end-code", true) {

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
            return linePostChange.startsWith("<pre style=\"padding: 0px;\"><code");
        }

        @Override
        public boolean isNewEndPresent(final String linePostChange) {
            return linePostChange.startsWith("</code></pre>");
        }

    };

    final static public ImmutableList<AnnotationPattern> ALL_PATTERNS = ImmutableList.<AnnotationPattern>builder()
            .add(HIGHLIGHT, NOTE, COMMAND, DANGER, WARNING)
            .add(INFO, RED_TEXT, OUTPUT_BLOCK)
            .add(HEADER_1, HEADER_2, HEADER_3)
            .add(CODE_BLOCK)
            .add(HTML_ORDERED_LIST, HTML_UNORDERED_LIST)
            .build();

    final static public List<BlockAnnotationPattern> BLOCK_PATTERNS = ImmutableList.<BlockAnnotationPattern>copyOf(ALL_PATTERNS.stream()
            .filter((p) -> (p instanceof BlockAnnotationPattern))
            .map((p) -> (BlockAnnotationPattern) p)
            .collect(Collectors.toList()));

    final static public List<InlineReplacementAnnotationPattern> INLINE_PATTERNS = ImmutableList.<InlineReplacementAnnotationPattern>copyOf(ALL_PATTERNS.stream()
            .filter((p) -> (p instanceof InlineReplacementAnnotationPattern))
            .map((p) -> (InlineReplacementAnnotationPattern) p)
            .collect(Collectors.toList()));

    final static public List<SingleLineAnnotationPattern> SINGLE_LINE_PATTERNS = ImmutableList.<SingleLineAnnotationPattern>copyOf(ALL_PATTERNS.stream()
            .filter((p) -> (p instanceof SingleLineAnnotationPattern))
            .map((p) -> (SingleLineAnnotationPattern) p)
            .collect(Collectors.toList()));

}