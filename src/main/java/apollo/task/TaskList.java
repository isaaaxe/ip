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
    public void add(Task task) {
        this.tasks.add(task);
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
        return this.tasks.remove(index);
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
}
