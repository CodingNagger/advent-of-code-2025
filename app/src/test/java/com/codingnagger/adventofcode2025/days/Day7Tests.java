package com.codingnagger.adventofcode2025.days;

import com.codingnagger.adventofcode2025.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day7Tests {
    private static final List<String> INPUT = InputLoader.LoadTest("day7.txt");
    private static final Day DAY = new Day7();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("21");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("40");
    }
}