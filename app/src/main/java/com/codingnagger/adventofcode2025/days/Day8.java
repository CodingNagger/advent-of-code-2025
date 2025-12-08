package com.codingnagger.adventofcode2025.days;

import java.util.*;

public class Day8 implements Day {
    private int shortestConnectionsToProcess;

    public Day8(int shortestConnectionsToProcess) {
        this.shortestConnectionsToProcess = shortestConnectionsToProcess;
    }

    @Override
    public String partOne(List<String> input) {
        final var junctionBoxes = input.stream()
                .filter(JunctionBox::canParse)
                .map(JunctionBox::parse)
                .toList();

        final var sortedConnections = connectionsSortedByShortestDistances(junctionBoxes);
        final var circuits = createCircuits(sortedConnections);

        final var circuitsWithSize = circuits.keySet().stream().map(
                        id -> new CircuitSize(id, circuits.get(id).size())
                )
                .collect(
                        () -> new PriorityQueue<>(Comparator.comparingInt((CircuitSize s) -> -s.size)),
                        PriorityQueue::add,
                        AbstractQueue::addAll
                );

        final var first = circuitsWithSize.poll();
        final var second = circuitsWithSize.poll();
        final var third = circuitsWithSize.poll();

        return "" + (first.size * second.size * third.size);
    }

    @Override
    public String partTwo(List<String> input) {
        final var junctionBoxes = input.stream()
                .filter(JunctionBox::canParse)
                .map(JunctionBox::parse)
                .toList();

        final var sortedConnections = connectionsSortedByShortestDistances(junctionBoxes);
        final var finalLinkingConnection = createOneLargeCircuitAndReturnFinalConnection(junctionBoxes, sortedConnections);
        return "" + (finalLinkingConnection.start.x * finalLinkingConnection.end.x);
    }


    private HashMap<Integer, List<JunctionBox>> createCircuits(PriorityQueue<Connection> sortedConnections) {
        var circuitsCount = 0;
        final var circuits = new HashMap<Integer, List<JunctionBox>>();
        final var connectionCircuit = new HashMap<JunctionBox, Integer>();

        int processedConnections = 0;

        while (!sortedConnections.isEmpty() && processedConnections < this.shortestConnectionsToProcess) {
            final var connection = sortedConnections.poll();
            final var start = connection.start();
            final var end = connection.end();

            if (connectionCircuit.containsKey(start)) {
                final var circuitId = connectionCircuit.get(start);

                if (connectionCircuit.containsKey(end)) {
                    final var otherCircuitId = connectionCircuit.get(end);

                    if (!Objects.equals(circuitId, otherCircuitId)) {
                        for (var junctionBox : circuits.get(otherCircuitId)) {
                            circuits.get(circuitId).add(junctionBox);
                            connectionCircuit.put(junctionBox, circuitId);
                        }

                        circuits.remove(otherCircuitId);
                    }
                } else {
                    circuits.get(circuitId).add(end);
                    connectionCircuit.put(end, circuitId);
                }
            } else if (connectionCircuit.containsKey(end)) {
                final var circuitId = connectionCircuit.get(end);

                circuits.get(circuitId).add(start);
                connectionCircuit.put(start, circuitId);
            } else {
                final var circuitId = ++circuitsCount;

                circuits.put(circuitId, new ArrayList<>());
                circuits.get(circuitId).add(start);
                circuits.get(circuitId).add(end);

                connectionCircuit.put(start, circuitId);
                connectionCircuit.put(end, circuitId);
            }

            processedConnections++;
        }

        return circuits;
    }

    private Connection createOneLargeCircuitAndReturnFinalConnection(List<JunctionBox> junctionBoxes, PriorityQueue<Connection> sortedConnections) {
        var circuitsCount = 0;
        final var circuits = new HashMap<Integer, List<JunctionBox>>();
        final var connectionCircuit = new HashMap<JunctionBox, Integer>();

        while (true) {
            final var connection = sortedConnections.poll();
            final var start = connection.start();
            final var end = connection.end();

            if (connectionCircuit.containsKey(start)) {
                final var circuitId = connectionCircuit.get(start);

                if (connectionCircuit.containsKey(end)) {
                    final var otherCircuitId = connectionCircuit.get(end);

                    if (!Objects.equals(circuitId, otherCircuitId)) {
                        for (var junctionBox : circuits.get(otherCircuitId)) {
                            circuits.get(circuitId).add(junctionBox);
                            connectionCircuit.put(junctionBox, circuitId);
                        }

                        circuits.remove(otherCircuitId);
                    }
                } else {
                    circuits.get(circuitId).add(end);
                    connectionCircuit.put(end, circuitId);
                }
            } else if (connectionCircuit.containsKey(end)) {
                final var circuitId = connectionCircuit.get(end);

                circuits.get(circuitId).add(start);
                connectionCircuit.put(start, circuitId);
            } else {
                final var circuitId = ++circuitsCount;

                circuits.put(circuitId, new ArrayList<>());
                circuits.get(circuitId).add(start);
                circuits.get(circuitId).add(end);

                connectionCircuit.put(start, circuitId);
                connectionCircuit.put(end, circuitId);
            }

            final var currentCircuitId = connectionCircuit.get(start);

            if (circuits.get(currentCircuitId).size() == junctionBoxes.size()) {
                return connection;
            }
        }
    }

    private static PriorityQueue<Connection> connectionsSortedByShortestDistances(List<JunctionBox> junctionBoxes) {
        final var potentialConnections = new HashSet<Connection>();

        for (var start : junctionBoxes) {
            for (var end : junctionBoxes) {
                if (start == end) continue;

                potentialConnections.add(new Connection(start, end, start.distanceTo(end)));
            }
        }

        final var sortedConnections = new PriorityQueue<>(Comparator.comparingDouble((Connection o) -> o.distance));
        sortedConnections.addAll(potentialConnections.stream().distinct().toList());
        return sortedConnections;
    }

    public void setShortestConnectionsToProcess(int shortestConnectionsToProcess) {
        this.shortestConnectionsToProcess = shortestConnectionsToProcess;
    }

    record CircuitSize(int id, int size) {
    }

    record Connection(JunctionBox start, JunctionBox end, double distance) {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Connection that)) return false;
            if (Objects.equals(end, that.end) && Objects.equals(start, that.start))
                return true;

            return Objects.equals(end, that.start) && Objects.equals(start, that.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start.hashCode() * end.hashCode());
        }
    }

    record JunctionBox(long x, long y, long z) {
        static JunctionBox parse(String line) {
            final var parts = Arrays.stream(line.split(",")).mapToLong(Long::parseLong).toArray();

            return new JunctionBox(parts[0], parts[1], parts[2]);
        }

        static boolean canParse(String line) {
            return line.contains(",");
        }

        public double distanceTo(JunctionBox end) {
            return (Math.pow(x - end.x, 2) + Math.pow(y - end.y, 2) + Math.pow(z - end.z, 2));
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", x, y, z);
        }
    }
}
