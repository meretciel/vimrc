package com.meretciel.vimutils.blog;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class BlockAnnotationPattern implements AnnotationPattern{
    final private String start;
    final private String end;
    private boolean canMergeWithFirstLine = false;
    private boolean isActive = false;

    private boolean startsInThisLine(final String line) {
        return line.contains(start);
    }

    private boolean endsInThisLine(final String line) {
        return line.contains(end);
    }

    abstract public String getNewStart(final String line);
    abstract public String getNewEnd(final String line);
    abstract public boolean isNewStartPresent(final String line);
    abstract public boolean isNewEndPresent(final String line);

    BlockAnnotationPattern(final String start, final String end, final boolean canMergeWithFirstLine) {
        this.start = start;
        this.end = end;
        this.canMergeWithFirstLine = canMergeWithFirstLine;
    }

    final public boolean canMergeWithFirstLine() { return canMergeWithFirstLine; }

    @Override
    final public List<String> process(final List<String> lines) {

        int lineNumber = 0;
        int totalLine = lines.size();

        while (lineNumber < totalLine) {
            final String line = lines.get(lineNumber);

            if (isActive && endsInThisLine(line)){
                lines.set(lineNumber, getNewEnd(line));
                isActive = false;
            }
            else if (startsInThisLine(line)) {

                final String newStart = getNewStart(line);

                if (canMergeWithFirstLine()) {
                    Validate.isTrue(lineNumber  + 1 < totalLine );
                    String nextLine = lines.get(lineNumber + 1);
                    lines.set(lineNumber, "");
                    lines.set(lineNumber + 1, newStart + nextLine);
                }
                else {
                    lines.set(lineNumber, newStart);
                }

                isActive = true;
            }

            ++lineNumber;
        }

        return lines;
    }

}
