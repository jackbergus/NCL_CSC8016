package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import uk.ncl.CSC8016.jackbergus.slides.semaphores.BinarySemaphore;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Scheduler<T> {

    private boolean doStop;
    BinarySemaphore tick = new BinarySemaphore(0);
    BinarySemaphore[] start;
    List<Thread> tasks;

    public Scheduler(Task... n) {
        assert (n != null);
        start = IntStream.range(0, n.length)
                         .mapToObj(i -> new BinarySemaphore(0))
                         .toArray(BinarySemaphore[]:: new);
        this.doStop = false;
        tasks = Arrays.stream(n).map(x -> {
            var t =  new Thread(x);
            t.run();
            return t;
        }).collect(Collectors.toList());
    }

    public synchronized void join() {
        if (!doStop) {
            for (var x : tasks) {
                try {
                    x.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stop();
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
                    Thread.sleep(1000);
                    tick.release();
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
        public Task(int taskId, T task, List<? extends Function<T, T>> tasks) {
            assert start.length < taskId;
            assert tasks != null;
            this.taskId = taskId;
            toRun = new TaskList<T>(task, tasks);
        }

        @Override
        public void run() {
            while ((!doStop) && (!doLocalStop) && (toRun.hasNext())) {
                try {
                    start[taskId].acquire();
                    toRun.next();
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
