package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Random;
import java.util.function.Supplier;

public class InsertionSortBenchmarkTest {
    public static void main(String[] args) {
        int n = 1000;
        final int numSizes = 5;
        final int m = 10;

        for (int i = 0; i < numSizes; i++) {
            final int currentN = n;
            System.out.println("Array size n = " + currentN);

            runBenchmark(currentN, "Random", () -> getRandomArray(currentN), m);
            runBenchmark(currentN, "Ordered", () -> getOrderedArray(currentN), m);
            runBenchmark(currentN, "Partially-Ordered", () -> getPartiallyOrderedArray(currentN), m);
            runBenchmark(currentN, "Reverse-Ordered", () -> getReverseOrderedArray(currentN), m);
            System.out.println("--------------------------------------------------------");
            n *= 2;
        }
    }

    /**
     * Use Benchmark_Timer to test the running time of InsertionSortComparator
     *
     * 其中：
     * - preFunction uses arr.clone() to copy in order to make sure elements are "unordered" everytime.
     *
     * @param n        array size
     * @param ordering array ordering description
     * @param supplier return a new Integer[] everytime
     * @param m        how many times we want to run
     */
    private static void runBenchmark(int n, String ordering, Supplier<Integer[]> supplier, int m) {
        Benchmark_Timer<Integer[]> benchmark = new Benchmark_Timer<>(
                "InsertionSortComparator " + ordering,

                (Integer[] arr) -> arr.clone(),

                (Integer[] arr) -> { InsertionSortComparator.sort(arr); }
        );
        double avgTime = benchmark.runFromSupplier(supplier, m);
        System.out.printf("%-20s: %10.3f ms%n", ordering, avgTime);
    }



    /**
     * Generate a random array
     *
     * @param n array size
     * @return return an integer array with length n
     */
    private static Integer[] getRandomArray(int n) {
        Integer[] arr = new Integer[n];
        Random rand = new Random(42);
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(n);
        }
        return arr;
    }

    /**
     * Generate an ordered(ascending) array
     *
     * @param n array size
     * @return An ascending array：0, 1, 2, …, n-1
     */
    private static Integer[] getOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Generate a reverse ordered array
     *
     * @param n array size
     * @return A reversed array
     */
    private static Integer[] getReverseOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }

    /**
     * Generate a partially ordered array
     *
     * @param n array size
     * @return a partially ordered array
     */
    private static Integer[] getPartiallyOrderedArray(int n) {
        Integer[] arr = getOrderedArray(n);
        Random rand = new Random(6);
        int numberToSwaps = n / 10; // swap about 10% elements
        for (int i = 0; i < numberToSwaps; i++) {
            int idx1 = rand.nextInt(n);
            int idx2 = rand.nextInt(n);
            int temp = arr[idx1];

            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }
        return arr;
    }
}
