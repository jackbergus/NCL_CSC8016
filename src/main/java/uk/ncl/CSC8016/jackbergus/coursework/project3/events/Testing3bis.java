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

public class Testing3bis {

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
        if (!isPresentTest(test)) {
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
        if (isPresentTest(test)) {
            messages.add(new Message(true, "GOOD (6): you were  able to access the recently-created thread"));
            if (Objects.equals(test.get().getPayload(), "From: zqek42yi\n" +
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
        if (isPresentTest(test2)) {
            messages.add(new Message(true, "GOOD (8): you were  able to access the recently-created thread"));
            if (Objects.equals(test2.get().getPayload(),"From: anonymouse\n" +
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
        if (!isPresentTest(test3)) {
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
        if (result.isPresent() && Objects.equals(result.get(),"Hello World")) {
            messages.add(new Message(true, "GOOD (3): you satisfactorily returned the set value"));
        } else {
            messages.add(new Message(false, "ERROR (3): you should either have returned something, or the value should have matched the one being inserted"));
        }
        var result2 = testBlockingEvent(() -> basicHandler.get(null, null));
        if (result2.isPresent() && Objects.equals(result2.get(),"Hello World")) {
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
        if (result3.isPresent() && Objects.equals(result3.get(),"Brave New World")) {
            messages.add(new Message(true, "GOOD (8): you satisfactorily returned the latest set value for a second time"));
        } else {
            messages.add(new Message(false, "ERROR (8): you should either have returned something for a second time, or the value should have matched the one being last inserted"));
        }
        var result4 = testBlockingEvent(() -> basicHandler.get(null, "Hello World"));
        if (result4.isPresent() && Objects.equals(result4.get(),"Brave New World")) {
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
        if (result1.isPresent() && Objects.equals(result1.get(),"Hellzapoppin'")) {
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
        if (result3.isPresent() && Objects.equals(result3.get(),"Hellzapoppin'")) {
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
        if (result4.isPresent() && Objects.equals(result4.get(),"Eat the Phikis")) {
            messages.add(new Message(true, "GOOD (6): you were trying to retrieve the value, and you satisfactorily returned the latest set value by the setter"));
        } else {
            messages.add(new Message(false, "ERROR (6): you should have being waiting for the value produced by the thread invoking set beforehand"));
        }
        return messages;
    }

    private static List<Message> testII_1() {
        List<Message> messages = new ArrayList<>();
        var b = new Blog();
        HashSet<String> expectedTopics = new HashSet<>();
        Thread[] posterArray = new Thread[10];
        for (int i = 0; i<posterArray.length; i++) {
            int finalI = i;
            for (int j = 0; j<3; j++) {
                expectedTopics.add("Topic"+j+"."+ finalI);
            }
            posterArray[i] = new Thread(() -> {
                for (int j = 0; j<3; j++) {
                    b.createNewTopicThread("Topic"+j+"."+ finalI);
                }
            });
        }
        for (Thread t : posterArray) t.start();
        if (testBooleanBlockingEvent(() -> {
            for (Thread t : posterArray) t.join();
            return true;
        })) {
            messages.add(new Message(true, "GOOD (1): the posters did not block each other while attempting to open a new thread"));
        } else {
            messages.add(new Message(false, "ERROR (1): there were some deadlocks while attempting at multiple thread publications"));
        }
        var result1 = testBlockingEvent(b::getAllTopics);
        if (isPresentTest(result1)) {
            messages.add(new Message(true, "GOOD (2): the attempt at retrieving all the opened threads was fulfilled"));
            if (result1.get().getPayload() == null) {

                messages.add(new Message(false, "ERROR (3): you had to generate " +expectedTopics.size()+" topics, and not null!"));
            } else {
                Set<String> retrievedTopics = new HashSet<>(Set.of(result1.get().getPayload().split("\n")));
                if (retrievedTopics.size() == expectedTopics.size()) {
                    messages.add(new Message(true, "GOOD (3): you correctly generated " +expectedTopics.size()+" topics"));
                } else {
                    messages.add(new Message(false, "ERROR (3): you had to generate " +expectedTopics.size()+" topics, and not "+retrievedTopics.size()+"!"));
                }
                retrievedTopics.retainAll(expectedTopics);
                if (retrievedTopics.size() == expectedTopics.size())  {
                    messages.add(new Message(true, "GOOD (4): you correctly generated all the topics with the expected name, and no race conditions"));
                } else {
                    messages.add(new Message(false, "ERROR (4): you might have had some race conditions when generating the topic name"));
                }
            }
        } else {
            messages.add(new Message(false, "ERROR (2): you should have been able to retrieve the list without any waiting"));
            messages.add(new Message(false, "ERROR (3): ...So, there was no payload associated to the message"));
            messages.add(new Message(false, "ERROR (4): ...for which, we could not test whether you have race conditions when generating those"));
        }
        return messages;
    }


    private static List<Message> testII_2() {
        List<Message> messages = new ArrayList<>();
        var b = new Blog();
        HashSet<String> expectedTopics0 = new HashSet<>();
        HashSet<String> expectedTopics1 = new HashSet<>();
        if (testBooleanBlockingEvent(() -> b.createNewTopicThread("Topoi"))) {
            messages.add(new Message(true, "GOOD (1): it is possible to correctly generate a topic"));
        } else {
            messages.add(new Message(false, "ERROR (1): the topic-generation is ill-formed"));
        }
        if (testBooleanBlockingEvent(() -> b.createNewTopicThread("Topoii"))) {
            messages.add(new Message(true, "GOOD (1): it is possible to correctly generate a topic"));
        } else {
            messages.add(new Message(false, "ERROR (1): the topic-generation is ill-formed"));
        }
        Thread[] posterArray = new Thread[10];
        for (int i = 0; i<posterArray.length; i++) {
            int finalI = i;
            for (int j = 0; j<3; j++) {
                if ((j % 2) == 0)
                    expectedTopics0.add("From: "+"Nick"+finalI+"\nText: "+"Message"+j);
                else
                    expectedTopics1.add("From: "+"Nick"+finalI+"\nText: "+"Message"+j);
            }
            posterArray[i] = new Thread(() -> {
                for (int j = 0; j<3; j++) {
                    b.addPostToThreadId(j%2, "Nick"+finalI, "Message"+j);
                }
            });
        }
        for (Thread t : posterArray) t.start();
        if (testBooleanBlockingEvent(() -> {
            for (Thread t : posterArray) t.join();
            return true;
        })) {
            messages.add(new Message(true, "GOOD (1): the posters did not block each other while attempting to post on the same thread"));
        } else {
            messages.add(new Message(false, "ERROR (1): there were some deadlocks while attempting at multiple post publications"));
        }
        {
            var result1 = testBlockingEvent(() -> b.getAllMessagesFromTopic(0));
            if (isPresentTest(result1)) {
                messages.add(new Message(true, "GOOD (2): the attempt at retrieving all the opened messages was fulfilled"));
                Set<String> retrievedTopics = new HashSet<>(Set.of(result1.get().getPayload().split("\n\n")));
                if (retrievedTopics.size() == expectedTopics0.size()) {
                    messages.add(new Message(true, "GOOD (3): you correctly generated " +expectedTopics0.size()+" messages"));
                } else {
                    messages.add(new Message(false, "ERROR (3): you had to generate " +expectedTopics0.size()+" messages for the same thread, and not "+retrievedTopics.size()+"!"));
                }
                retrievedTopics.retainAll(expectedTopics0);
                if (retrievedTopics.size() == expectedTopics0.size())  {
                    messages.add(new Message(true, "GOOD (4): you correctly generated all the messages with the expected author and content, and no race conditions seemed to occur"));
                } else {
                    messages.add(new Message(false, "ERROR (4): you might have had some race conditions when generating the comments to the thread"));
                }
            } else {
                messages.add(new Message(false, "ERROR (2): you should have been able to retrieve the list without any waiting"));
                messages.add(new Message(false, "ERROR (3): ...So, there was no payload associated to the message"));
                messages.add(new Message(false, "ERROR (4): ...for which, we could not test whether you have race conditions when generating those"));
            }
        }
        {
            var result1 = testBlockingEvent(() -> b.getAllMessagesFromTopic(1));
            if (isPresentTest(result1)) {
                messages.add(new Message(true, "GOOD (2): the attempt at retrieving all the opened messages was fulfilled"));
                Set<String> retrievedTopics = new HashSet<>(Set.of(result1.get().getPayload().split("\n\n")));
                if (retrievedTopics.size() == expectedTopics1.size()) {
                    messages.add(new Message(true, "GOOD (3): you correctly generated " +expectedTopics1.size()+" messages"));
                } else {
                    messages.add(new Message(false, "ERROR (3): you had to generate " +expectedTopics1.size()+" messages for the same thread, and not "+retrievedTopics.size()+"!"));
                }
                retrievedTopics.retainAll(expectedTopics1);
                if (retrievedTopics.size() == expectedTopics1.size())  {
                    messages.add(new Message(true, "GOOD (4): you correctly generated all the messages with the expected author and content, and no race conditions seemed to occur"));
                } else {
                    messages.add(new Message(false, "ERROR (4): you might have had some race conditions when generating the comments to the thread"));
                }
            } else {
                messages.add(new Message(false, "ERROR (2): you should have been able to retrieve the list without any waiting"));
                messages.add(new Message(false, "ERROR (3): ...So, there was no payload associated to the message"));
                messages.add(new Message(false, "ERROR (4): ...for which, we could not test whether you have race conditions when generating those"));
            }
        }
        return messages;
    }

    private static List<Message> testII_4() {
        int NRuns = 3;
        Supplier<TopicUpdatesType> randomOperation = new Supplier<>() {
            Random PRNG = new Random(0);
            @Override
            public TopicUpdatesType get() {
                TopicUpdatesType[] directions = TopicUpdatesType.values();
                return directions[PRNG.nextInt(directions.length)];
            }
        };
        Supplier<String> randomString = new Supplier<>() {
            private static String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            Random rnd = new Random(0);

            @Override
            public String get() {
                StringBuilder salt = new StringBuilder();
                while (salt.length() < 18) { // length of the random string.
                    int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                    salt.append(SALTCHARS.charAt(index));
                }
                return salt.toString();
            }
        };
        ArrayList<Message> finalMessageList = new ArrayList<>();
        for (int run = 0; run<NRuns; run++) {
            ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<TopicUpdates> updates = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<TopicUpdates> expectedUpdates = new ConcurrentLinkedQueue<>();
            var b = new Blog();

            AtomicBoolean doContinuePolling = new AtomicBoolean(true);
            AtomicBoolean expectingMessage = new AtomicBoolean(true);

            Thread reader = new Thread(() -> {
                AtomicReference<TopicUpdates> reference = new AtomicReference<>(null);
                while (doContinuePolling.get()) {
                    var opt = testBlockingEvent(() -> b.pollForUpdate(reference.get()), 5000);
                    if (opt.isEmpty()) {
                        if (doContinuePolling.get() && expectingMessage.get())
                            messages.add(new Message(false, "The project was not able to retrieve the  event after 5 seconds: something wrong must happening... please double check the code"));
                        else
                            System.out.println("BREAK!");
                    } else {
                        var current = opt.get();
                        System.out.println("MESSAGE");
                        if (!Objects.equals(current, reference.get())) {
                            messages.add(new Message(true, "OK: the newly retrieved message refers to a completely new and fresh event!"));
                        } else {
                            messages.add(new Message(false, "There are some issues, as this message is completely equal to the previously provided one!"));
                        }
                        updates.add(current);
                        reference.set(current);
                    }
                }
            });

            Thread messager = new Thread(() -> {
                Random rndInt = new Random(0);
                HashMap<Integer, ThreadTopic> messagesSimulated = new HashMap<>();
                ArrayList<String> topicNames = new ArrayList<>();
                ArrayList<Integer> lastTopicId = new ArrayList<>();
                int topicId = 0;
                for (int actions=0; actions<10; actions++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    var newAction = randomOperation.get();
                    TopicUpdates expectedMessage = null;
                    System.out.println(newAction.name());
                    switch (newAction) {
                        case NewTopicPublished -> {
                            String topicName = randomString.get();
                            topicNames.add(topicName);
                            if (!testBooleanBlockingEvent( () -> b.createNewTopicThread(topicName))) {
                                messages.add(new Message(false, "Unexpected failure while attempting a creating a new topic: this should be at any means always possible"));
                                expectedMessage = null;
                            } else {
                                messagesSimulated.put(topicId, new ThreadTopic(topicName));
                                messages.add(new Message(true, "The topic was created successfully"));
                                expectedMessage = TopicUpdates.newTopic(topicName, topicId++);
                                lastTopicId.add(-1);
                            }
                        }

                        case NewCommentPublishedInToipic -> {
                            if (topicId == 0) {
                                if (!testBooleanBlockingEvent(() -> b.addPostToThreadId(0, "this", "impossible message"))) {
                                    messages.add(new Message(true, "Correctly failing at posting a message within an non-existing thread"));
                                    expectedMessage = null;
                                } else {
                                    messages.add(new Message(false, "You should not be able to post a message within a non-existing thread"));
                                    expectedMessage = TopicUpdates.newPost(null, 0, 0);
                                }
                            } else {
                                int currTopic = rndInt.nextInt(0, topicId);
                                int newMessageId =  lastTopicId.get(currTopic)+1;
                                lastTopicId.set(currTopic, newMessageId);
                                messagesSimulated.get(currTopic).addNewMessage("this", "Message #"+newMessageId);
                                if (!testBooleanBlockingEvent(() -> b.addPostToThreadId(currTopic, "this", "Message #"+newMessageId))) {
                                    messages.add(new Message(false, "You should be able to post a message within an existing topic thread"));
                                    expectedMessage = null;
                                } else {
                                    messages.add(new Message(true, "You successfully added a message to a topic thread"));
                                    expectedMessage = TopicUpdates.newPost(topicNames.get(currTopic), currTopic, newMessageId);
                                }
                            }
                        }

                        case TopicDeleted -> {
                            if (topicId == 0) {
                                if (!testBooleanBlockingEvent(() -> b.removeTopicThreadById(0))) {
                                    messages.add(new Message(true, "Correctly failing at removing an non-existing thread."));
                                    expectedMessage = null;
                                } else {
                                    messages.add(new Message(false, "You should not be able to delete a thread"));
                                    expectedMessage = TopicUpdates.newPost(null, 0, 0);
                                }
                            } else {
                                int currTopic = rndInt.nextInt(0, topicId);
                                messagesSimulated.remove(currTopic);
                                int newMessageId =  lastTopicId.get(currTopic)+1;
                                lastTopicId.set(currTopic, newMessageId);
                                if (!testBooleanBlockingEvent(() -> b.removeTopicThreadById(currTopic))) {
                                    messages.add(new Message(false, "You should be able to remove an existing thread."));
                                    expectedMessage = null;
                                } else {
                                    topicNames.set(topicId, null);
                                    lastTopicId.set(topicId, -2);
                                    messages.add(new Message(true, "You successfully deleted a topic thread"));
                                    expectedMessage = TopicUpdates.delTopic(topicNames.get(currTopic), currTopic);
                                }
                            }
                        }

                        case QueryGetAllTopicNamesSortedByDate -> {
                            var tmp = TopicUpdates.getAllTopicNamesSortedByFirstPublishedDate(topicNames.stream().filter(Objects::nonNull).collect(Collectors.toList()));
                            var opt = testBlockingEvent(b::getAllTopics);
                            if (opt.isEmpty()) {
                                messages.add(new Message(false, "You should have been able to return a list of topics, despide this being null."));
                                expectedMessage = null;
                            } else {
                                if (opt.get().equals(tmp)) {
                                    messages.add(new Message(true, "You correctly returned a list of topics sorted by date."));
                                } else {
                                    messages.add(new Message(false, "You correctly returned a list of topics sorted by date."));
                                }
                                expectedMessage = tmp;
                            }
                        }

                        case QueryGetAllTopicIDsSortedByDate -> {
                            var opt = testBlockingEvent(b::getAllTopicIDs);
                            List<Integer> idx = new ArrayList<>(lastTopicId.size());
                            for (int i = 0, N = lastTopicId.size(); i<N; i++) {
                                if (lastTopicId.get(i) >= -1) {
                                    idx.add(i);
                                }
                            }
                            var tmp = TopicUpdates.getAllTopicIDsSortedByFirstPublishedDate(idx);
                            if (opt.isEmpty()) {
                                messages.add(new Message(false, "You should have been able to return a list of topics, despide this being null."));
                                expectedMessage = null;
                            } else {
                                if (opt.get().equals(idx)) {
                                    messages.add(new Message(true, "You correctly returned a list of topics sorted by date."));
                                } else {
                                    messages.add(new Message(false, "You correctly returned a list of topics sorted by date."));
                                }
                                expectedMessage = tmp;
                            }
                        }

                        case QueryGetAllTopicPosts -> {
                            if (topicId == 0) {
                                var opt = testBlockingEvent(() -> b.getAllMessagesFromTopic(0));
                                if (opt.isEmpty()) {
                                    messages.add(new Message(true, "Correctly failing at retrieving non-existant posts"));
                                    expectedMessage = null;
                                } else {
                                    messages.add(new Message(false, "You should not be able to retrieve messages from a non-existant thread"));
                                    expectedMessage = TopicUpdates.getAllMessagesFromTopic(null, 0, Collections.emptyList());
                                }
                            } else {
                                int currTopic = rndInt.nextInt(0, topicId);
                                var opt = testBlockingEvent(() -> b.getAllMessagesFromTopic(currTopic));
                                expectedMessage = TopicUpdates.getAllMessagesFromTopic(topicNames.get(currTopic), currTopic, messagesSimulated.get(currTopic).getMessages());
                                if (opt.isPresent() && opt.get().equals(expectedMessage)) {
                                    messages.add(new Message(true, "You successfully retrieved a list of messages a topic thread"));
                                } else {
                                    messages.add(new Message(false, "You should be able to retrive a list of messages from an existing thread, and it should match with the expected format"));
                                }
                            }
                            actions++;
                        }
                    }
                    expectingMessage.set(expectedMessage != null);
                    if (expectedMessage != null)
                        expectedUpdates.add(expectedMessage);
                }
                doContinuePolling.set(false);
            });

            reader.start();
            messager.start();

            {
                String message = null;
                try {
                    reader.join(100000);
                } catch (Exception e) {
                    message = e.getMessage();
                }
                if (message != null) {
                    messages.add(new Message(false, "There was an issue with the thread reading from the pool: " + message));
                } else {
                    messages.add(new Message(true, "The reader terminated gracefully after 10s"));
                }
            }
            {
                String message = null;
                try {
                    messager.join(10000);
                } catch (Exception e) {
                    message = e.getMessage();
                }
                if (message != null) {
                    messages.add(new Message(false, "There was an issue with the operating thread (messager): " + message));
                } else {
                    messages.add(new Message(true, "The operating thread terminated gracefully after 10s"));
                }
            }
            finalMessageList.addAll(messages);
        }
        return finalMessageList;
    }

    private static List<Message> testII_3() {
        ArrayList<Message> finalMessageList = new ArrayList<>();
        for (int run = 0; run<200; run++) {
            System.out.println("ROUND #"+run);
            ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
            var b = new Blog();
            Thread querier = new Thread(() -> {
                TopicUpdates outcome = null;
                for (int i = 0; i<3; i++) {
                    int finalI = i;
                    b.createNewTopicThread("Help Me!");
                    b.addPostToThreadId(finalI, "querier", "Can you help me with this?");
                }
                for (int i = 0; i<3; i++) {
                    do {
                        var allMessages = b.getAllMessagesFromTopic(i);
                        if (allMessages == null)
                            System.err.println("ERROR! "+i);
                        var payload = allMessages.getPayload();
                        if ((!payload.isEmpty()) && (payload.split("\n\n").length >8))
                            break;
                    } while (true);
                    b.addPostToThreadId(i, "querier", "Thank You!");
                }
            }, "querier");

            Thread[] answerer = new Thread[8];
            for (int i = 0; i<answerer.length; i++) {
                int finalI = i;
                answerer[i] = new Thread(() -> {
                    Random rand = new Random();
                    boolean firstIteration = true;
                    Set<Integer> threadsWithReplies = new HashSet<>();
                    while (true) {
                        List<Integer> topicIdList;
                        topicIdList = b.getAllTopicIDs();
                        if ((!firstIteration) && (topicIdList.isEmpty())) {
                            break;
                        }
                        if (firstIteration) {
                            while (true) {
                                b.pollForUpdate(); // Minimise the risk of computing getAllTopicIDs in vain
                                topicIdList = b.getAllTopicIDs();
                                if (!topicIdList.isEmpty())
                                    break;
                            }
                        }
                        firstIteration = false;
                        topicIdList.removeAll(threadsWithReplies);
                        if (topicIdList.isEmpty())
                            break;
                        int randomElement;
                        String payload = "";
                        do {
                            randomElement = topicIdList.get(rand.nextInt(topicIdList.size()));
                            payload = b.getAllMessagesFromTopic(randomElement).getPayload();
                        } while (payload.isEmpty() || (payload.split("\n\n").length == 0));
                        threadsWithReplies.add(randomElement);
                        b.addPostToThreadId(randomElement, "querier"+finalI, "This is my answer to this problem: "+finalI);
                    }
//                    writeln("AARGH!! " + finalI);
                }, "answerer"+finalI);
            }

            Thread moderator = new Thread(() -> {
                Set<Integer> removed = new HashSet<>();
                for (int i = 0; i<3; i++) {
                    int N;
                    int toRemove = -1;
                    while (true) {
                        for (int j = 0; j<3; j++) {
                            if (!removed.contains(j)) {
                                var event = b.getAllMessagesFromTopic(j);
                                if ((event != null) && event.getPayload().split("\n\n").length == 10) {
                                    toRemove = j;
                                    break;
                                }
                            }
                        }
                        if (toRemove >= 0)
                            break;
                    }
                    String messagesRetrieved = b.getAllMessagesFromTopic(toRemove).getPayload();
                    if (messagesRetrieved.startsWith("From: querier\n" +
                            "Text: Can you help me with this?\n" +
                            "\n" +
                            "From: querier")) {
                        messages.add(new Message(true, "(3:"+i+") GOOD: the querier correctly started the posting attempt first"));
                    } else {
                        messages.add(new Message(false, "(3:"+i+") ERROR: the querier should have started the posting attempt"));
                    }
                    if (messagesRetrieved.endsWith("\nFrom: querier\n" +
                            "Text: Thank You!")) {
                        messages.add(new Message(true, "(4:"+i+") GOOD: the querier correctly finished the thread with an acknowledge."));
                    } else {
                        messages.add(new Message(false, "(4:"+i+") ERROR: the querier should have ended the thread"));
                    }
                    b.removeTopicThreadById(toRemove);
                    removed.add(toRemove);
                }
            }, "moderator");

            moderator.start();
            for (int i = 0; i<answerer.length; i++) {
                answerer[i].start();
            }
            querier.start();

            {
                String errModerator = null;
                try {
                    moderator.join();
                } catch (InterruptedException e) {
                    errModerator = e.getMessage();
                }
                if (errModerator != null) {
                    messages.add(new Message(false, "There was an error with the moderator: "+errModerator));
                } else {
                    messages.add(new Message(true, "The moderator terminated correctly"));
                }
            }
            for (int i = 0; i<answerer.length; i++) {
                String errModerator = null;
                try {
                    answerer[i].join();
                } catch (InterruptedException e) {
                    errModerator = e.getMessage();
                }
                if (errModerator != null) {
                    messages.add(new Message(false, "There was an error with the answerer #"+i+": "+errModerator));
                } else {
                    messages.add(new Message(true, "Answerer #"+i+" terminated correctly"));
                }

            }
            {
                String errModerator = null;
                try {
                    querier.join();
                } catch (InterruptedException e) {
                    errModerator = e.getMessage();
                }
                if (errModerator != null) {
                    messages.add(new Message(false, "There was an error with the querierer: "+errModerator));
                } else {
                    messages.add(new Message(true, "The querierer terminated correctly"));
                }
            }

            ArrayList<Message> outcome = new ArrayList<>(messages);
            boolean _30found = false;
            boolean _31found = false;
            boolean _32found = false;
            boolean _40found = false;
            boolean _41found = false;
            boolean _42found = false;
            for (var msg : outcome) {
                if (msg.message.startsWith("(3:0)")) _30found = true;
                if (msg.message.startsWith("(3:1)")) _31found = true;
                if (msg.message.startsWith("(3:2)")) _32found = true;
                if (msg.message.startsWith("(4:0)")) _40found = true;
                if (msg.message.startsWith("(4:1)")) _41found = true;
                if (msg.message.startsWith("(4:2)")) _42found = true;
            }
            if (!_30found) {
                outcome.add(new Message(false, "(3:0) ERROR: not found, maybe due to a previous exception"));
            }
            if (!_31found) {
                outcome.add(new Message(false, "(3:1) ERROR: not found, maybe due to a previous exception"));
            }
            if (!_32found) {
                outcome.add(new Message(false, "(3:2) ERROR: not found, maybe due to a previous exception"));
            }
            if (!_40found) {
                outcome.add(new Message(false, "(4:0) ERROR: not found, maybe due to a previous exception"));
            }
            if (!_41found) {
                outcome.add(new Message(false, "(4:1) ERROR: not found, maybe due to a previous exception"));
            }
            if (!_42found) {
                outcome.add(new Message(false, "(4:2) ERROR: not found, maybe due to a previous exception"));
            }

            for (Message m : outcome) {
                finalMessageList.add(new Message(m.isOK, "[Iteration #"+run+"] = "+m.message));
            }
        }
        return finalMessageList;
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
        String StudentId = Blog.studentID();
        FileOutputStream fos = new FileOutputStream(new File(StudentId+".html"));
        html = new HtmlAnsiOutputStream(fos);
        Consumer<String> toFile = Testing3bis::writeln;
        Consumer<String> toConsole = System.out::println;
        Consumer<String> currentConsumer = toConsole; //toFile, toConsole;

        Function<Boolean, List<Message>> none = null;
        List<Test> scoring = new ArrayList<>();

        if (currentConsumer.hashCode() == toFile.hashCode())
            noline("<!DOCTYPE html><html><body>");
        currentConsumer.accept("StudentId: " + StudentId);
        currentConsumer.accept("I. Single Threaded Correctness");
        currentConsumer.accept("==============================");
        currentConsumer.accept("");
        scoring.add(new Test(Testing3bis::testI_1,
                "I cannot interact with a blog thread if this was not previously created.",
                4.0));
        scoring.add(new Test(Testing3bis::testI_2,
                "I can always interact with a topic thread that was previously created.",
                16.0));
        scoring.add(new Test(Testing3bis::testI_3,
                "I am correctly handling the thread closure.",
                4.0));
        scoring.add(new Test(Testing3bis::testI_4,
                "I am correctly handling the pollForUpdate method where, if successful requests are always fired before polling for events, should always return the most recent event available.",
                7.0));
        scoring.add(new Test(Testing3bis::testI_5,
                "I am handling the event update messages correctly.",
                3.0));
        scoring.add(new Test(Testing3bis::testI_6,
                "I am correctly handling the set method from ReadWriteMonitorMultiRead.",
                10.0));
        scoring.add(new Test(Testing3bis::testI_7,
                "I am correctly handling the get method from ReadWriteMonitorMultiRead.",
                6.0));

        FunctionScoring(scoring, currentConsumer);
        scoring.clear();

        currentConsumer.accept("");
        currentConsumer.accept("II. Multi-Threaded Correctness");
        currentConsumer.accept("==============================");
        currentConsumer.accept("");
        scoring.add(new Test(Testing3bis::testII_1,
                "Correctly handling the concurrent creation of different topics.",
                7.0));
        scoring.add(new Test(Testing3bis::testII_2,
                "Correctly handling the concurrent creation of different messages/posts within the same topic.",
                9.0));
        //currentConsumer.accept("The following two are tests usually not working on studen's projects");
        scoring.add(new Test(Testing3bis::testII_3,
                "The moderator is able to wait to receive 10 messages, after which the main thread and their posts are deleted",
                12.0));
        scoring.add(new Test(Testing3bis::testII_4,
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

    public static boolean isPresentTest(Optional<TopicUpdates> result1) {
        return result1.isPresent() && result1.get() != null && result1.get().getPayload() != null;
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
        public int compareTo(@NotNull Testing3bis.Message o) {
            return message.compareTo(o.message);
        }
    }

}
