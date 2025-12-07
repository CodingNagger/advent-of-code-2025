package com.codingnagger.adventofcode2025.days;

import java.util.*;

public class Day7 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var diagram = TachyonManifold.parse(input);
        final var splitters = diagram.splitters();
        final var exit = diagram.exit();
        final var explored = new HashSet<Position>();

        final Deque<Position> stack = new LinkedList<>();
        stack.push(diagram.start());

        var result = 0;

        while (!stack.isEmpty()) {
            final var current = stack.poll();

            if (explored.contains(current)) {
                continue;
            }

            explored.add(current);

            if (splitters.contains(current)) {
                current.split().forEach(stack::push);
                result++;
            } else if (current.y() != exit) {
                stack.push(current.down());
            }
        }

        return "" + result;
    }

    @Override
    public String partTwo(List<String> input) {
        final var diagram = TachyonManifold.parse(input);
        final var splitters = diagram.splitters();
        final var exit = diagram.exit();

        return "" + countTimelines(diagram.start(), exit, splitters);
    }

    static Map<Position, Long> CACHE = new HashMap<>();

    private long countTimelines(Position current, int exit, Set<Position> splitters) {
        if (CACHE.containsKey(current)) {
            return CACHE.get(current);
        }

        long result;

        if (current.y() == exit) {
            result = 1;
        } else if (splitters.contains(current)) {
            result = countTimelines(current.left(), exit, splitters) + countTimelines(current.right(), exit, splitters);
        } else {
            result = countTimelines(current.down(), exit, splitters);
        }

        CACHE.put(current, result);

        return result;
    }

    private record TachyonManifold(Position start, Set<Position> splitters, int exit) {
        static TachyonManifold parse(List<String> input) {
            final var splitters = new HashSet<Position>();
            Position start = null;

            final var map = input.stream().map(String::toCharArray).toArray(char[][]::new);

            for (var y = 0; y < map.length; y++) {
                for (var x = 0; x < map[y].length; x++) {
                    if (map[y][x] == 'S') {
                        start = new Position(x, y);
                    } else if (map[y][x] == '^') {
                        splitters.add(new Position(x, y));
                    }
                }
            }

            return new TachyonManifold(start, splitters, map.length);
        }
    }

    private record Position(int x, int y) {
        Position down() {
            return new Position(x, y + 1);
        }

        public List<Position> split() {
            return List.of(left(), right());
        }

        private Position left() {
            return new Position(x - 1, y);
        }

        private Position right() {
            return new Position(x + 1, y);
        }
    }
}
