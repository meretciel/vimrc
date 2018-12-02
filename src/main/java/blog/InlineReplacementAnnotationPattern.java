package blog;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


/**
 * ReplacementAnnotation defines patterns for simple replacement. Note that no other patterns
 * can exist inside a InlineReplacementAnnotationPattern and the ReplacementAnnotation must start and end in the same line.
 */

@AllArgsConstructor
@Getter
public class InlineReplacementAnnotationPattern implements AnnotationPattern {
    final private String start;
    final private String end;
    final private String newStart;
    final private String newEnd;


    public List<String> process(final List<String> lines) {

        int lineNumber = 0;
        int totalLine = lines.size();

        while (lineNumber < totalLine) {
            final String line = lines.get(lineNumber);

            int startIndex = 0;
            int writingIndex = 0;
            StringBuilder builder = new StringBuilder();

            while ((startIndex = line.indexOf(start, writingIndex)) != -1) {
                int endIndex = line.indexOf(end, startIndex);

                if (endIndex != -1) {
                    builder.append(line, writingIndex, startIndex)
                            .append(newStart)
                            .append(line, startIndex + start.length(), endIndex)
                            .append(newEnd);

                    writingIndex = endIndex + end.length();
                }
                else {
                    throw new RuntimeException("Pattern at line " + lineNumber + "does not match.");
                }
            }

            builder.append(line, writingIndex, line.length());
            lines.set(lineNumber, builder.toString());
            ++lineNumber;
        }

        return lines;
    }
}
