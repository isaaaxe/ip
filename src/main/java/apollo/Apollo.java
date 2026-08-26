package apollo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import apollo.parser.Parser;
import apollo.parser.Parser.Command;
import apollo.storage.Storage;
import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.TaskList;
import apollo.ui.Ui;

public class Apollo {
    private static boolean validSession = true;
    private static TaskList tasks = new TaskList();
    private static Storage storage = new Storage("data/apollo.txt");
    private static Parser parser = new Parser();
    private static Ui ui = new Ui();

    public static void main(String[] args) {

        // Load the saved task list.
        loadTasks();

        ui.showGreeting();
        while (validSession) {
            String input = ui.readCommand();
            ui.showSeparator();

            try {
                Command command = parser.parseCommand(input);
                switch (command) {
                    case BYE:
                        validSession = false;
                        break;
                    case LIST:
                        printList();
                        break;
                    case MARK:
                        try {
                            int indexMark = parser.parseIndex(input);
                            markAsDone(indexMark);
                        } catch (Exception e) {
                            ui.showInvalidIndex();
                        }
                        break;
                    case UNMARK:
                        try {
                            int indexUnmark = parser.parseIndex(input);
                            markAsUndone(indexUnmark);
                        } catch (Exception e) {
                            ui.showInvalidIndex();
                        }
                        break;
                    case TODO:
                        try {
                            addTask(parser.parseTodo(input));
                        } catch (Exception e) {
                            ui.showEmptyDescription();
                        }
                        break;
                    case EVENT:
                        try {
                            addTask(parser.parseEvent(input));
                        } catch (DateTimeParseException e) {
                            ui.showInvalidDateTime();
                        } catch (Exception e) {
                            ui.showInvalidTaskArguments();
                        }
                        break;
                    case DEADLINE:
                        try {
                            addTask(parser.parseDeadline(input));
                        } catch (DateTimeParseException e) {
                            ui.showInvalidDateTime();
                        } catch (Exception e) {
                            ui.showInvalidTaskArguments();
                        }
                        break;
                    case DUE_TODAY:
                        dueToday();
                        break;
                    case ONGOING_NOW:
                        ongoingNow();
                        break;
                    case DUE_THIS_DATE:
                        try {
                            dueThisDate(parser.parseDueDate(input));
                        } catch (DateTimeParseException e) {
                            ui.showInvalidDate();
                        }
                        break;
                    case DELETE:
                        try {
                            int indexDelete = parser.parseIndex(input);
                            deleteTask(indexDelete);
                        } catch (Exception e) {
                            ui.showInvalidIndex();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported command: " + command);
                }
            } catch (Exception e) {
                ui.showInvalidCommand();
            } finally {
                ui.showSeparator();
            }
        }
        ui.showExit();
        ui.close();
        saveTasks();
    }

    public static void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showSavingError();
        }
    }

    public static void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
        }
    }

    public static void printList() {
        ui.showTaskList(tasks);
    }

    public static void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task);
    }

    public static void markAsDone(int index) {
        Task currentTask = tasks.get(index);
        currentTask.markAsDone(true);
        ui.showMarkChange(currentTask);
    }

    public static void markAsUndone(int index) {
        Task currentTask = tasks.get(index);
        currentTask.markAsDone(false);
        ui.showMarkChange(currentTask);
    }

    public static void deleteTask(int index) {
        Task deletedTask = tasks.delete(index);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /** Shows all deadlines due on the current date. */
    public static void dueToday() {
        dueThisDate(LocalDate.now());
    }

    /** Shows all events whose time range includes the current time. */
    public static void ongoingNow() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> ongoingEvents = tasks.getEventsOngoingAt(now);
        ui.showOngoingEvents(ongoingEvents);
    }

    /**
     * Shows all deadlines due on the supplied date.
     *
     * @param date date for which deadlines should be shown
     */
    public static void dueThisDate(LocalDate date) {
        List<Deadline> dueDeadlines = tasks.getDeadlinesDueOn(date);
        ui.showDeadlinesDueOn(date, dueDeadlines);
    }
}
