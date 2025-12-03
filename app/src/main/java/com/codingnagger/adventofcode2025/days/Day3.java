package com.codingnagger.adventofcode2025.days;

import java.util.List;

public class Day3 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + input.stream()
                .mapToLong(this::largestVoltage)
                .sum();
    }

    private long largestVoltage(String input) {
        var leftDigit = '0';
        var rightDigit = '0';
        final var bank = input.toCharArray();

        // find largest first digit where there is
        // find largest second digit

        var leftDigitI = 0;
        for (var i = 0; i < bank.length - 1; i++) {
            if (bank[i] > leftDigit) {
                leftDigit = bank[i];
                leftDigitI = i;
            }
        }

        for (var i = leftDigitI + 1; i < bank.length; i++) {
            if (bank[i] > rightDigit) {
                rightDigit = bank[i];
            }
        }

        return Long.parseLong(leftDigit + "" + rightDigit);
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + input.stream()
                .mapToLong(this::largestVoltagePartTwo)
                .sum();
    }

    private long largestVoltagePartTwo(String input) {
        final var bank = input.toCharArray();

        var searchStart = 0;
        var searchEnd = input.length() - 12;

        final var result = new StringBuilder();

        while (searchEnd < input.length()) {
            var largestDigit = '0';
            var largestI = 0;
            for (var i = searchStart; i <= searchEnd; i++) {
                if (bank[i] > largestDigit) {
                    largestDigit = bank[i];
                    largestI = i;
                }
            }

            searchStart = largestI + 1;
            searchEnd++;
            result.append(bank[largestI]);
        }

        return Long.parseLong(result.toString());
    }
}
