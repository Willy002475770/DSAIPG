package com.phasmidsoftware.dsaipg;

import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PQBenchmark {

    public static void main(String[] args) {

        Comparator<Integer> comparator = Integer::compareTo;
        final int capacity = 4095;
        final int insert_num = 16000;
        final int remove_num = 4000;

        int RUNS = 10;


        ArrayList<Integer> runtime = new ArrayList<>();
        ArrayList<Double> pq2 = new ArrayList<>();
        ArrayList<Double> pq2_floyd = new ArrayList<>();
        ArrayList<Double> pq4 = new ArrayList<>();
        ArrayList<Double> pq4_floyd = new ArrayList<>();


        while (RUNS < 10000) {

            RUNS *= 2;           // RUN TIME: 20, 40, 80, 160, 320, 640 ...
            runtime.add(RUNS);


            Consumer<PriorityQueue<Integer>> testLogic = pq -> {
                Random rand = new Random();
                for (int i = 0; i < insert_num; i++) {
                    pq.give(rand.nextInt(Integer.MAX_VALUE));
                }
                for (int i = 0; i < remove_num; i++) {
                    try {
                        pq.take();
                    } catch (PQException e) {
                        break;
                    }
                }
            };

            // (B) Basic Binary Heap (2-ary, no floyd)
            Supplier<PriorityQueue<Integer>> supBasic = () ->
                    new PriorityQueue<>(capacity, true, comparator, false);
            Benchmark_Timer<PriorityQueue<Integer>> benchBasic =
                    new Benchmark_Timer<>("PQ Basic Binary Heap", testLogic);
            double timeBasic = benchBasic.runFromSupplier_withRunTime(supBasic, 10, RUNS);
            pq2.add(timeBasic);

            // (C) 2-ary Floyd's Trick
            Supplier<PriorityQueue<Integer>> supFloyd = () ->
                    new PriorityQueue<>(capacity, true, comparator, true);
            Benchmark_Timer<PriorityQueue<Integer>> benchFloyd =
                    new Benchmark_Timer<>("PQ Floyd's Trick (2-ary)", testLogic);
            double timeFloyd = benchFloyd.runFromSupplier(supFloyd, RUNS);
            pq2_floyd.add(timeFloyd);

            // (D) 4-ary Heap (no floyd)
            Supplier<PriorityQueue4ary<Integer>> sup4ary = () ->
                    new PriorityQueue4ary<>(capacity, true, comparator, false);
            Consumer<PriorityQueue4ary<Integer>> test4ary = pq -> {
                Random rand = new Random();
                for (int i = 0; i < insert_num; i++) {
                    pq.give(rand.nextInt(Integer.MAX_VALUE));
                }
                for (int i = 0; i < remove_num; i++) {
                    try {
                        pq.take();
                    } catch (PQException e) {
                        break;
                    }
                }
            };
            Benchmark_Timer<PriorityQueue4ary<Integer>> bench4ary =
                    new Benchmark_Timer<>("PQ 4-ary Heap", test4ary);
            double time4ary = bench4ary.runFromSupplier(sup4ary, RUNS);
            pq4.add(time4ary);

            // 4-ary Floyd's Trick
            Supplier<PriorityQueue4ary<Integer>> supFloyd4ary = () ->
                    new PriorityQueue4ary<>(capacity, true, comparator, true);
            Benchmark_Timer<PriorityQueue4ary<Integer>> benchFloyd4ary =
                    new Benchmark_Timer<>("PQ 4-ary Floyd's Trick", test4ary);

            double timeFloyd4ary = benchFloyd4ary.runFromSupplier(supFloyd4ary, RUNS);
            pq4_floyd.add(timeFloyd4ary);
        }


        System.out.println("runtime (RUNS): " + runtime);
        System.out.println("PQ2 Basic (no floyd): " + pq2);
        System.out.println("PQ2 Floyd's Trick  : " + pq2_floyd);
        System.out.println("PQ4 (4-ary, no floyd): " + pq4);
        System.out.println("PQ4 Floyd's Trick     : " + pq4_floyd);
    }
}
