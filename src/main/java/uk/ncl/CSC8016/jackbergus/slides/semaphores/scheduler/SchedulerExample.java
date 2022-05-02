package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SchedulerExample {

    public static void main(String args[]) throws InterruptedException {
        var adder = IntStream
                .range(0, 5)
                .mapToObj((x) -> (Function<Integer, Integer>) previous_value -> {
                    System.out.println(previous_value+" -> "+x+"+"+previous_value+" = "+(x+previous_value));
                    return x + previous_value;
                })
                .collect(Collectors.<Function<Integer,Integer>>toList());
        var multiplier = IntStream
                .range(1, 9)
                .mapToObj((x) -> (Function<Integer, Integer>) integer -> {
                    System.out.println(integer+" -> "+x+"*"+integer+" = "+(x*integer));
                    return x * integer;
                })
                .collect(Collectors.<Function<Integer,Integer>>toList());

        var scheduler = new Scheduler<>(1000, new Pair<>(0, adder), new Pair<>(1, multiplier));
        scheduler.start(new int[]{1,3});
        scheduler.join();

    }

}
