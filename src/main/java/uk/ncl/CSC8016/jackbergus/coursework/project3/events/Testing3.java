package uk.ncl.CSC8016.jackbergus.coursework.project3.events;

import au.com.dius.pact.provider.org.fusesource.jansi.HtmlAnsiOutputStream;
import org.jetbrains.annotations.NotNull;
import uk.ncl.CSC8016.jackbergus.coursework.project3.Blog;
import uk.ncl.CSC8016.jackbergus.coursework.project3.ReadWriteMonitorMultiRead;
import uk.ncl.CSC8016.jackbergus.coursework.project3.ThreadTopic;
import uk.ncl.CSC8016.jackbergus.coursework.project3.events.TopicUpdates;
import uk.ncl.CSC8016.jackbergus.coursework.project3.events.TopicUpdatesType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static uk.ncl.CSC8016.jackbergus.coursework.project3.events.TopicUpdatesType.QueryGetAllTopicNamesSortedByDate;

public class Testing3 {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static boolean isGlobal = true;
    public static double total_score = 0.0;
    public static double total_max_score = 0.0;

    public static @NotNull List<Message> testI_1() {
        var b = new Blog();
        List<Message> messages = new ArrayList<>();
        var test2 = testBooleanBlockingEvent(() -> b.removeTopicThreadById(0));
        if (!test2) {
            messages.add(new Message(true, "GOOD (1): you were not able to remove a blog thread as non-existant thread"));
        } else {
            messages.add(new Message(false, "ERROR (1): you should not have been able to remove a blog thread as non-existant thread"));
        }
        var test3 =  testBooleanBlockingEvent(() ->b.addPostToThreadId(0, "myUsername", "Post Content"));
        if (!test3) {
            messages.add(new Message(true, "GOOD (2): you were not able to post a comment over a non-existant blog thread"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should not have been able to add a comment on a non-existant thread"));
        }
        var test = testBlockingEvent(() ->b.getAllMessagesFromTopic(0));
        if (!test.isPresent()) {
            messages.add(new Message(true, "GOOD (3): you were not able to access the messages from a non-existant thread"));
            messages.add(new Message(true, "GOOD (4): So, the thread was not associated to a payload"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should not have been able to access a non-existant blog"));
            if ((test.get().getPayload() == null) || (!test.get().getPayload().isEmpty()) || (test.get().getLatestCommentId() >= 0)) {
                messages.add(new Message(false, "ERROR (3): furthermore, this content should have been null"));
            } else {
                messages.add(new Message(true, "GOOD (4): Nevertheless, no content was retrieved, which is good"));
            }
        }
        return messages;
    }

    public static @NotNull List<Message> testI_2() {
        var b = new Blog();
        List<Message> messages = new ArrayList<>();
        boolean hasBeenCreated =  testBooleanBlockingEvent(() ->b.createNewTopicThread("HELP ME!"));
        if (hasBeenCreated) {
            messages.add(new Message(true, "GOOD (1): you were to create a new topic!"));
        } else {
            messages.add(new Message(false, "ERROR (1): you should be able to create a new topic!"));
        }
        boolean hasMessage1Posted =  testBooleanBlockingEvent(() ->b.addPostToThreadId(0, "zqek42yi", "Use case scenarios"));
        if (hasMessage1Posted) {
            messages.add(new Message(true, "GOOD (2): user 'zqek42yi' posted the use case scenario"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should be able to add a message."));
        }
        boolean hasMessage2Posted =  testBooleanBlockingEvent(() -> b.addPostToThreadId(0, "anonymous", "Fulma fantomskribo"));
        if (hasMessage2Posted) {
            messages.add(new Message(true, "GOOD (3): user 'anonymous' posted as a fantomskribo"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should be able to add a message."));
        }
        boolean hasBeenCreated2 =  testBooleanBlockingEvent(() ->b.createNewTopicThread("HELP ME!"));
        if (hasBeenCreated2) {
            messages.add(new Message(true, "GOOD (4): you were to create a new topic with the same name but at a different time!"));
        } else {
            messages.add(new Message(false, "ERROR (4): you should be able to create a new topic with the same name!"));
        }
        boolean hasMessage4Posted =  testBooleanBlockingEvent(() ->b.addPostToThreadId(1, "anonymouse", "Umbane"));
        if (hasMessage4Posted) {
            messages.add(new Message(true, "GOOD (5): user 'anonymouse' posted an Umbane"));
        } else {
            messages.add(new Message(false, "ERROR (5): you should be able to add a message."));
        }
        var test = testBlockingEvent(() ->b.getAllMessagesFromTopic(0));
        if (test.isPresent()) {
            messages.add(new Message(true, "GOOD (6): you were  able to access the recently-created thread"));
            if (test.get().getPayload().equals("From: zqek42yi\n" +
                    "Text: Use case scenarios\n" +
                    "\n" +
                    "From: anonymous\n" +
                    "Text: Fulma fantomskribo")) {
                messages.add(new Message(true, "GOOD (7): you can visualize the correct message"));
            }
        } else {
            messages.add(new Message(false, "ERROR (6): you should  have been able to access a existant thread."));
            messages.add(new Message(false, "ERROR (7): you were not able to correctly visualize the two messages"));
        }
        var test2 =testBlockingEvent(() -> b.getAllMessagesFromTopic(1));
        if (test2.isPresent()) {
            messages.add(new Message(true, "GOOD (8): you were  able to access the recently-created thread"));
            if (test2.get().getPayload().equals("From: anonymouse\n" +
                    "Text: Umbane")) {
                messages.add(new Message(true, "GOOD (9): you can visualize the correct message"));
            } else {
                messages.add(new Message(false, "ERROR (9): you were not able to correctly visualize the two messages"));
            }
        } else {
            messages.add(new Message(false, "ERROR (8): you should  have been able to access a existant thread."));
            messages.add(new Message(false, "ERROR (9): you were not able to correctly visualize the two messages"));
        }
        return messages;
    }

    public static @NotNull List<Message> testI_3() {
        List<Message> messages = new ArrayList<>();
        var b = new Blog();
        boolean test1 = testBooleanBlockingEvent(() ->b.removeTopicThreadById(0));
        if (!test1) {
            messages.add(new Message(true, "GOOD (1): you cannot remove a non-existant thread"));
        } else {
            messages.add(new Message(false, "ERROR (1): you should not have a positive outcome for attempting at removing a non-existant thread"));
        }
        testBlockingEvent(() -> {
            b.createNewTopicThread("HELP ME!");
            b.addPostToThreadId(0, "zqek42yi", "Use case scenarios");
            b.addPostToThreadId(0, "anonymous", "Fulma fantomskribo");
            return null;
        });
        boolean test2= testBooleanBlockingEvent(() ->b.removeTopicThreadById(0));
        if (test2) {
            messages.add(new Message(true, "GOOD (2): you can remove an existant thread"));
            boolean test3 = b.removeTopicThreadById(0);
            if (!test3) {
                messages.add(new Message(true, "GOOD (3): you cannot remove an existant thread twice"));
            } else {
                messages.add(new Message(false, "ERROR (3): apparently, you are not correctly handling the removal of the thread"));
            }
        } else {
            messages.add(new Message(false, "ERROR (2): you should not have a positive outcome for attempting at removing a non-existant thread... "));
            messages.add(new Message(false, "ERROR (3): ...this entails that any further attempt is ill-formed "));
        }
        var test3 = testBlockingEvent(() -> b.getAllMessagesFromTopic(0));
        if (!test3.isPresent()) {
            messages.add(new Message(true, "GOOD (4): you cannot acess the messages from a non-existant thread"));
        } else {
            messages.add(new Message(true, "ERROR (4): you cannot acess the messages from a non-existant thread"));
        }
        return messages;
    }

    public static @NotNull List<Message> testI_4() {
        List<Message> messages = new ArrayList<>();
        var b = new Blog();
        var hasBeenCreated1 = testBooleanBlockingEvent(() -> b.createNewTopicThread("HELP ME!"));
        if (hasBeenCreated1) {
            messages.add(new Message(true, "GOOD (1): you were to create a new topic!"));
        } else {
            messages.add(new Message(false, "ERROR (1): you should be able to create a new topic!"));
        }
        var test1 = testBlockingEvent(b::pollForUpdate);
        if ((test1.isPresent()) &&
                (test1.get().getThreadTopicName().equals("HELP ME!")) &&
                (test1.get().getThreadTopicID() == 0) &&
                (test1.get().getOperation() == TopicUpdatesType.NewTopicPublished) &&
                (test1.get().getLatestCommentId() == -1)) {
            messages.add(new Message(true, "GOOD (2): you were able to retrieve a new topic creation event!"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should be able to retrieve the generation of a new topic as an event!"));
        }
        var hasBeenCreated2 = testBooleanBlockingEvent(() -> b.createNewTopicThread("HELP ME NOW!"));
        var hasBeenCreated3 = testBooleanBlockingEvent(() -> b.createNewTopicThread("HELP ME ASAP!"));
        var test2 = testBlockingEvent(b::pollForUpdate);
        if ((test1.isPresent()) &&
                (test2.get().getThreadTopicName().equals("HELP ME ASAP!")) &&
                (test2.get().getThreadTopicID() == 2) &&
                (test2.get().getOperation() == TopicUpdatesType.NewTopicPublished)  &&
                (test2.get().getLatestCommentId() == -1)) {
            messages.add(new Message(true, "GOOD (3): you were able to retrieve only the newest new topic creation event!"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should be able to retrieve the generation of the newest topic!"));
        }
        var test3 = testBooleanBlockingEvent(() -> b.addPostToThreadId(0, "nicko", "First Message"));
        var test4 = testBooleanBlockingEvent(() -> b.addPostToThreadId(3, "nicko", "Second Message"));
        if (test3 && (!test4)) {
            messages.add(new Message(true, "GOOD (4): correctly handling the creation of a new event!"));
        } else {
            messages.add(new Message(false, "ERROR (4): Please refer to the previous tests for returning an event!"));
        }
        var test5 = testBlockingEvent(b::pollForUpdate);
        if ((test5.isPresent()) &&
                (test5.get().getOperation() == TopicUpdatesType.NewCommentPublishedInToipic) &&
                (test5.get().getThreadTopicID() == 0) &&
                (test5.get().getLatestCommentId() == 0)) {
            messages.add(new Message(true, "GOOD (5): correctly handling the creation of a new event, also from the poll update standpoint!"));
        } else {
            messages.add(new Message(false, "ERROR (5): the polled event should be only able to refer to the latest occurring event!"));
        }
        var test6 = testBooleanBlockingEvent(() -> b.removeTopicThreadById(4));
        var test7 = testBooleanBlockingEvent(() -> b.removeTopicThreadById(1));
        var test8 = testBlockingEvent(b::pollForUpdate);
        if (test7 && (!test6)) {
            messages.add(new Message(true, "GOOD (6): correctly handling the deletion of an available blog thread!"));
        } else {
            messages.add(new Message(false, "ERROR (6): Please refer to the previous tests for returning an event!"));
        }
        if ((test8.isPresent()) &&
                (test8.get().getOperation() == TopicUpdatesType.TopicDeleted) &&
                (test8.get().getThreadTopicID() == 1) &&
                (test8.get().getLatestCommentId() == -1) &&
                (test8.get().getThreadTopicName().equals("HELP ME NOW!")) ) {
            messages.add(new Message(true, "GOOD (7): correctly handling the deletion of a thread, also from the poll update standpoint!"));
        } else {
            messages.add(new Message(false, "ERROR (7): the removed event should be handled also correctly!"));
        }
        return messages;
    }

    public static @NotNull List<Message> testI_5() {
        List<Message> messages = new ArrayList<>();
        var b = new Blog();
        FutureTask<TopicUpdates> ft = new FutureTask<>(b::pollForUpdate);
        executor.submit(ft);
        if (testDoesNotTimeOut(ft, 1000)) {
            messages.add(new Message(false, "ERROR (1): cannot obtain a result if no event is scheduled!"));
        } else {
            messages.add(new Message(true, "GOOD (1): you should be busy waiting for the event!"));
        }
        var hasBeenCreated1 = testBooleanBlockingEvent(() -> b.createNewTopicThread("HELP ME!"));
        var result = testDoesNotTimeOut(ft, 1000);
        if (hasBeenCreated1) {
            messages.add(new Message(true, "GOOD (2): you were to create a new topic!"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should be able to create a new topic!"));
        }
        if (result) {
            messages.add(new Message(true, "GOOD (3): you immediately returned after the event was sent!"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should be able to immediately retrieve the event!"));
        }
        return messages;
    }

    public static @NotNull List<Message> testI_6() {
        List<Message> messages = new ArrayList<>();
        ReadWriteMonitorMultiRead<String> basicHandler = new ReadWriteMonitorMultiRead<>();
        if (testBooleanBlockingEvent(() -> !basicHandler.set(null))) {
            messages.add(new Message(true, "GOOD (1): you returned null and the code converged"));
        } else {
            messages.add(new Message(false, "ERROR (1): the calling method should neither return a non-null result, nor block!"));
        }
        if (testBooleanBlockingEvent(() -> basicHandler.set(() -> "Hello World"))) {
            messages.add(new Message(true, "GOOD (2): you returned the accomplishment for setting the value"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should have returned a value determining the accomplishment of the project!"));
        }
        var result = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result.isPresent() && result.get().equals("Hello World")) {
            messages.add(new Message(true, "GOOD (3): you satisfactorily returned the set value"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should either have returned something, or the value should have matched the one being inserted"));
        }
        var result2 = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result2.isPresent() && result2.get().equals("Hello World")) {
            messages.add(new Message(true, "GOOD (4): you satisfactorily returned the set value for a second time"));
        } else {
            messages.add(new Message(false, "ERROR (4): you should either have returned something for a second time, or the value should have matched the one being inserted"));
        }
        var resultBis = testBlockingEvent(() -> basicHandler.get(null, "Hello World"));
        if (resultBis.isEmpty()) {
            messages.add(new Message(true, "GOOD (5): you were satisfactorily polling waiting for a new value to be set"));
        } else {
            messages.add(new Message(false, "ERROR (5): you be now waiting to retrieve a new value, and not just the same that you already retrieved"));
        }
        if (testBooleanBlockingEvent(() -> basicHandler.set(() -> "New World"))) {
            messages.add(new Message(true, "GOOD (6): you returned the accomplishment for setting the value"));
        } else {
            messages.add(new Message(false, "ERROR (6): you should have returned a value determining the accomplishment of the project!"));
        }
        if (testBooleanBlockingEvent(() -> basicHandler.set(() -> "Brave New World"))) {
            messages.add(new Message(true, "GOOD (7): you returned the accomplishment for setting the value"));
        } else {
            messages.add(new Message(false, "ERROR (7): you should have returned a value determining the accomplishment of the project!"));
        }
        var result3 = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result3.isPresent() && result3.get().equals("Brave New World")) {
            messages.add(new Message(true, "GOOD (8): you satisfactorily returned the latest set value for a second time"));
        } else {
            messages.add(new Message(false, "ERROR (8): you should either have returned something for a second time, or the value should have matched the one being last inserted"));
        }
        var result4 = testBlockingEvent(() -> basicHandler.get(null, "Hello World"));
        if (result4.isPresent() && result4.get().equals("Brave New World")) {
            messages.add(new Message(true, "GOOD (9): you satisfactorily returned the latest set value for a second time"));
        } else {
            messages.add(new Message(false, "ERROR (9): you should either have returned something for a second time, or the value should have matched the one being last inserted"));
        }
        var resultTer = testBlockingEvent(() -> basicHandler.get(null, "Brave New World"));
        if (resultBis.isEmpty()) {
            messages.add(new Message(true, "GOOD (10): you were satisfactorily polling waiting for a new value to be set"));
        } else {
            messages.add(new Message(false, "ERROR (10): you be now waiting to retrieve a new value, and not just the same that you already retrieved"));
        }
        return messages;
    }

    public static @NotNull List<Message> testI_7() {
        List<Message> messages = new ArrayList<>();
        ReadWriteMonitorMultiRead<String> basicHandler = new ReadWriteMonitorMultiRead<>();
        var result1 = testBlockingEvent(() -> basicHandler.get(() -> "Hellzapoppin'", null));
        if (result1.isPresent() && result1.get().equals("Hellzapoppin'")) {
            messages.add(new Message(true, "GOOD (1): you satisfactorily returned the supplier value rather than waiting for the latest buffered value"));
        } else {
            messages.add(new Message(false, "ERROR (1): you should have immediately returned the buffered event, and not waiting for one being provided"));
        }
        var result2 = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result2.isEmpty()) {
            messages.add(new Message(true, "GOOD (2): you were trying to poll the value, and nothing was returned, as no value was previously set"));
        } else {
            messages.add(new Message(false, "ERROR (2): you should have being waiting for the value produced by the thread invoking set beforehand"));
        }
        if (testBooleanBlockingEvent(() -> basicHandler.set(() -> "Brave New World"))) {
            messages.add(new Message(true, "GOOD (3): you returned the accomplishment for setting the value"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should have returned a value determining the accomplishment of the project!"));
        }
        var result3 = testBlockingEvent(() -> basicHandler.get(() -> "Hellzapoppin'", null));
        if (result3.isPresent() && result3.get().equals("Hellzapoppin'")) {
            messages.add(new Message(true, "GOOD (4): you satisfactorily returned the supplier value rather than waiting for the latest buffered value"));
        } else {
            messages.add(new Message(false, "ERROR (4): you should have immediately returned the buffered event, and not waiting for one being provided"));
        }
        if (testBooleanBlockingEvent(() -> basicHandler.set(() -> "Eat the Phikis"))) {
            messages.add(new Message(true, "GOOD (5): you returned the accomplishment for setting the value"));
        } else {
            messages.add(new Message(false, "ERROR (5): you should have returned a value determining the accomplishment of the project!"));
        }
        var result4 = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result4.isPresent() && result4.get().equals("Eat the Phikis")) {
            messages.add(new Message(true, "GOOD (6): you were trying to retrieve the value, and you satisfactorily returned the latest set value by the setter"));
        } else {
            messages.add(new Message(false, "ERROR (6): you should have being waiting for the value produced by the thread invoking set beforehand"));
        }
        return messages;
    }

    private static List<Message> testII_1() {
        return null;
    }

    private static List<Message> testII_2() {
        return null;
    }

    private static List<Message> testII_4() {
        return null;
    }

    private static List<Message> testII_3() {
        return null;
    }

    static HtmlAnsiOutputStream html = null;

    public static void noline(String line) {
        var x = line+System.lineSeparator();
        try {
            html.write(x.getBytes(StandardCharsets.UTF_8));
            html.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeln(String line) {
        var x = "<p>"+line+"</p>"+System.lineSeparator();
        try {
            html.write(x.getBytes(StandardCharsets.UTF_8));
            html.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) throws IOException {
        Consumer<String> toFile = Testing3::writeln;
        Consumer<String> toConsole = System.out::println;
        Consumer<String> currentConsumer = toConsole;

        Function<Boolean, List<Message>> none = null;
        List<Test> scoring = new ArrayList<>();
        String StudentId = Blog.studentID();
        FileOutputStream fos = new FileOutputStream(new File(StudentId+".html"));
        html = new HtmlAnsiOutputStream(fos);

        if (currentConsumer.hashCode() == toFile.hashCode())
            noline("<!DOCTYPE html><html><body>");
        currentConsumer.accept("StudentId: " + StudentId);
        currentConsumer.accept("I. Single Threaded Correctness");
        currentConsumer.accept("==============================");
        currentConsumer.accept("");
        scoring.add(new Test(Testing3::testI_1,
                "I cannot interact with a blog thread if this was not previously created.",
                4.0));
        scoring.add(new Test(Testing3::testI_2,
                "I can always interact with a topic thread that was previously created.",
                16.0));
        scoring.add(new Test(Testing3::testI_3,
                "I am correctly handling the thread closure.",
                4.0));
        scoring.add(new Test(Testing3::testI_4,
                "I am correctly handling the pollForUpdate method where, if successful requests are always fired before polling for events, should always return the most recent event available.",
                7.0));
        scoring.add(new Test(Testing3::testI_5,
                "I am handling the event update messages correctly.",
                3.0));
        scoring.add(new Test(Testing3::testI_6,
                "I am correctly handling the set method from ReadWriteMonitorMultiRead.",
                10.0));
        scoring.add(new Test(Testing3::testI_7,
                "I am correctly handling the get method from ReadWriteMonitorMultiRead.",
                6.0));

        FunctionScoring(scoring, currentConsumer);
        scoring.clear();

        currentConsumer.accept("");
        currentConsumer.accept("II. Multi-Threaded Correctness");
        currentConsumer.accept("==============================");
        currentConsumer.accept("");
        scoring.add(new Test(Testing3::testII_1,
                "Correctly handling the concurrent creation of different topics.",
                7.0));
        scoring.add(new Test(Testing3::testII_2,
                "Correctly handling the concurrent creation of different messages/posts within the same topic.",
                9.0));
        scoring.add(new Test(Testing3::testII_3,
                "The moderator is able to wait to receive 10 messages, after which the main thread and their posts are deleted",
                12.0));
        scoring.add(new Test(Testing3::testII_4,
                "While having only one single user running and one subscriber to receive the updates from the website, no interference occurs, and all the perceived events actually match the expected results",
                12.0));
        FunctionScoring(scoring, currentConsumer);
        scoring.clear();
        executor.shutdown();
        currentConsumer.accept("");
        currentConsumer.accept("[" + StudentId + "] Total Score: " + total_score + "/" + total_max_score + " = " + (total_score/total_max_score));
        if (currentConsumer.hashCode() == toFile.hashCode())
            noline("</body></html>");
        fos.close();
    }



    public static double sumUpOk(Collection<Message> msg) {
        if ((msg == null) || msg.isEmpty()) return 0.0;
        else {
            double N = msg.size();
            double OK = 0.0;
            for (var x : msg) if (x.isOK) OK++;
            return OK / N;
        }
    }

    public static void FunctionScoring(List<Test> scoring, Consumer<String> c) {
        for (var x : scoring) {
            double score = 0;
            int nMsg = 0;
            List<Message> result = null;
            if (x != null && x.test != null) {
                result = x.test.get();
                if (result != null) {
                    nMsg = result.size();
                    score = sumUpOk(result) * x.max_score;
                }
                total_max_score += x.max_score;
            }
            c.accept(" * " + x.name + "[#"+nMsg+"]. Score = "+score);
            if (result != null)
                for (var res : result)
                    c.accept("   - " + res);
            total_score += score;
        }
    }


    private static ExecutorService executor = Executors.newFixedThreadPool(100);

    public static <T> Optional<T> testBlockingEvent(Callable<T> r, long time) {
        FutureTask<T> futureTask = new FutureTask<>(r);
        executor.submit(futureTask);
        return testFuture(futureTask, time);
    }

    public static <T> Optional<T> testBlockingEvent(Callable<T> r) {
        FutureTask<T> futureTask = new FutureTask<>(r);
        executor.submit(futureTask);
        return testFuture(futureTask, 3000);
    }

    public static <T> Optional<T> testFuture(FutureTask<T> futureTask, long time) {
        try {
            T s = futureTask.get(time, TimeUnit.MILLISECONDS);
            if (s != null)
                return Optional.of(s);
            else
                return Optional.empty();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            futureTask.cancel(true);
            return Optional.empty();
        }
    }

    public static<T> boolean testDoesNotTimeOut(Callable<T> r) {
        FutureTask<T> futureTask = new FutureTask<>(r);
        executor.submit(futureTask);
        return testDoesNotTimeOut(futureTask, 1000);
    }

    public static<T> boolean testDoesNotTimeOut(Callable<T> r, long time) {
        FutureTask<T> futureTask = new FutureTask<>(r);
        executor.submit(futureTask);
        return testDoesNotTimeOut(futureTask, time);
    }

    public static <T> boolean testDoesNotTimeOut(FutureTask<T> futureTask, long time) {
        try {
            T s = futureTask.get(time, TimeUnit.MILLISECONDS);
            return s != null;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
    }

    public static boolean testBooleanBlockingEvent(Callable<Boolean> r) {
        var result = testBlockingEvent(r);
        return result.isPresent() ? result.get() : false;
    }


    public static class Test {
        public final Supplier<List<Message>> test;
        public final String name;
        public final double max_score;

        public Test(Supplier<List<Message>> test, String name, double max_score) {
            this.test = test;
            this.name = name;
            this.max_score = max_score;
        }
    }

    public static class Message implements Comparable<Message> {
        public final boolean isOK;
        public final String message;

        public Message(boolean isOK, String message) {
            this.isOK = isOK;
            this.message = message;
        }

        public String toString() {
            return (this.isOK ? ANSI_GREEN : ANSI_RED) + message + (ANSI_RESET);
        }

        @Override
        public int compareTo(@NotNull Testing3.Message o) {
            return message.compareTo(o.message);
        }
    }

}
