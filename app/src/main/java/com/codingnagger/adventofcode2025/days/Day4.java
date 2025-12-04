package com.codingnagger.adventofcode2025.days;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day4 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var diagram = input.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        return "" + countAccessiblePaperRolls(diagram);
    }

    @Override
    public String partTwo(List<String> input) {
        var diagram = input.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        var result = 0;
        List<Position> accessiblePaperRolls;

        do {
            accessiblePaperRolls = listAccessiblePaperRolls(diagram);
            diagram = removeAccessiblePaperRolls(diagram, new HashSet<>(accessiblePaperRolls));

            result += accessiblePaperRolls.size();
        } while (!accessiblePaperRolls.isEmpty());
        return "" + result;
    }

    private char[][] removeAccessiblePaperRolls(char[][] diagram, Set<Position> accessiblePaperRolls) {
        final var newDiagram = new char[diagram.length][];

        for (var row = 0; row < diagram.length; row++) {
            newDiagram[row] = new char[diagram[row].length];

            for (var column = 0; column < diagram[row].length; column++) {
                newDiagram[row][column] = accessiblePaperRolls.contains(new Position(column, row)) ?
                        '.' : diagram[row][column];
            }
        }

        return newDiagram;
    }

    private List<Position> listAccessiblePaperRolls(char[][] diagram) {
        var result = new ArrayList<Position>();

        for (var row = 0; row < diagram.length; row++) {
            for (var column = 0; column < diagram[row].length; column++) {
                if (diagram[row][column] == '@' && countAdjacentRolls(diagram, row, column) < 4) {
                    result.add(new Position(column, row));
                }
            }
        }

        return result;
    }

    private long countAccessiblePaperRolls(char[][] diagram) {
        return listAccessiblePaperRolls(diagram).size();
    }

    private int countAdjacentRolls(char[][] diagram, int row, int column) {
        final var minRow = Math.max(row - 1, 0);
        final var maxRow = Math.min(row + 1, diagram.length - 1);

        final var minColumn = Math.max(column - 1, 0);
        final var maxColumn = Math.min(column + 1, diagram[minRow].length - 1);

        var count = 0;

        for (var y = minRow; y <= maxRow; y++) {
            for (var x = minColumn; x <= maxColumn; x++) {
                if (x == column && y == row) continue;

                if (diagram[y][x] == '@') {
                    count++;

                }
            }
        }

        return count;
    }

    record Position(int x, int y) {
    }
}
