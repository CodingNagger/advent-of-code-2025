package com.codingnagger.adventofcode2025.days;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Day2 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + findAndAddInvalidIds(input, this::isInvalidIdPartOne);
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + findAndAddInvalidIds(input, this::isInvalidIdPartTwo);
    }

    private boolean isInvalidIdPartTwo(Long id) {
        final var fullId = String.valueOf(id);

        for (var i = 1; i <= fullId.length() / 2; i++) {
            final var partialId = fullId.substring(0, i);
            if (fullId.replaceAll(partialId, "").isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean isInvalidIdPartOne(long id) {
        final var value = String.valueOf(id).toCharArray();

        if (value.length % 2 == 1) {
            return false;
        }

        final var midPoint = value.length / 2;
        for (var i = 0; i < midPoint; i++) {
            if (value[i] != value[midPoint + i]) {
                return false;
            }
        }

        return true;
    }

    private long findAndAddInvalidIds(List<String> input, Predicate<Long> isInvalid) {
        return Arrays.stream(input.getFirst().split(","))
                .map(Range::parse)
                .map(range -> invalidIds(range, isInvalid))
                .flatMap(List::stream)
                .mapToLong(l -> l)
                .sum();
    }

    private List<Long> invalidIds(Range range, Predicate<Long> isInvalid) {
        final var ids = new ArrayList<Long>();

        for (var id = range.min; id <= range.max; id++) {
            if (isInvalid.test(id)) {
                IO.println(id + " is invalid");
                ids.add(id);
            }
        }

        return ids;
    }

    record Range(long min, long max) {
        static Range parse(String input) {
            final var parts = input.split("-");
            return new Range(
                    Long.parseLong(parts[0]),
                    Long.parseLong(parts[1])
            );
        }
    }
}
