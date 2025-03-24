package uk.ncl.CSC8016.jackbergus.coursework.project3;

import uk.ncl.CSC8016.jackbergus.coursework.project3.events.TopicUpdates;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import javax.xml.stream.FactoryConfigurationError;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Blog {

    // TODO: YOU CAN ADD OTHER FIELDS, IF YOU LIKE, BUT NOT  CHANGE THE ONES GIVEN BELOW
    HashMap<Pair<String, LocalDateTime>, Integer> blog;         // TO KEEP
    private List<Integer> timeOrderedList;                          // TO KEEP
    private HashMap<Integer, ThreadTopic> incrementalTopicList;     // TO KEEP
    public AtomicInteger ai;                                        // TO REMOVE
    public ReadWriteMonitorMultiRead<TopicUpdates> latestOperation; // TO REMOVE


    public static String studentID() {
        // TODO: YOU NEED TO IMPLEMENT THIS METHOD WITH YOUR STUDENT ID
        return "123456";
    }

    public Blog() {
        ai = new AtomicInteger(0);
        blog = new HashMap<>();
        timeOrderedList = new ArrayList<>();
        incrementalTopicList = new HashMap<>();
        latestOperation = new ReadWriteMonitorMultiRead<>();
//        username = new ConcurrentSkipListSet<>();
    }


    private Integer generateNewTopicId() {
        // TODO: YOU NEED TO IMPLEMENT THIS METHOD
        return ai.getAndIncrement();
    }

    public boolean createNewTopicThread(String threadTopicName) {
        return latestOperation.set(() -> {
            var id = 0;
            return TopicUpdates.newTopic(threadTopicName, id);
        });
    }

    public TopicUpdates pollForUpdate() {
        // TODO: implement this method with concurrency control
        return null;
    }

    public TopicUpdates pollForUpdate(TopicUpdates previousMessage) {
        // TODO: implement this method with concurrency control
        return null;
    }

    public TopicUpdates getAllMessagesFromTopic(int topicId) {
        // TODO: implement this method with concurrency control
        return null;
    }

    public TopicUpdates getAllTopics() {
        // TODO: implement this method with concurrency control
        return null;
    }

    public List<Integer> getAllTopicIDs() {
        AtomicReference<String> payload = new AtomicReference<>("");

        boolean test = false;
        // TODO: implement this method with concurrency control
        if ((!test) || payload.get().isEmpty())
            return Collections.emptyList();
        return  Arrays.stream(payload.get().split("\n")).map(Integer::valueOf).collect(Collectors.toList());
    }

    public boolean removeTopicThreadById(int id) {
        return false;
    }

    public boolean addPostToThreadId(int topicId, String nickname, String message) {
        return latestOperation.set(() -> {
            var newMessageId = 1;
            return TopicUpdates.newPost("SomeThreadTopicNameAssociatedWithId", topicId, newMessageId);
        });
    }


}
