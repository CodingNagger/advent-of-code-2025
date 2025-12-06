package com.codingnagger.adventofcode2025.days;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day6 implements Day {

    @Override
    public String partOne(List<String> input) {
        final var numberLines = input.stream()
                .filter(l -> l.matches("([\\s]*[\\d]+[\\s]*)+"))
                .toList();
        final var numbers = new long[numberLines.size()][];

        for (var i = 0; i < numberLines.size(); i++) {
            numbers[i] = Arrays.stream(numberLines.get(i).trim().split("[\\s]+"))
                    .mapToLong(Long::parseLong)
                    .toArray();
        }

        final var operators = input.get(numberLines.size()).trim().split("[\\s]+");
        final var results = new long[operators.length];

        for (var i = 0; i < operators.length; i++) {
            final var isMult = Objects.equals(operators[i], "*");

            results[i] = isMult ? 1 : 0;

            for (var r = 0; r < numbers.length; r++) {
                if (isMult) {
                    results[i] *= numbers[r][i];
                } else {
                    results[i] += numbers[r][i];
                }
            }
        }

        return "" + Arrays.stream(results).sum();
    }

    @Override
    public String partTwo(List<String> input) {
        final var numberLines = input.stream()
                .filter(l -> l.matches("([\\s]*[\\d]+[\\s]*)+"))
                .toList();

        final var operatorsLine = input.get(numberLines.size());

        var cursor = 0;
        var total = BigDecimal.ZERO;

        while (cursor < operatorsLine.length()) {
            var nextOperatorIndex = findNextOperatorIndex(operatorsLine, cursor);
            var length = nextOperatorIndex > 0 ? nextOperatorIndex - 1 : longuestLineLength(numberLines);

            final var numbers = new Long[length - cursor];

            for (var i = cursor; i < length; i++) {
                for (var r = 0; r < numberLines.size(); r++) {
                    final var line = numberLines.get(r);

                    if (i >= line.length()) continue;

                    final char c = line.charAt(i);

                    if (c == ' ') continue;

                    final var digit = Long.parseLong("" + c);

                    numbers[i - cursor] = numbers[i - cursor] == null ? digit : numbers[i - cursor] * 10 + digit;
                }
            }

            final var tmp = operatorsLine.substring(cursor, Math.min(length, operatorsLine.length())).trim();

            if (tmp.isEmpty()) {
                break;
            }

            final var operator = tmp.charAt(0);

            if (operator == '*') {
                var result = BigDecimal.ONE;
                for (var number : numbers) {
                    result = result.multiply(BigDecimal.valueOf(number));
                }
                total = total.add(result);
            } else {
                var result = BigDecimal.ZERO;
                for (var number : numbers) {
                    result = result.add(BigDecimal.valueOf(number));
                }
                total = total.add(result);
            }

            if (nextOperatorIndex == -1) break;

            cursor = nextOperatorIndex;
        }

        return "" + total;
    }

    private int longuestLineLength(List<String> numberLines) {
        return numberLines.stream().mapToInt(String::length).max().orElseThrow();
    }

    private int findNextOperatorIndex(String operatorsLine, int start) {
        if (operatorsLine.substring(start + 1).isBlank()) {
            return -1;
        }

        var cursor = start;
        while (++cursor < operatorsLine.length() && operatorsLine.charAt(cursor) == ' ') ;
        return cursor;
    }
}
