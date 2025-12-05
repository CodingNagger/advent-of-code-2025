package com.codingnagger.adventofcode2025.days;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Day5 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var freshRanges = new ArrayList<FreshIngredientRange>();
        final var ingredients = new ArrayList<Long>();

        for (var line : input) {
            if (line.isEmpty()) continue;

            if (line.contains("-")) {
                freshRanges.add(FreshIngredientRange.parse(line));
            } else {
                ingredients.add(Long.parseLong(line));
            }
        }

        return "" + ingredients.parallelStream().filter(i -> isFresh(freshRanges, i)).count();
    }

    private boolean isFresh(ArrayList<FreshIngredientRange> freshRanges, Long ingredient) {
        return freshRanges.parallelStream().anyMatch(r -> r.contains(ingredient));
    }

    @Override
    public String partTwo(List<String> input) {
        final var ranges = new ArrayList<FreshIngredientRange>();

        for (var line : input) {
            if (line.contains("-")) {
                ranges.add(FreshIngredientRange.parse(line));
            }
        }

        final var mergedRanges = mergeRanges(ranges);

        return "" + mergedRanges.stream().mapToLong(FreshIngredientRange::size).sum();
    }

    private List<FreshIngredientRange> mergeRanges(List<FreshIngredientRange> ranges) {
        // for each range, find overlapping ranges and set new merged ; mark old ranges for deletion
        ArrayList<FreshIngredientRange> mergedRanges;
        ArrayList<Integer> rangeIndexesToRemove;

        do {
            rangeIndexesToRemove = new ArrayList<>();
            mergedRanges = new ArrayList<>();

            for (var i = 0; i < ranges.size(); i++) {
                if (rangeIndexesToRemove.contains(i)) continue;

                var current = ranges.get(i);

                for (var j = i + 1; j < ranges.size(); j++) {
                    if (rangeIndexesToRemove.contains(j)) continue;

                    var other = ranges.get(j);

                    if (current.overlaps(other)) {
                        rangeIndexesToRemove.add(i);
                        rangeIndexesToRemove.add(j);
                        current = current.merge(other);
                    }
                }

                mergedRanges.add(current);
            }

            ranges = mergedRanges;
        } while (!rangeIndexesToRemove.isEmpty());


        return mergedRanges;
    }

    public record FreshIngredientRange(long min, long max) {
        public static FreshIngredientRange parse(String line) {
            final var parts = line.split("-");
            return new FreshIngredientRange(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }

        public boolean contains(Long ingredient) {
            return min <= ingredient && max >= ingredient;
        }

        public boolean overlaps(FreshIngredientRange other) {
            return contains(other.min) || contains(other.max) || other.contains(min) || other.contains(max);
        }

        public FreshIngredientRange merge(FreshIngredientRange other) {
            return new FreshIngredientRange(
                    Math.min(min, other.min),
                    Math.max(max, other.max)
            );
        }

        public long size() {
            return max - min + 1;
        }
    }
}
