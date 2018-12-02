package blog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SingleLineAnnotationPattern implements AnnotationPattern {
    final private String start;
    final private String newStart;
    final private String newEnd;



    public List<String> process(final List<String> lines) {

        int lineNumber = 0;
        int totalLine = lines.size();

        while (lineNumber < totalLine) {
            final String line = lines.get(lineNumber);

            StringBuilder builder = new StringBuilder();
            int index = line.indexOf(start);

            if (index != -1) {
                builder.append(newStart)
                        .append(line, index + start.length(), line.length())
                        .append(newEnd);
                lines.set(lineNumber, builder.toString());
            }

            ++lineNumber;
        }

        return lines;
    }

}
