package com.codingnagger.adventofcode2025.days;

import com.codingnagger.adventofcode2025.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day5Tests {
    private static final List<String> INPUT = InputLoader.LoadTest("day5.txt");
    private static final Day DAY = new Day5();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("3");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("14");
    }
}