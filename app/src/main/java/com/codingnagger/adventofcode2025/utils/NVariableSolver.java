package com.codingnagger.adventofcode2025.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NVariableSolver { // Used Grok AI agent to generate this class to avoid using a library
    public static long[] findSmallestSumNonNegativeSolution(long[][] aug) {
        int rows = aug.length;
        int vars = aug[0].length - 1;

        // Use double matrix for exact elimination without integer truncation issues
        double[][] matrix = new double[rows][vars + 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= vars; j++) {
                matrix[i][j] = aug[i][j];
            }
        }

        List<Integer> freeVars = new ArrayList<>();
        int rank = gaussianEliminationDouble(matrix, freeVars);

        // Consistency check with tolerance
        for (int i = rank; i < rows; i++) {
            if (Math.abs(matrix[i][vars]) > 1e-8) {
                return null;
            }
        }

        int numFree = freeVars.size();

        long bestSum = Long.MAX_VALUE;
        long[] bestSol = null;

        long bound = 200;

        if (numFree == 0) {
            long[] cand = backSubstituteDouble(matrix, rank, freeVars, new long[0]);
            if (cand != null) {
                bestSol = cand;
            }
        } else if (numFree <= 3) {
            long[][] combos = generateCombinations(numFree, bound);
            for (long[] freeVals : combos) {
                long[] cand = backSubstituteDouble(matrix, rank, freeVars, freeVals);
                if (cand != null && allNonNegative(cand)) {
                    long s = sum(cand);
                    if (s < bestSum) {
                        bestSum = s;
                        bestSol = cand.clone();
                    }
                }
            }
        } else {
            System.out.println("Too many free variables for brute force.");
            return null;
        }

        return bestSol;
    }

    private static long[][] generateCombinations(int numFree, long bound) {
        long range = 2 * bound + 1;
        long size = 1;
        for (int i = 0; i < numFree; i++) size *= range;  // may be large, but ok for <=3
        long[][] combos = new long[(int) size][numFree];
        int idx = 0;

        long[] current = new long[numFree];
        Arrays.fill(current, -bound);

        while (true) {
            combos[idx++] = current.clone();

            int pos = numFree - 1;
            while (pos >= 0 && current[pos] == bound) {
                pos--;
            }
            if (pos < 0) break;

            current[pos]++;
            for (int j = pos + 1; j < numFree; j++) {
                current[j] = -bound;
            }
        }
        return Arrays.copyOf(combos, idx);
    }

    private static long[] backSubstituteDouble(double[][] echelon, int rank, List<Integer> freeVars, long[] freeValues) {
        int vars = echelon[0].length - 1;
        double[] sol = new double[vars];

        for (int i = 0; i < freeVars.size(); i++) {
            sol[freeVars.get(i)] = freeValues[i];
        }

        for (int r = rank - 1; r >= 0; r--) {
            int pc = -1;
            for (int c = 0; c < vars; c++) {
                if (Math.abs(echelon[r][c]) > 1e-10) {
                    pc = c;
                    break;
                }
            }
            if (pc == -1) continue;

            double sum = echelon[r][vars];
            for (int j = pc + 1; j < vars; j++) {
                sum -= echelon[r][j] * sol[j];
            }
            double piv = echelon[r][pc];
            sol[pc] = sum / piv;
        }

        // Convert to long, check if integer
        long[] intSol = new long[vars];
        for (int i = 0; i < vars; i++) {
            long rounded = Math.round(sol[i]);
            if (Math.abs(sol[i] - rounded) > 1e-8) {
                return null;
            }
            intSol[i] = rounded;
        }

        return intSol;
    }

    private static boolean allNonNegative(long[] arr) {
        for (long v : arr) {
            if (v < 0) return false;
        }
        return true;
    }

    private static long sum(long[] arr) {
        long s = 0;
        for (long v : arr) s += v;
        return s;
    }

    private static int gaussianEliminationDouble(double[][] m, List<Integer> freeVars) {
        int rows = m.length;
        int vars = m[0].length - 1;
        int rank = 0;
        boolean[] colUsed = new boolean[vars];

        for (int col = 0; col < vars && rank < rows; col++) {
            // Find best pivot (largest abs value for numerical stability)
            int pivot = rank;
            for (int i = rank + 1; i < rows; i++) {
                if (Math.abs(m[i][col]) > Math.abs(m[pivot][col])) {
                    pivot = i;
                }
            }
            if (Math.abs(m[pivot][col]) < 1e-10) continue;

            // Swap rows
            if (pivot != rank) {
                double[] temp = m[rank];
                m[rank] = m[pivot];
                m[pivot] = temp;
            }

            // Eliminate below
            double pivVal = m[rank][col];
            for (int i = rank + 1; i < rows; i++) {
                double factor = m[i][col] / pivVal;
                for (int j = col; j <= vars; j++) {
                    m[i][j] -= factor * m[rank][j];
                }
            }

            colUsed[col] = true;
            rank++;
        }

        for (int v = 0; v < vars; v++) {
            if (!colUsed[v]) freeVars.add(v);
        }

        return rank;
    }
}