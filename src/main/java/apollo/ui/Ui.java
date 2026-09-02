package apollo.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.TaskList;

/** Handles console input and presentation of messages and task information. */
public class Ui {
    private static final String GREETING =
            "Greetings young mortal! Apollo here to answer any queries under the sun!";
    private static final String EXIT_MESSAGE =
            "To the end of the west wind, where fresh flowers bloom.";

    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Reads and returns the next command entered by the user. */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /** Returns whether another console command is available to read. */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /** Closes the console input scanner. */
    public void close() {
        this.scanner.close();
    }

    /** Prints greeting message to console */
    public void showGreeting() {
        showBarrier();
        System.out.println(GREETING);
    }

    /** Prints exit message to console*/
    public void showExit() {
        System.out.println(EXIT_MESSAGE);
        showBarrier();
    }

    public void showBarrier() {
        System.out.println("=======================================================================");
    }

    public void showSeparator() {
        System.out.println("-----------------------------------------------------------------------");
    }

    /** Prints a response produced by Apollo's command-processing core. */
    public void showResponse(String response) {
        System.out.println(response);
    }

    public void showInvalidCommand() {
        System.out.println("Invalid command given, try again mortal.");
    }

    public void showInvalidIndex() {
        System.out.println("Give a valid index mortal");
    }

    public void showEmptyDescription() {
        System.out.println("The description cannot be empty mortal!");
    }

    public void showInvalidTaskArguments() {
        System.out.println("Give me good arguments mortal!");
    }

    public void showInvalidDateTime() {
        System.out.println("Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!");
    }

    public void showInvalidDate() {
        System.out.println("Use a date in d/M/yyyy format, mortal!");
    }

    public void showLoadingError(String message) {
        System.out.println("Error loading tasks: " + message);
    }

    public void showSavingError() {
        System.out.println("Could not save tasks.");
    }

    /**
     * Displays a task after it has been added.
     *
     * @param task task that was added
     */
    public void showTaskAdded(Task task) {
        System.out.println("Understood child, adding to your task list:");
        System.out.println(task);
    }

    /**
     * Displays a task after its completion state has changed.
     *
     * @param task task whose completion state changed
     */
    public void showMarkChange(Task task) {
        String change = task.getIsDone() ? "marked" : "unmarked";
        System.out.println(String.format(
                "Your prayers are heard child, I have %s the specified task:", change));
        System.out.println(task);
    }

    /**
     * Displays a deleted task and the number of tasks remaining.
     *
     * @param task task that was deleted
     * @param remainingTaskCount number of tasks remaining after deletion
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        System.out.println("Understood young one, I have removed this task:");
        System.out.println(task);
        System.out.println(String.format("You now have %d tasks left.", remainingTaskCount));
    }

    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are your current tasks child: ");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, tasks.get(i)));
        }
    }

    /**
     * Displays the supplied ongoing events, or a message if there are none.
     *
     * @param events ongoing events to display
     */
    public void showOngoingEvents(List<Event> events) {
        System.out.println("Here are your ongoing events, child:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, events.get(i)));
        }
        if (events.isEmpty()) {
            System.out.println("You have no ongoing events.");
        }
    }

    /**
     * Displays deadlines due on the supplied date, or a message if there are none.
     *
     * @param date due date represented by the results
     * @param deadlines deadlines to display
     */
    public void showDeadlinesDueOn(LocalDate date, List<Deadline> deadlines) {
        System.out.println(String.format("Here are your deadlines due on %s, child:", date));
        for (int i = 0; i < deadlines.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, deadlines.get(i)));
        }
        if (deadlines.isEmpty()) {
            System.out.println("You have no deadlines due on this date.");
        }
    }

    /**
     * Prints matching tasks output to terminal.
     *
     * @param matchingTasks list of tasks that contain the search text
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            showEmptySearchText();
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(String.format(
                        "%d. %s", i + 1, matchingTasks.get(i)));
            }
        }
    }
    /** Prints out empty search text */
    public void showEmptySearchText() {
        System.out.println("There are no matching tasks.");
    }
}
