package com.meretciel.vimutils.blog;

public class HtmlBlockPattern extends BlockAnnotationPattern {

    public HtmlBlockPattern(final String start, final String end) {
        super(start, end);
        freezeLine = Integer.MAX_VALUE;
    }

    @Override
    public String getNewStart(final String line) {
        return getStart();
    }

    @Override
    public String getNewEnd(final String line) {
        return getEnd();
    }

    @Override
    public boolean isNewStartPresent(final String linePostChange) {
        return linePostChange.contains(getStart());
    }

    @Override
    public boolean isNewEndPresent(final String linePostChange) {
        return linePostChange.contains(getEnd());
    }
}
