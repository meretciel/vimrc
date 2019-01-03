package com.meretciel.vimutils.blog;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageAnnotationPattern extends SingleLineAnnotationPattern {
    public ImageAnnotationPattern(String start, String newStart, String newEnd) {
        super(start, newStart, newEnd);
    }

    public List<String> process(final List<String> lines) {

        int lineNumber = 0;
        int totalLine = lines.size();

        while (lineNumber < totalLine) {
            final String line = lines.get(lineNumber);


            int index = line.indexOf(this.getStart());

            if (index != -1) {
                final Pattern p = Pattern.compile("@image\\[(.*?)\\]");
                final Matcher matcher = p.matcher(line);
                double scale = 1;

                if (matcher.find()) {
                    scale = Double.valueOf(matcher.group(1));
                }

                final Pattern srcPattern = Pattern.compile("@image.*?\\s(.*?)$");
                final Matcher srcMatcher = srcPattern.matcher(line);
                String src = "";

                if (srcMatcher.find()) {
                    src = srcMatcher.group(1);
                }

                final String scaleString = String.format("%.2f", 100 * scale);

                final String style = "style=\"width:" + scaleString + "%;height:" + scaleString + "%;\"";
                final String content = "<img " + style + " src=" + src + " alt=\"Error with src=" + src + "\">";

                StringBuilder builder = new StringBuilder();
                builder.append(this.getNewStart())
                        .append(content)
                        .append(this.getNewEnd());

                lines.set(lineNumber, builder.toString());
            }

            ++lineNumber;
        }

        return lines;
    }
}


