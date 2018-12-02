package blog;

import org.apache.commons.lang3.Validate;

import java.util.List;

interface AnnotationPattern {
    String getStart();
    List<String> process(List<String> lines);

    /**
     * Check if the pattern starts in the line.
     *
     * @param line line in the file
     * @return Check if the pattern is present in the line.
     */
    default boolean hasStart(final String line) {
        Validate.notNull(line);
        return line.contains(getStart());
    }
}
