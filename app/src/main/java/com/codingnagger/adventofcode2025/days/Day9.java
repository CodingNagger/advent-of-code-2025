package com.codingnagger.adventofcode2025.days;

import java.util.*;
import java.util.stream.Collectors;

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

        final var distinctRectangles = rectangles.stream().distinct().toList();

        final var expectedRedTileCountInRectangle = 2;
        final var flatRectangles = distinctRectangles.stream()
                .filter(Rectangle::isFlat)
                .filter(r -> redTiles.stream().filter(r::contains).count() == expectedRedTileCountInRectangle)
                .toList();

        final var connectingGreenOrRedTiles = flatRectangles.stream()
                .flatMap(r -> r.allPositions().stream())
                .collect(Collectors.toUnmodifiableSet());

        final var topLeft = new Position(
                redTiles.stream().mapToLong(Position::x).min().orElseThrow(),
                redTiles.stream().mapToLong(Position::y).min().orElseThrow()
        );
        final var bottomRight = new Position(
                redTiles.stream().mapToLong(Position::x).max().orElseThrow(),
                redTiles.stream().mapToLong(Position::y).max().orElseThrow()
        );

        var maxArea = 0L;
        final var sortedRectangles = new PriorityQueue<Rectangle>(Comparator.comparingDouble(r -> -r.area()));
        sortedRectangles.addAll(distinctRectangles);

        final var sortedRectanglesResult = new PriorityQueue<Rectangle>(Comparator.comparingDouble(r -> -r.area()));

        for (var rectangle : sortedRectangles) {
            if (rectangle.area() < maxArea) continue;

            final var corners = rectangle.corners();

            if (
                    (!redTiles.contains(rectangle.topLeft()) || !redTiles.contains(rectangle.bottomRight()))
                            && (!redTiles.contains(rectangle.getTopRight()) || !redTiles.contains(rectangle.getBottomLeft()))
            ) {
                continue;
            }


            if (corners.stream().anyMatch(p -> !insidePolygon(topLeft, bottomRight, connectingGreenOrRedTiles, p)))
                continue;

            sortedRectanglesResult.add(rectangle);
            maxArea = rectangle.area();
        }

        return "" + sortedRectanglesResult.poll().area();
    }

    private boolean insidePolygon(Position topLeft, Position bottomRight, Set<Position> connectingGreenOrRedTiles, Position position) {
        final var outsideTopLeft = new Position(topLeft.x - 1, topLeft.y - 1);
        final var outsideBottomRight = new Position(bottomRight.x + 1, bottomRight.y + 1);

        final var tilesOnY = connectingGreenOrRedTiles.stream().filter(t -> t.y == position.y).collect(Collectors.toUnmodifiableSet());

        if (tilesOnY.contains(position)) {
            return true;
        }

        var countCrossingFromOutsideLeft = 0;

        var xLeft = outsideTopLeft.x;

        while (xLeft <= position.x) {
            while (!tilesOnY.contains(new Position(xLeft, position.y)) && xLeft < position.x) {
                xLeft++;
            }

            if (xLeft >= position.x) {
                break;
            }

            countCrossingFromOutsideLeft++;

            while (tilesOnY.contains(new Position(xLeft, position.y)) && xLeft < position.x) {
                xLeft++;
            }

            if (xLeft >= position.x) {
                break;
            }
        }

        if (countCrossingFromOutsideLeft % 2 == 0) {
            return false;
        }

        var countCrossingFromOutsideRight = 0;

        var xRight = outsideBottomRight.x;

        while (xRight >= position.x) {
            while (!tilesOnY.contains(new Position(xRight, position.y)) && xRight >= position.x) {
                xRight--;
            }

            if (xRight < position.x) {
                break;
            }

            countCrossingFromOutsideRight++;

            while (tilesOnY.contains(new Position(xRight, position.y)) && xRight >= position.x) {
                xRight--;
            }

            if (xRight < position.x) {
                break;
            }
        }

        if (countCrossingFromOutsideRight % 2 == 0) {
            return false;
        }

        var countCrossingFromOutsideTop = 0;

        var yTop = outsideTopLeft.y;
        final var tilesOnX = connectingGreenOrRedTiles.stream().filter(t -> t.x == position.x).collect(Collectors.toUnmodifiableSet());

        while (yTop <= position.y) {
            while (!tilesOnX.contains(new Position(position.x, yTop)) && yTop <= position.y) {
                yTop++;
            }

            if (yTop > position.y) {
                break;
            }

            countCrossingFromOutsideTop++;

            while (tilesOnX.contains(new Position(position.x, yTop)) && yTop <= position.y) {
                yTop++;
            }

            if (yTop > position.y) {
                break;
            }
        }

        if (countCrossingFromOutsideTop % 2 == 0) {
            return false;
        }

        var countCrossingFromOutsideBottom = 0;

        var yBottom = outsideBottomRight.y;

        while (yBottom >= position.y) {
            while (!tilesOnX.contains(new Position(position.x, yBottom)) && yBottom >= position.y) {
                yBottom--;
            }

            if (yBottom < position.y) {
                break;
            }

            countCrossingFromOutsideBottom++;

            while (tilesOnX.contains(new Position(position.x, yBottom)) && yBottom >= position.y) {
                yBottom--;
            }

            if (yBottom < position.y) {
                break;
            }
        }

        if (countCrossingFromOutsideBottom % 2 == 0) {
            return false;
        }

        return true;
    }


    private List<Position> findInnerGreenTiles(List<Position> redTiles, List<Position> connectingGreenOrRedTiles) {
        final var queryableGreenOrRedTiles = new HashSet<>(connectingGreenOrRedTiles);
        final var innerTiles = new HashSet<Position>();
        final var start = redTiles.stream()
                .filter(r -> queryableGreenOrRedTiles.contains(r.down()))
                .filter(r -> queryableGreenOrRedTiles.contains(r.right()))
                .filter(r -> !queryableGreenOrRedTiles.contains(r.up()))
                .filter(r -> !queryableGreenOrRedTiles.contains(r.left()))
                .map(r -> new Position(r.x + 1, r.y + 1))
                .findFirst()
                .orElseThrow();

        final Queue<Position> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            final var current = queue.poll();

            innerTiles.add(current);

            final var neighbors = List.of(current.up(), current.down(), current.left(), current.right());

            for (var neighbor : neighbors) {
                if (innerTiles.contains(neighbor)) continue;
                if (queryableGreenOrRedTiles.contains(neighbor)) continue;

                queue.add(neighbor);
            }
        }

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
            return corners().contains(position);
        }

        public List<Position> corners() {
            return List.of(topLeft(), bottomRight(), getTopRight(), getBottomLeft());
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

        public List<Position> allBorderPositions() {
            final var positions = new ArrayList<Position>();

            for (var x = topLeft.x(); x <= bottomRight().x(); x++) {
                positions.add(new Position(x, topLeft().y));
                positions.add(new Position(x, bottomRight().y));
            }

            for (var y = topLeft.y(); y <= bottomRight().y(); y++) {
                positions.add(new Position(topLeft().x, y));
                positions.add(new Position(bottomRight().x, y));
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

        public Position up() {
            return new Position(x, y - 1);
        }

        public Position left() {
            return new Position(x - 1, y);
        }

        public Position down() {
            return new Position(x, y + 1);
        }

        public Position right() {
            return new Position(x + 1, y);
        }
    }
}
