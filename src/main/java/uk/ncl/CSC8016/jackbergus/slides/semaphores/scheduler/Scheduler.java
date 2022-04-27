package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import uk.ncl.CSC8016.jackbergus.slides.semaphores.BinarySemaphore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public Scheduler(Pair<T, List<Function<T, T>>>... n) {
        assert (n != null);
        start = IntStream.range(0, n.length)
                         .mapToObj(i -> new BinarySemaphore(0))
                         .toArray(BinarySemaphore[]:: new);
        this.doStop = false;
        tasks = IntStream.range(0, n.length)
                .mapToObj(i -> {
                    var t =  new Thread(new Task(i, n[i].key, n[i].value));
                    t.start();
                    return t;
                }).collect(Collectors.toList());
        clockThread = new Thread(new Clock());
    }

    public void start(int[] interval) {
        if (!(interval.length == tasks.size()))
            throw new RuntimeException("ERROR: the time intervals associated to each thread should be the same number as the threads!");
        int[] next = new int[interval.length];
        for (int i = 0; i<interval.length; i++) next[i] = interval[i];
        actualScheduler = new Thread(() -> {
            while (!doStop) {
                try {
                    tick.acquire();
                    System.out.println("Got a tick!");
                    for (int i = 0; i<tasks.size(); i++) {
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
        actualScheduler.start();
        clockThread.start();
    }

    public synchronized void join() {
        if (!doStop) {
            try {
                for (var x : tasks) {
                    x.join();
                }
                stop();
                System.out.println("Should stop now!");
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
                    Thread.sleep(1000);
                    System.out.println("tick!");
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
        public Task(int taskId, T task, List<Function<T, T>> tasks) {
            assert start.length < taskId;
            assert tasks != null;
            this.taskId = taskId;
            toRun = new TaskList<T>(task, tasks);
        }

        @Override
        public void run() {
            while ((!doStop) && (!doLocalStop) && (toRun.hasNext())) {
                try {
//                    System.out.println(this.taskId);
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
