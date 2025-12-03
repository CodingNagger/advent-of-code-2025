package com.codingnagger.adventofcode2025.days;

import com.codingnagger.adventofcode2025.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day3Tests {
    private static final List<String> INPUT = InputLoader.LoadTest("day3.txt");
    private static final Day DAY = new Day3();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("357");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("3121910778619");
    }
}