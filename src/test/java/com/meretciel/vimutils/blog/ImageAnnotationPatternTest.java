package com.meretciel.vimutils.blog;


import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class ImageAnnotationPatternTest {


    @Test
    public void testProcess() {
        final AnnotationPattern p = Config.IMAGE;
        final List<String> lines = Arrays.asList("@image file1", "@image[0.7] file2");
        final List<String> expected = Arrays.asList(
            "<div class=\"img-div\"><img style=\"width:100.00%;height:100.00%;\" src=file1 alt=\"Error with src=file1\"></div>",
            "<div class=\"img-div\"><img style=\"width:70.00%;height:70.00%;\" src=file2 alt=\"Error with src=file2\"></div>"
        );

        final List<String> output = p.process(lines);
        assertThat(output).isEqualTo(expected);

    }
}
