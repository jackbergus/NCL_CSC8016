package uk.ncl.CSC8016.jackbergus.coursework.project3.events;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TopicUpdates { // THIS CLASS SHALL NOT BE CHANGED, EXCEPT FROM WHEN EXPLICITLY ASKED
    private TopicUpdatesType op;
    private String topicName;
    private int topicID;
    private int latestCommentID;
    private String payload;

    private TopicUpdates(TopicUpdatesType op, String topicName, int topicID, int latestCommentID, String payload) {
        this.op = op;
        this.topicName = topicName;
        this.topicID = topicID;
        this.latestCommentID = latestCommentID;
        this.payload = payload;
    }

    public static  TopicUpdates newTopic(String topicName, int topicID) {
        return new TopicUpdates(TopicUpdatesType.NewTopicPublished, topicName, topicID, -1, null);
    }

    public static TopicUpdates delTopic(String topicName, int topicID) {
        return new TopicUpdates(TopicUpdatesType.TopicDeleted, topicName, topicID, -1, null);
    }

    public static TopicUpdates newPost(String topicName, int topicID, int lastComment) {
        return new TopicUpdates(TopicUpdatesType.NewCommentPublishedInToipic, topicName, topicID, lastComment, null);
    }


    public static TopicUpdates getAllTopicNamesSortedByFirstPublishedDate(List<String> allTopics) {
        return new TopicUpdates(TopicUpdatesType.QueryGetAllTopicNamesSortedByDate, null, -1, -1, String.join("\n", allTopics));
    }

    public static TopicUpdates getAllTopicIDsSortedByFirstPublishedDate(List<Integer> allTopics) {
        return new TopicUpdates(TopicUpdatesType.QueryGetAllTopicIDsSortedByDate, null, -1, -1, allTopics.stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    public static TopicUpdates getAllMessagesFromTopic(String topicName, int topicID, List<String> allMessages) {
        return new TopicUpdates(TopicUpdatesType.QueryGetAllTopicPosts, topicName, topicID, allMessages.size(), String.join("\n\n", allMessages));
    }

    /**
     * Type of update message of interest to update the querier.
     * @return
     */
    public TopicUpdatesType getOperation() {
        return op;
    }

    /**
     * If of refers to a specific topic, this should refer to the name associated to the specific topic of interest.
     * @return
     */
    public String getThreadTopicName() {
        return topicName;
    }

    /**
     * If op refers to a specific topic, this should return the topic id of interest. Otherwise, this should return -1
     * @return
     */
    public int    getThreadTopicID() {
        return topicID;
    }

    /**
     * If this refers to QueryGetAllTopicNamesSortedByDate, this should return the number of messages available within the
     * specific thread of interest minus one, and -1 otherwise.
     * @return
     */
    public int    getLatestCommentId() {
        return latestCommentID;
    }

    /**
     * If this refers to QueryGetAllTopicNamesSortedByDate, this should return a "\n"-separated topic name list;
     * if this refers to QueryGetAllTopicNamesSortedByDate, this should return a "\n\n"-separated list of messages, each
     * of one is rendered as From: username\nText: content. Otherwise, this should return null
     *
     * @return
     */
    public String getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicUpdates that = (TopicUpdates) o;
        return topicID == that.topicID && latestCommentID == that.latestCommentID && op == that.op && Objects.equals(topicName, that.topicName) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, topicName, topicID, latestCommentID, payload);
    }
}
