package uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class TaskList<T> implements Iterator<T> {

    private T init;                           // Initial value from which start the computation
    private List<Function<T, T>> taskList;    // List of functions, taking init as an input, and updating it with the provided output
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
            return init; // If I reach the end, then the init field will contain the final result
        else {
            // At each i-th iteration step, I'm getting the i-th function and applying the previous computed results. This will be stored in the global variable.
            init = taskList.get(count).apply(init);
            // Incrementing the i-th to (i+1).
            count++;
            return init;
        }
    }

    public int size() {
        return taskList.size();
    }

}
