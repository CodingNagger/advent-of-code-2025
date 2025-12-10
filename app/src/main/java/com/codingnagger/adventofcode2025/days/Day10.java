package com.codingnagger.adventofcode2025.days;

import java.util.*;

public class Day10 implements Day {
    @Override
    public String partOne(List<String> input) {
        return "" + input.stream()
                .map(Machine::parse)
                .mapToLong(Machine::fewestTotalPressesForIndicatorLights)
                .sum();
    }

    @Override
    public String partTwo(List<String> input) {
        return "" + input.stream()
                .map(Machine::parse)
                .mapToLong(Machine::fewestTotalPressesForJoltage)
                .sum();
    }

    private record Machine(boolean[] indicatorLightsDiagram, List<Button> buttons, long[] joltages) {
        static Machine parse(String line) {
            final var indicatorAndRest = line.split("] \\(");
            final var buttonsAndJoltage = indicatorAndRest[1].split("\\) \\{");

            final var indicatorChars = indicatorAndRest[0].substring(1)
                    .toCharArray();

            final var indicatorLightsDiagram = buildIndicatorLightsDiagram(indicatorChars);
            final var buttons = Arrays.stream(buttonsAndJoltage[0].split("\\) \\("))
                    .map(Button::parse)
                    .toList();

            final var joltages = Arrays.stream(buttonsAndJoltage[1].substring(0, buttonsAndJoltage[1].length() - 1).split(","))
                    .mapToLong(Long::parseLong)
                    .toArray();

            return new Machine(indicatorLightsDiagram, buttons, joltages);
        }

        private static boolean[] buildIndicatorLightsDiagram(char[] indicatorChars) {
            final var result = new boolean[indicatorChars.length];

            for (int i = 0; i < indicatorChars.length; i++) {
                result[i] = indicatorChars[i] == '#';
            }

            return result;
        }

        public long fewestTotalPressesForIndicatorLights() {
            final var fewestPresses = new HashMap<String, Long>();
            final var queue = new PriorityQueue<Step>(Comparator.comparingLong(s -> -s.presses));

            queue.add(new Step(0, new boolean[indicatorLightsDiagram.length]));

            while (!queue.isEmpty()) {
                final var current = queue.poll();

                for (var b : buttons) {
                    final var nextPresses = current.presses + 1;
                    final var nextLights = b.press(current.lights);
                    final var nextKey = key(nextLights);

                    if (!fewestPresses.containsKey(nextKey) || fewestPresses.get(nextKey) > nextPresses) {
                        queue.add(new Step(nextPresses, nextLights));
                        fewestPresses.put(nextKey, nextPresses);
                    }
                }
            }

            return fewestPresses.get(key(indicatorLightsDiagram));
        }

        private String key(boolean[] lights) {
            return Arrays.toString(lights);
        }

        public long fewestTotalPressesForJoltage() {
            // bruteforce didn't work, need maths here
            final var fewestPresses = new HashMap<String, Long>();
            final var queue = new PriorityQueue<JoltageStep>(Comparator.comparingLong(s -> -s.presses));

            queue.add(new JoltageStep(0, new long[joltages.length]));

            while (!queue.isEmpty()) {
                final var current = queue.poll();

                for (var b : buttons) {
                    final var nextStep = b.maxiPress(current, joltages);
                    final var nextKey = key(nextStep.joltages);

                    if ((!fewestPresses.containsKey(nextKey) || fewestPresses.get(nextKey) > nextStep.presses)) {
                        queue.add(nextStep);
                        fewestPresses.put(nextKey, nextStep.presses);
                    }
                }
            }

            IO.println("Result for " + key(joltages));
            return fewestPresses.get(key(joltages));
        }

        private boolean canReachTarget(long[] nextJoltages) {
            for (var i = 0; i < joltages.length; i++) {
                if (nextJoltages[i] > joltages[i]) {
                    return false;
                }
            }

            return true;
        }

        private String key(long[] nextJoltages) {
            return Arrays.toString(nextJoltages);
        }
    }

    private record Button(List<Integer> toggleIndexes) {
        public static Button parse(String input) {
            return new Button(Arrays.stream(input.split(",")).map(Integer::parseInt).toList());
        }

        public boolean[] press(boolean[] currentIndicatorLights) {
            final var newLights = new boolean[currentIndicatorLights.length];
            for (var i = 0; i < currentIndicatorLights.length; i++) {
                newLights[i] = toggleIndexes.contains(i) != currentIndicatorLights[i];
            }
            return newLights;
        }

        public JoltageStep maxiPress(JoltageStep current, long[] targetJoltages) {
            var start = toggleIndexes.getFirst();
            var maxPresses = targetJoltages[start] - current.joltages[start];

            for (var index : toggleIndexes) {
                maxPresses = Math.min(maxPresses, targetJoltages[index] - current.joltages[index]);
            }

            final var newJoltages = new long[current.joltages.length];
            for (var i = 0; i < current.joltages.length; i++) {
                newJoltages[i] = current.joltages[i] + (toggleIndexes.contains(i) ? maxPresses : 0);
            }

            return new JoltageStep(current.presses + maxPresses, newJoltages);
        }
    }

    private record Step(long presses, boolean[] lights) {
        @Override
        public String toString() {
            return "presses: " + presses + " - " + Arrays.toString(lights);
        }
    }

    private record JoltageStep(long presses, long[] joltages) {
        @Override
        public String toString() {
            return "presses: " + presses + " - " + Arrays.toString(joltages);
        }
    }
}
