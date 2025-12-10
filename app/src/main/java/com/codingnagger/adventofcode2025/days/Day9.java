package com.codingnagger.adventofcode2025.days;

import java.util.*;

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

        final var rectangles = new ArrayList<Rectangle>();

        for (var potentialCornerA : redTiles) {
            for (var potentialCornerB : redTiles) {
                if (potentialCornerA == potentialCornerB) continue;

                rectangles.add(Rectangle.create(potentialCornerA, potentialCornerB));
            }
        }

        final var expectedRedTileCountInRectangle = 2;
        final var flatRectangles = rectangles.stream()
                .filter(Rectangle::isFlat)
                .filter(r -> redTiles.stream().filter(r::contains).count() == expectedRedTileCountInRectangle)
                .toList();

        final var connectingGreenOrRedTiles = flatRectangles.stream()
                .flatMap(r -> r.allPositions().stream())
                .toList();

        // still need to fill connectingTileShape with other greens otherwise the 4 corners check fails
        final var filteredRectangles = rectangles.stream()
                .filter(r -> redTiles.stream().filter(r::containsCorner).count() == expectedRedTileCountInRectangle)
                .filter(r -> connectingGreenOrRedTiles.stream().filter(r::containsCorner).count() == 4)
                .toList();

        final var sortedRectangles = new PriorityQueue<Rectangle>(Comparator.comparingDouble(r -> -r.area()));
        sortedRectangles.addAll(filteredRectangles);

        return "" + sortedRectangles.poll().area();
    }

    private List<Position> findInnerGreenTiles(List<Position> connectingGreenOrRedTiles) {
        return null;
    }


    private record Rectangle(Position topLeft, Position bottomRight) {
        public long area() {
            final var topRight = getTopRight();
            final var bottomLeft = getBottomLeft();

            final var sideA = bottomRight.distanceTo(topRight);
            final var sideB = bottomRight.distanceTo(bottomLeft);

            return sideA * sideB;
        }

        private Position getBottomLeft() {
            return new Position(topLeft.x(), bottomRight.y());
        }

        private Position getTopRight() {
            return new Position(bottomRight.x(), topLeft.y());
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

        private boolean contains(Position position) {
            return topLeft.x() <= position.x() && topLeft.y() <= position.y()
                    && bottomRight.x() >= position.x() && bottomRight.y() >= position.y();
        }

        public boolean isFlat() {
            return topLeft.x() == bottomRight().x() || topLeft.y() == bottomRight().y();
        }

        public boolean containsCorner(Position position) {
            return topLeft().equals(position)
                    || bottomRight().equals(position)
                    || getTopRight().equals(position)
                    || getBottomLeft().equals(position);
        }

        public List<Position> allPositions() {
            final var positions = new ArrayList<Position>();

            for (var x = topLeft.x(); x <= bottomRight().x(); x++) {
                for (var y = topLeft.y(); y <= bottomRight().y(); y++) {
                    positions.add(new Position(x, y));
                }
            }

            return positions;
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
