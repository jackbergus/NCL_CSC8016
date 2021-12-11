package it.giacomobergami.CSC8016.coursework.wrong_example_test;

import it.giacomobergami.CSC8016.coursework.ClientSystemTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class ExampleDriverClass implements AutoCloseable {

    static Semaphore start = new Semaphore(0, true);
    static Semaphore sem = new Semaphore(0, true);
    private final String username;
    SimpleWrongSystem ss;
    List<Boolean> testServerStopWhenAlreadyNotRunning;
    List<Boolean> testWasServerStartedCorrectly;

    public ExampleDriverClass(String username) {
        this.username = username;
        ss = new SimpleWrongSystem();
        ss.createAccount(username);
        testServerStopWhenAlreadyNotRunning = new ArrayList<>();
        testWasServerStartedCorrectly = new ArrayList<>();
        testServerStopWhenAlreadyNotRunning.add(!ss.stopServer());
        testWasServerStartedCorrectly.add(ss.startServer());
        testWasServerStartedCorrectly.add(!ss.startServer());
    }

    public static void main(String[] args) throws InterruptedException {
        try (ExampleDriverClass ex = new ExampleDriverClass("hello")) {
            System.out.println("FIRST SCHEDULING");
            {
                Thread t1 = new Thread(ex.controllerThread());
                Thread t2 = new Thread(ex.generateThread1(200));
                Thread t3 = new Thread(ex.generateThread2(0));
                t1.start();
                t2.start();
                t3.start();
                t3.join();
                t2.join();
                t1.join();
            }


            System.out.println("SECOND SCHEDULING");
            {
                Thread t1 = new Thread(ex.controllerThread());
                Thread t2 = new Thread(ex.generateThread1(0));
                Thread t3 = new Thread(ex.generateThread2(200));
                t1.start();
                t2.start();
                t3.start();
                t3.join();
                t2.join();
                t1.join();
            }

            System.out.println(
                    ex.ss.openAccount("hello").getTotalAmount());

            System.out.println(
                    ex.ss.openAccount("hello").getLast10Operations().stream().map(x -> x.toString()).collect(Collectors.joining("\n")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Runnable generateThread1(long startAfter) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    start.acquire();
                    Thread.sleep(startAfter);
                    try (ClientSystemTransaction t = ss.openAccount(username)) {
                        System.out.println("Ok1");
                        t.withdrawMoney(20);
                        Thread.sleep(3000);
                        t.depositMoney(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Done1");
                        sem.release(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    Runnable generateThread2(long startAfter) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    start.acquire();
                    Thread.sleep(startAfter);
                    try (ClientSystemTransaction t = ss.openAccount(username)) {
                        System.out.println("Ok2");
                        t.depositMoney(100);
                        t.withdrawMoney(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Done2");
                        sem.release(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    Runnable controllerThread() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Releasing");
                    start.release(2);
                    System.out.println("Waiting completion");
                    sem.acquire(2);
                    System.out.println("Done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void close() throws Exception {
        testServerStopWhenAlreadyNotRunning.add(ss.stopServer());
        testServerStopWhenAlreadyNotRunning.add(!ss.stopServer());
        System.out.println("testServerStopWhenAlreadyNotRunning: " + testServerStopWhenAlreadyNotRunning.stream().filter(x -> x).count() + "/" + testServerStopWhenAlreadyNotRunning.size());
        System.out.println("testWasServerStartedCorrectly: " + testWasServerStartedCorrectly.stream().filter(x -> x).count() + "/" + testWasServerStartedCorrectly.size());
    }
}
