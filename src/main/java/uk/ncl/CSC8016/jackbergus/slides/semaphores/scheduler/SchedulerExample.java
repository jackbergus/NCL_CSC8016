package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SchedulerExample {

    public static void main(String args[]) throws InterruptedException {
        var adder = IntStream
                .range(0, 5)   // Given a range of numbers (integer), from 0 to 4, 4 included
                               // I want to add (integer) to the previously considered number, (x)
                .mapToObj((x) -> (Function<Integer, Integer>) integer -> {
                    System.out.println(integer+" -> "+x+"+"+integer+" = "+(x+integer));
                    return x + integer;
                })             // Then, these incrementing instructions are defined as a collection of operations
                .collect(Collectors.<Function<Integer,Integer>>toList());
        /*
        The previous block of code is equivalent to the following one:

        var adder = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            int i = j;
            Function<Integer, Integer> integerIntegerFunction = integer -> {
                System.out.println(integer + " -> " + i + "+" + integer + " = " + (i + integer));
                return i + integer;
            };
            adder.add(integerIntegerFunction);
        }
         */



        var multiplier = IntStream
                .range(1, 9)   // Given a range of numbers (integer), from 1 to 8, 8 included
                               // I want to multiply (integer) by the previously considered number, (x)
                .mapToObj((x) -> (Function<Integer, Integer>) integer -> {
                    System.out.println(integer+" -> "+x+"*"+integer+" = "+(x*integer));
                    return x * integer;
                })
                .collect(Collectors.<Function<Integer,Integer>>toList());
        /*
        Similarly to the previous line, the multiplier can be also defined as follows:

        var multiplier = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            int x = i;
            Function<Integer, Integer> integerIntegerFunction = integer -> {
                System.out.println(integer + " -> " + x + "*" + integer + " = " + (x * integer));
                return x * integer;
            };
            multiplier.add(integerIntegerFunction);
        }
         */

        // A scheduler should consider a scheduling tick after each second. Furthermore, the adder will start adding from 0, and the multiply from 1
        var scheduler = new Scheduler<>(1000, new Pair<>(0, adder), new Pair<>(1, multiplier));
        // Setting the delay with which each task is going to be scheduled
        // The adder will run every second, while the multiplier will run every 3 seconds.
        scheduler.start(new int[]{1,3});
        // Joining the threads and, as soon as everyone finishes, stops the scheduler and the ticker all-together
        scheduler.join();
    }

}
