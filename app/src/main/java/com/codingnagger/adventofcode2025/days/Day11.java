package com.codingnagger.adventofcode2025.days;

import java.util.*;

public class Day11 implements Day {
    @Override
    public String partOne(List<String> input) {
        final var graph = buildGraph(input);
        final var start = "you";
        final var exit = "out";

        final var visited = getPaths(start, exit, graph, List.of());

        return "" + visited.stream()
                .filter(path -> path.endsWith(exit))
                .distinct()
                .count();
    }

    @Override
    public String partTwo(List<String> input) {
        final var graph = buildGraph(input);
        final var start = "svr";
        final var dac = "dac";
        final var fft = "fft";
        final var exit = "out";

        // svr to dac
        final var svrToDac = getPaths(start, dac, graph, List.of(exit, fft)).stream().filter(p -> p.endsWith(dac)).findFirst().orElseThrow();
        // svr to fft
        final var svrToFft = getPaths(start, fft, graph, List.of(exit, dac)).stream().filter(p -> p.endsWith(fft)).findFirst().orElseThrow();

        // dac to fft
        final var svrToDacToFft = getPaths(svrToDac, fft, graph, List.of(exit, dac)).stream().filter(p -> p.endsWith(fft)).toList();
        // fft to dac
        final var svrToFftToDac = getPaths(svrToFft, dac, graph, List.of(exit, fft)).stream().filter(p -> p.endsWith(dac)).toList();

        // dac to out
        final var dacToExit = getPaths(dac, exit, graph, List.of(fft, dac)).stream().filter(p -> p.endsWith(exit)).toList();

        // fft to out
        final var fftToExit = getPaths(fft, exit, graph, List.of(fft, dac)).stream().filter(p -> p.endsWith(exit)).toList();

//        final var svrToDacToFftToExitCount = svrToDac.size() * dacToFft.size() * fftToExit.size();
//        final var svrToFftToDacToExitCount = svrToFft.size() * fftToDac.size() * dacToExit.size();

//        return "" + (svrToDacToFftToExitCount + svrToFftToDacToExitCount);
        return "";
    }

    private static HashSet<String> getPaths(String start, String exit, Map<String, List<String>> graph, List<String> exclusions) {
        final var visited = new HashSet<String>();
        final var queue = new PriorityQueue<String>(Comparator.comparingLong(s -> -s.length()));

        queue.add(start);

        while (!queue.isEmpty()) {
            final var current = queue.poll();

            if (visited.contains(current)) continue;

            visited.add(current);

            final var gates = current.split("-");
            final var currentGate = gates[gates.length - 1];

            if (exclusions.contains(currentGate)) continue;

            if (currentGate.equals(exit)) continue;

            final var potentialGates = graph.get(currentGate);

            if (potentialGates == null) continue;

            for (var gate : potentialGates) {
                if (visited.contains(gate)) continue;

                queue.add(current + "-" + gate);
            }
        }
        return visited;
    }

    private Map<String, List<String>> buildGraph(List<String> input) {
        final var result = new HashMap<String, List<String>>();

        for (var line : input) {
            final var parts = line.split(": ");

            result.put(parts[0], Arrays.asList(parts[1].split(" ")));
        }

        return result;
    }


}
