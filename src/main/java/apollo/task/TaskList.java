package apollo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Stores tasks and provides operations for managing and querying them. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks to place in the list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void add(Task... tasks) {
        int previousSize = this.tasks.size();

        for(Task task : tasks) {
            assert task != null : "TaskList must not contain null tasks";
            this.tasks.add(task);
        }

        assert this.tasks.size() == previousSize + tasks.length
                : "Task count must increase by number of tasks added";
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the specified index
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     */
    public Task delete(int index) {
        int previousSize = this.tasks.size();
        Task deletedTask = this.tasks.remove(index);

        assert this.tasks.size() == previousSize - 1
                : "Deleting one task must decrease task count by one";
        assert deletedTask != null
                : "Deleting a valid index must return a task";

        return deletedTask;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return this.tasks.size();
    }

    /** Returns a copy of the tasks for display or persistence. */
    public List<Task> getTasks() {
        return new ArrayList<>(this.tasks);
    }

    /**
     * Returns all deadlines due on the supplied date.
     *
     * @param date date on which deadlines must be due
     * @return deadlines due on the specified date
     */
    public List<Deadline> getDeadlinesDueOn(LocalDate date) {
        assert date != null : "Date used to search deadlines must not be null";
        List<Deadline> matchingDeadlines = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.getBy().toLocalDate().equals(date)) {
                    matchingDeadlines.add(deadline);
                }
            }
        }
        return matchingDeadlines;
    }

    /**
     * Returns all events whose time range includes the supplied time.
     *
     * @param dateTime date and time that must fall within each event's range
     * @return events ongoing at the specified date and time
     */
    public List<Event> getEventsOngoingAt(LocalDateTime dateTime) {
        assert dateTime != null : "Time used to search events must not be null";
        List<Event> matchingEvents = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task instanceof Event) {
                Event event = (Event) task;
                boolean hasStarted = !dateTime.isBefore(event.getFrom());
                boolean hasNotEnded = !dateTime.isAfter(event.getTo());
                if (hasStarted && hasNotEnded) {
                    matchingEvents.add(event);
                }
            }
        }
        return matchingEvents;
    }

    /**
     * Finds tasks that include the search text.
     *
     * @param searchText the text that we are using to filter for tasks
     * @return list of tasks that mention the search text
     */
    public List<Task> find(String searchText) {
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : this.tasks) {
            if (task.containsDescription(searchText)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }
}
