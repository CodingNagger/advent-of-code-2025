package com.codingnagger.adventofcode2025.days;

import com.codingnagger.adventofcode2025.utils.InputLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day1Tests {
    private static final List<String> INPUT = InputLoader.LoadTest("day1.txt");
    private static final Day DAY = new Day1();

    @Test
    void partOne() {
        String result = DAY.partOne(INPUT);

        assertThat(result).isEqualTo("3");
    }

    @Test
    void partTwo() {
        String result = DAY.partTwo(INPUT);

        assertThat(result).isEqualTo("6");
    }

    @Test
    void partTwo_hint() {
        String result = DAY.partTwo(List.of("R1000"));

        assertThat(result).isEqualTo("10");
    }

    @Test
    void partTwo_52_R48_shouldBeOneRotation() {
        String result = DAY.partTwo(List.of("R2", "R48"));

        assertThat(result).isEqualTo("1");
    }

    @Test
    void partTwo_0_L5_shouldBeZeroRotation() {
        String result = DAY.partTwo(List.of("L50", "L5"));

        assertThat(result).isEqualTo("1");
    }
}