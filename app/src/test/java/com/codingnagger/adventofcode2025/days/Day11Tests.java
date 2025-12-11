package com.codingnagger.adventofcode2025.days;

import com.codingnagger.adventofcode2025.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day11Tests {
    private static final List<String> INPUT = InputLoader.LoadTest("day11.txt");
    private static final Day DAY = new Day11();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("5");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(InputLoader.LoadTest("day11_part2.txt"));

        assertThat(result).isEqualTo("2");
    }
}