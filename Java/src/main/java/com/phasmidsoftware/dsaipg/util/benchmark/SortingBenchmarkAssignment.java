package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.sort.elementary.HeapSort;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


public class SortingBenchmarkAssignment {

    public static void main(String[] args) throws IOException {

        Config config = Config.load(SortingBenchmarkAssignment.class);


        if (args.length == 0) {
            args = new String[]{"10000", "20000", "40000", "80000", "160000", "320000", "640000", "1280000", "2560000"};
        }


        for (String arg : args) {
            int n = Integer.parseInt(arg);
            System.out.println(">>> Array size = " + n);


            // instrument=true for testing factors
            // "instrument=false" for testing run time
            Config instrumentConfig   = config.copy("helper", "instrument", "true");
            Config noInstrumentConfig = config.copy("helper", "instrument", "false");


            runInstrumentation(n, instrumentConfig);
            runTiming(n, noInstrumentConfig);
        }
    }

    private static void runInstrumentation(int n, Config config) {
        System.out.println("[Instrumentation mode]");


        Integer[] original = createRandomArray(n);

        //  QuickSort Dual Pivot
        InstrumentedComparableHelper<Integer> quickHelper =
                new InstrumentedComparableHelper<>("QuickSortDualPivot", n, config);
        QuickSort_DualPivot<Integer> quicksort = new QuickSort_DualPivot<>(quickHelper);


        Integer[] qsCopy = Arrays.copyOf(original, original.length);
        quicksort.sort(qsCopy);
        printStats("QuickSortDualPivot", quickHelper);

        // MergeSort
        InstrumentedComparableHelper<Integer> mergeHelper =
                new InstrumentedComparableHelper<>("MergeSort", n, config);
        MergeSort<Integer> mergesort = new MergeSort<>(mergeHelper);

        Integer[] msCopy = Arrays.copyOf(original, original.length);
        mergesort.sort(msCopy);

        printStats("MergeSort       ", mergeHelper);

        // HeapSort
        InstrumentedComparableHelper<Integer> heapHelper =
                new InstrumentedComparableHelper<>("HeapSort", n, config);
        HeapSort<Integer> heapsort = new HeapSort<>(heapHelper);

        Integer[] hsCopy = Arrays.copyOf(original, original.length);
        heapsort.sort(hsCopy);

        printStats("HeapSort        ", heapHelper);
    }


    private static void runTiming(int n, Config config) {
        System.out.println("[Timing mode]");


        final Integer[] original = createRandomArray(n);

        //QuickSort Dual Pivot
        QuickSort_DualPivot<Integer> quicksort = new QuickSort_DualPivot<>("QuickSortDualPivot", n, 1, config);
        SorterBenchmark<Integer> quickBench = new SorterBenchmark<>(
                Integer.class,
                quicksort,
                original,
                /* nRuns = */ 10,
                new TimeLogger[]{
                        new TimeLogger("DualPivotQSort", null) {
                            @Override
                            public void log(String description, double time, int size) {
                                System.out.printf("%s (DualPivotQSort) average time: %.3f ms for n=%d\n",
                                        description, time, size);
                            }
                        }
                }
        );
        quickBench.run("QuickSortDualPivot", n);

        // MergeSort
        MergeSort<Integer> mergesort = new MergeSort<>(n, 1, config);
        SorterBenchmark<Integer> mergeBench = new SorterBenchmark<>(
                Integer.class,
                mergesort,
                original,
                /* nRuns = */ 10,
                new TimeLogger[]{
                        new TimeLogger("MergeSort", null) {
                            @Override
                            public void log(String description, double time, int size) {
                                System.out.printf("%s (MergeSort)       average time: %.3f ms for n=%d\n",
                                        description, time, size);
                            }
                        }
                }
        );
        mergeBench.run("MergeSort", n);

        // HeapSort
        HeapSort<Integer> heapsort = new HeapSort<>(n, 1, config);
        SorterBenchmark<Integer> heapBench = new SorterBenchmark<>(
                Integer.class,
                heapsort,
                original,
                /* nRuns = */ 10,
                new TimeLogger[]{
                        new TimeLogger("HeapSort", null) {
                            @Override
                            public void log(String description, double time, int size) {
                                System.out.printf("%s (HeapSort)        average time: %.3f ms for n=%d\n",
                                        description, time, size);
                            }
                        }
                }
        );
        heapBench.run("HeapSort", n);
    }


    private static Integer[] createRandomArray(int n) {
        Random rd = new Random(0);
        Integer[] result = new Integer[n];
        for (int i = 0; i < n; i++) {
            result[i] = rd.nextInt();
        }
        return result;
    }


    private static void printStats(String sorterName, InstrumentedComparableHelper<Integer> helper) {
        System.out.printf("%s => Compares: %d, Swaps: %d, Copies: %d, Hits: %d\n",
                sorterName,
                helper.getCompares(),
                helper.getSwaps(),
                helper.getCopies(),
                helper.getHits()
        );
    }
}