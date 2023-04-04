package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import uk.ncl.CSC8016.jackbergus.slides.semaphores.BinarySemaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Scheduler<T> {

    private boolean doStop;
    BinarySemaphore tick = new BinarySemaphore(0);
    BinarySemaphore[] start;
    List<Thread> tasks;
    Thread actualScheduler;
    Thread clockThread;
    int tick_time;

    public Scheduler(int tick_time_milliseconds, Pair<T, List<Function<T, T>>>... n) {
        assert (n != null);
        this.tick_time = tick_time_milliseconds;
        
        // New Java style: create an array of n.length BinarySemaphores, initialized to zero (events)
        start = IntStream.range(0, n.length)
                         .mapToObj(i -> new BinarySemaphore(0))
                         .toArray(BinarySemaphore[]:: new);
        /*

        The previous single-statement code is equivalent to this block of code:

        List<BinarySemaphore> list = new ArrayList<>();
        for (int i1 = 0; i1 < n.length; i1++) {
            BinarySemaphore binarySemaphore = new BinarySemaphore(0);
            list.add(binarySemaphore);
        }
        start = list.toArray(new BinarySemaphore[0]);

        */

        // Liveliness
        this.doStop = false;
        
        // For each task in the array of tasks n, generate a new thread and start it: this will wait for the scheduler to make them run!
        tasks = IntStream.range(0, n.length)
                .mapToObj(i -> {
                    var t =  new Thread(new Task(i, n[i].key, n[i].value));
                    t.start();
                    return t;
                }).collect(Collectors.toList());
        /*
        The previous single-statement code is equivalent to this block of code:

        tasks = new ArrayList<>();
        for (int i = 0; i<n.length; i++) {
            var t =  new Thread(new Task(i, n[i].key, n[i].value));
            t.start();
            tasks.add(t);
        }
         */
        
        // Creating, but not starting, the clock thread.
        clockThread = new Thread(new Clock());
    }

    public void start(int[] interval) {
        if (!(interval.length == tasks.size()))
            throw new RuntimeException("ERROR: the time intervals associated to each thread should be the same number as the threads!");
        
        // Ticks to be waited before scheduling the next task
        int[] next = new int[interval.length];
        // Initializing the ticks to be waited with the provided interval array
        for (int i = 0; i<interval.length; i++) next[i] = interval[i];
        // Definition of the scheduler, as in the slides
        actualScheduler = new Thread(() -> {
            while (!doStop) {
                try {
                    tick.acquire();
                    System.out.println("Got a tick!");
                    for (int i = 0; i<tasks.size(); i++) {
                        // I will need to schedule this activity only if the thread has not been stopped yet!
                        // Exercise: what does it happen if I remove this condition?
                        if (tasks.get(i).isAlive()) {
                            next[i]--;
                            if (next[i] == 0) {
                                next[i] = interval[i];
                                start[i].release();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        actualScheduler.start(); // Starting both the scheduler and the ticker.
        clockThread.start();
    }

    public synchronized void join() {
        if (!doStop) {
            try {
                // Waiting for all of the activities to finish
                for (var x : tasks) {
                    x.join();
                }
                // After they stop, I can stop the whole computation
                stop();
                System.out.println("Should stop now!");
                // Joining the two orchestrators!
                actualScheduler.join();
                clockThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        doStop = true;
    }

    public class Clock implements Runnable {
        @Override
        public void run() {
            while (!doStop) {
                try {
                    Thread.sleep(tick_time);
                    System.out.println("tick!");
                    tick.release(); // A new tick event is available
                } catch (InterruptedException e) {
                    doStop = true;
                    e.printStackTrace();
                }
            }
        }
        public void stop() {
            doStop = true;
        }
    }

    public class Task implements Runnable {
        private boolean doLocalStop = false;
        int taskId;
        TaskList<T> toRun;
        int count = 0;
        public Task(int taskId, T task, List<Function<T, T>> tasks) {
            assert start.length < taskId;
            assert tasks != null;
            this.taskId = taskId;
            toRun = new TaskList<>(task, tasks);
        }

        @Override
        public void run() {
            while ((!doStop) && (!doLocalStop) && (toRun.hasNext())) {
                try {
                    start[taskId].acquire(); // Waiting to be orchestrated by the scheduler
                    toRun.next();            // Performing the computation associated to the current step
                } catch (InterruptedException e) {
                    doLocalStop = true;
                    e.printStackTrace();
                }
            }
        }

        public T result() {
            return toRun.hasNext() ? null : toRun.next();
        }
    }

}
