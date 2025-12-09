package com.codingnagger.adventofcode2025.days;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var redTiles = input.stream().map(Position::parse).toList();
        final var rectangles = new PriorityQueue<Rectangle>(Comparator.comparingDouble(r -> -r.area()));

        for (var potentialCornerA : redTiles) {
            for (var potentialCornerB : redTiles) {
                if (potentialCornerA == potentialCornerB) continue;

                rectangles.add(Rectangle.create(potentialCornerA, potentialCornerB));
            }
        }

        return "" + rectangles.poll().area();
    }

    @Override
    public String partTwo(List<String> input) {
        final var redTiles = input.stream().map(Position::parse).toList();
        final var greenTiles = findGreenTiles(redTiles);
        final var greenOrRedTiles = Stream.concat(redTiles.stream(), greenTiles.stream()).collect(Collectors.toUnmodifiableSet());

        final var rectangles = new PriorityQueue<Rectangle>(Comparator.comparingDouble(r -> -r.area()));

        for (var potentialCornerA : redTiles) {
            for (var potentialCornerB : redTiles) {
                if (potentialCornerA == potentialCornerB) continue;

                final var rectangle = Rectangle.create(potentialCornerA, potentialCornerB);

                if (rectangle.isFilledWith(greenOrRedTiles)) {
                    rectangles.add(Rectangle.create(potentialCornerA, potentialCornerB));
                }
            }
        }

        return "" + rectangles.poll().area();
    }

    private List<Position> findGreenTiles(List<Position> redTiles) {
        final var connectingGreenTiles = findConnectingGreenTiles(redTiles);
        final var innerGreenTiles = findInnerGreenTiles(redTiles, connectingGreenTiles);

        final var result = new ArrayList<Position>();

        result.addAll(connectingGreenTiles);
        result.addAll(innerGreenTiles);

        return result;
    }

    private List<Position> findConnectingGreenTiles(List<Position> redTiles) {
        var cursor = redTiles.getFirst();
        var minX = cursor.x();
        var maxX = cursor.x();
        var minY = cursor.y();
        var maxY = cursor.y();

        for (var position : redTiles) {
            minX = Math.min(minX, position.x());
            maxX = Math.max(maxX, position.x());
            minY = Math.min(minY, position.y());
            maxY = Math.max(maxY, position.y());

            if (minX == position.x()) {
                cursor = position;
            }
        }

        final var queryableRedTiles = new HashSet<>(redTiles);
        final var greenTiles = new ArrayList<Position>();
        final var visitedRedTiles = new HashSet<Position>();

        exploration:
        while (visitedRedTiles.size() < redTiles.size()) {
            visitedRedTiles.add(cursor);

            for (var y = cursor.y() - 1; y >= minY; y--) {
                final var candidate = new Position(cursor.x(), y);

                if (candidate.equals(cursor)) continue;

                if (queryableRedTiles.contains(candidate)) {
                    for (var yy = candidate.y + 1; yy < cursor.y(); ++yy) {
                        greenTiles.add(new Position(cursor.x(), yy));
                    }

                    cursor = candidate;
                    continue exploration;
                }
            }

            for (var y = cursor.y() + 1; y <= minY; y++) {
                final var candidate = new Position(cursor.x(), y);

                if (candidate.equals(cursor)) continue;

                if (queryableRedTiles.contains(candidate)) {
                    for (var yy = candidate.y - 1; yy > cursor.y(); --yy) {
                        greenTiles.add(new Position(cursor.x(), yy));
                    }

                    cursor = candidate;
                    continue exploration;
                }
            }

            for (var x = cursor.x() - 1; x >= minX; x--) {
                final var candidate = new Position(x, cursor.y());

                if (candidate.equals(cursor)) continue;

                if (queryableRedTiles.contains(candidate)) {
                    for (var xx = candidate.x + 1; xx < cursor.x(); ++xx) {
                        greenTiles.add(new Position(xx, cursor.y()));
                    }

                    cursor = candidate;
                    continue exploration;
                }
            }

            for (var x = cursor.x() + 1; x <= maxX; x++) {
                final var candidate = new Position(x, cursor.y());

                if (candidate.equals(cursor)) continue;

                if (queryableRedTiles.contains(candidate)) {
                    for (var xx = candidate.x - 1; xx > cursor.x(); --xx) {
                        greenTiles.add(new Position(xx, cursor.y()));
                    }

                    cursor = candidate;
                    continue exploration;
                }
            }
        }

        return greenTiles;
    }


    private List<Position> findInnerGreenTiles(List<Position> redTiles, List<Position> connectingGreenTiles) {
        return null;
    }


    private record Rectangle(Position topLeft, Position bottomRight) {
        public long area() {
            final var topRight = new Position(bottomRight.x(), topLeft.y());
            final var bottomLeft = new Position(topLeft.x(), bottomRight.y());

            final var sideA = bottomRight.distanceTo(topRight);
            final var sideB = bottomRight.distanceTo(bottomLeft);

            return sideA * sideB;
        }

        static Rectangle create(Position a, Position b) {
            final var c = new Position(a.x(), b.y());
            final var d = new Position(b.x(), a.y());

            final var topLeft = determineTopLeft(a, b, c, d);
            final var bottomRight = determineBottomRight(a, b, c, d);

            return new Rectangle(topLeft, bottomRight);
        }

        private static Position determineTopLeft(Position... positions) {
            var result = positions[0];

            for (var i = 1; i < positions.length; i++) {
                final var candidate = positions[i];

                if (candidate.x() <= result.x() && candidate.y() <= result.y()) {
                    result = candidate;
                }
            }

            return result;
        }

        private static Position determineBottomRight(Position... positions) {
            var result = positions[0];

            for (var i = 1; i < positions.length; i++) {
                final var candidate = positions[i];

                if (candidate.x() >= result.x() && candidate.y() >= result.y()) {
                    result = candidate;
                }
            }

            return result;
        }

        public boolean isFilledWith(Set<Position> greenOrRedTiles) {
            final var containedTilesCount = greenOrRedTiles.stream().filter(this::contains).count();
            return area() == containedTilesCount;
        }

        private boolean contains(Position position) {
            return topLeft.x() <= position.x() && topLeft.y() <= position.y()
                    && bottomRight.x() >= position.x() && bottomRight.y() >= position.y();
        }
    }

    private record Position(long x, long y) {
        static Position parse(String line) {
            final var parts = Arrays.stream(line.split(",")).mapToLong(Long::parseLong).toArray();
            return new Position(parts[0], parts[1]);
        }

        public long distanceTo(Position other) {
            return (long) Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2)) + 1;
        }
    }
}
