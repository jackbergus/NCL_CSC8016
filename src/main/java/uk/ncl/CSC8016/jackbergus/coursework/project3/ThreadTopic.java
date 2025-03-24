package uk.ncl.CSC8016.jackbergus.coursework.project3;

import java.util.ArrayList;
import java.util.List;

public class ThreadTopic { // THIS CLASS SHALL NOT BE CHANGED

    private final String threadTopicName;
    List<String> messages;

    public ThreadTopic(String threadTopicName) {
        messages = new ArrayList<>();
        this.threadTopicName = threadTopicName;
    }

    public int addNewMessage(String nickname, String message) {
        if (message.startsWith("This is my answer to this problem:") && messages.size() == 0)
            System.err.println("HERE!");
        messages.add("From: "+nickname+"\nText: "+message);
        return messages.size()-1;
    }

    public List<String> getMessages() {
        return new ArrayList<String>(messages);
    }

    public String getThreadName() {
        return threadTopicName;
    }
}
