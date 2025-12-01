package com.codingnagger.adventofcode2025.days;

import java.util.ArrayList;
import java.util.List;

public class Day1 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var start = 50;
        final var outputs = new ArrayList<Integer>();
        var cursor = start;

        for (var command : input) {
            final var direction = command.charAt(0);
            final var distance = Integer.parseInt(command.substring(1));

            cursor = switch (direction) {
                case 'L' -> 100 + cursor - distance;
                case 'R' -> cursor + distance;
                default -> throw new IllegalArgumentException("Invalid direction " + direction);
            };

            cursor = cursor % 100;

            outputs.add(cursor);
        }

        return "" + outputs.stream().filter(i -> i == 0).count();
    }

    @Override
    public String partTwo(List<String> input) {
        var cursor = 50;
        var count = 0;

        for (var command : input) {
            final var direction = command.charAt(0);
            final var distance = Integer.parseInt(command.substring(1));
            final var previous = cursor;

            final var fullRotationCount = distance / 100;
            final var remainingDistance = distance % 100;

            cursor = switch (direction) {
                case 'L' -> 100 + cursor - remainingDistance;
                case 'R' -> cursor + remainingDistance;
                default -> throw new IllegalArgumentException("Invalid direction " + direction);
            };

            cursor = cursor % 100;

            if (direction == 'L' && previous < cursor && previous != 0) {
                count++;
            } else if (direction == 'R' && previous > cursor) {
                count++;
            } else if (cursor == 0) {
                count++;
            }

            count += fullRotationCount;

            IO.println(String.format("%s - Previous [%d] - Current [%d] - Count [%d]", command, previous, cursor, count));
        }

        return "" + count;
    }
}
