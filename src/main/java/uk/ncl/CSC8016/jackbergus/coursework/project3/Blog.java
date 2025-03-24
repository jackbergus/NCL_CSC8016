package uk.ncl.CSC8016.jackbergus.coursework.project3;

import uk.ncl.CSC8016.jackbergus.coursework.project3.events.TopicUpdates;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

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
            // TODO: remove after this
            var cp = new Pair<>(threadTopicName,LocalDateTime.now());
            if (blog.containsKey(cp)) {
                return null;
            }
            var id = generateNewTopicId();
            var t = new ThreadTopic(threadTopicName);
            blog.put(cp, id);
            timeOrderedList.add(0, id);
            incrementalTopicList.put(id, t);
            // TODO: remove before this
            return TopicUpdates.newTopic(threadTopicName, id);
        });
    }

    public TopicUpdates pollForUpdate() {
        return latestOperation.get(null, null);
    }

    public TopicUpdates pollForUpdate(TopicUpdates previousMessage) {
        return latestOperation.get(null, previousMessage);
    }

    public TopicUpdates getAllMessagesFromTopic(int topicId) {
        AtomicReference<TopicUpdates> payload = new AtomicReference<>(null);
        return latestOperation.set(() -> {
            // TODO: remove after this
            var thread = incrementalTopicList.get(topicId);
            if (thread == null)
                return null;
            // TODO: remove before this
            var tmp = TopicUpdates.getAllMessagesFromTopic(thread.getThreadName(), topicId, thread.messages);
            payload.set(tmp);
            return tmp;
        }) ? payload.get() : null;
    }

    public TopicUpdates getAllTopics() {
        AtomicReference<TopicUpdates> payload = new AtomicReference<>(null);
        return latestOperation.set(() -> {
            // TODO: remove after this
            var ls = new ArrayList<>(blog.keySet());
            ls.sort(Comparator.comparing(o -> o.value));
            // TODO: remove before this
            var tmp = TopicUpdates.getAllTopicNamesSortedByFirstPublishedDate(ls.stream().map(x->x.key).collect(Collectors.toList()));
            payload.set(tmp);
            return tmp;
        }) ? payload.get() : null;
    }

    public List<Integer> getAllTopicIDs() {
        AtomicReference<String> payload = new AtomicReference<>("");
        boolean test = latestOperation.set(() -> {
            // TODO: remove before this
            var tmp =  TopicUpdates.getAllTopicIDsSortedByFirstPublishedDate(timeOrderedList);
            payload.set(tmp.getPayload());
            return tmp;
        });
        if ((!test) || payload.get().isEmpty())
            return Collections.emptyList();
        return  Arrays.stream(payload.get().split("\n")).map(Integer::valueOf).collect(Collectors.toList());
    }

    public boolean removeTopicThreadById(int id) {
        return latestOperation.set(() -> {
            // TODO: remove after this
            var thread = incrementalTopicList.remove(id);
            if (thread == null)
                return null;
            timeOrderedList.remove((Integer) id);
            var ls = blog.entrySet().stream().filter(x -> x.getValue() == id).map(Map.Entry::getKey).toList();
            if (ls.size() > 1)
                return null;
            ls.forEach(k-> blog.remove(k));
            var threadTopicName = ls.get(0).key;
            // TODO: remove before this
            return TopicUpdates.delTopic(threadTopicName, id);
        });
    }

    public boolean addPostToThreadId(int topicId, String nickname, String message) {
        return latestOperation.set(() -> {
            // TODO: remove after this
            var thread = incrementalTopicList.get(topicId);
            if (thread == null)
                return null;
            var ls = blog.entrySet().stream().filter(x -> x.getValue() == topicId).map(Map.Entry::getKey).toList();
            if (ls.size() > 1)
                return null;
            int newMessageId = thread.addNewMessage(nickname, message);
            var threadTopicName = ls.get(0).key;
            // TODO: remove before this
            return TopicUpdates.newPost(threadTopicName, topicId, newMessageId);
        });
    }


}
