package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class TaskList<T> implements Iterator<T> {

    private T init;
    private List<Function<T, T>> taskList;
    private int count;

    public TaskList(T init, List<Function<T, T>> taskList) {
        this.init = init;
        this.taskList = new ArrayList<>();
        this.taskList.addAll(taskList);
        count = 0;
    }

    public boolean hasNext() {
        return (count < taskList.size());
    }

    public T next() {
        if (count == taskList.size())
            return init;
        else {
            init = taskList.get(count).apply(init);
            count++;
            return init;
        }
    }

    public int size() {
        return taskList.size();
    }

}
