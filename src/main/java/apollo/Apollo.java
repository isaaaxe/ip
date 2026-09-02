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

/**
 * Processes Apollo commands independently of the interface used to enter them.
 * Both the JavaFX and command-line front ends use the same Apollo instance as
 * the application's command-processing core.
 */
public class Apollo {
    private static final String DEFAULT_STORAGE_PATH = "data/apollo.txt";
    private static final String EXIT_MESSAGE =
            "To the end of the west wind, where fresh flowers bloom.";

    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private boolean exitRequested;
    private String commandType = "DefaultCommand";
    private String loadingError;

    /** Creates Apollo using the default task storage file. */
    public Apollo() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Creates Apollo using the specified task storage file.
     *
     * @param storagePath path of the file used to load and save tasks
     */
    public Apollo(String storagePath) {
        this.storage = new Storage(storagePath);
        this.parser = new Parser();
        this.tasks = new TaskList();
        loadTasks();
    }

    /**
     * Processes one user command and returns the message to display.
     *
     * @param input complete command entered by the user
     * @return Apollo's response to the command
     */
    public String getResponse(String input) {
        this.commandType = "DefaultCommand";

        try {
            Command command = this.parser.parseCommand(input);
            this.commandType = mapCommandType(command);

            return switch (command) {
                case BYE -> processBye();
                case LIST -> formatTaskList();
                case MARK -> processMark(input, true);
                case UNMARK -> processMark(input, false);
                case TODO -> processTodo(input);
                case EVENT -> processEvent(input);
                case DEADLINE -> processDeadline(input);
                case DUE_TODAY -> formatDeadlinesDueOn(LocalDate.now());
                case ONGOING_NOW -> formatOngoingEvents();
                case DUE_THIS_DATE -> processDueThisDate(input);
                case DELETE -> processDelete(input);
                case FIND -> processFind(input);
            };
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "Invalid command given, try again mortal.";
        }
    }

    /** Returns the category of the most recently processed command for GUI styling. */
    public String getCommandType() {
        return this.commandType;
    }

    /** Returns whether the current front-end session has received {@code bye}. */
    public boolean isExitRequested() {
        return this.exitRequested;
    }

    /**
     * Returns the loading error encountered during construction, or {@code null}
     * if the task file loaded successfully.
     */
    public String getLoadingError() {
        return this.loadingError;
    }

    /** Loads saved tasks without depending on either the console or JavaFX UI. */
    private void loadTasks() {
        try {
            this.tasks = new TaskList(this.storage.load());
        } catch (IOException e) {
            this.loadingError = "Error loading tasks: " + e.getMessage();
        }
    }

    /** Processes a request to end the current front-end session. */
    private String processBye() {
        this.exitRequested = true;
        return EXIT_MESSAGE;
    }

    /** Processes a command that changes a task's completion state. */
    private String processMark(String input, boolean isDone) {
        try {
            int index = this.parser.parseIndex(input);
            Task task = this.tasks.get(index);
            task.markAsDone(isDone);
            return appendSavingErrorIfNeeded(formatMarkChange(task));
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "Give a valid index mortal";
        }
    }

    /** Processes a todo command and adds the resulting task. */
    private String processTodo(String input) {
        try {
            return addTask(this.parser.parseTodo(input));
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "The description cannot be empty mortal!";
        }
    }

    /** Processes an event command and adds the resulting task. */
    private String processEvent(String input) {
        try {
            return addTask(this.parser.parseEvent(input));
        } catch (DateTimeParseException e) {
            this.commandType = "ErrorCommand";
            return "Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!";
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "Give me good arguments mortal!";
        }
    }

    /** Processes a deadline command and adds the resulting task. */
    private String processDeadline(String input) {
        try {
            return addTask(this.parser.parseDeadline(input));
        } catch (DateTimeParseException e) {
            this.commandType = "ErrorCommand";
            return "Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!";
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "Give me good arguments mortal!";
        }
    }

    /** Processes a query for deadlines due on a specified date. */
    private String processDueThisDate(String input) {
        try {
            return formatDeadlinesDueOn(this.parser.parseDueDate(input));
        } catch (DateTimeParseException e) {
            this.commandType = "ErrorCommand";
            return "Use a date in d/M/yyyy format, mortal!";
        }
    }

    /** Processes a task deletion command. */
    private String processDelete(String input) {
        try {
            int index = this.parser.parseIndex(input);
            Task deletedTask = this.tasks.delete(index);
            String response = String.format(
                    "Understood young one, I have removed this task:%n%s%n"
                            + "You now have %d tasks left.",
                    deletedTask, this.tasks.size());
            return appendSavingErrorIfNeeded(response);
        } catch (Exception e) {
            this.commandType = "ErrorCommand";
            return "Give a valid index mortal";
        }
    }

    /** Processes a task search command. */
    private String processFind(String input) {
        try {
            String searchText = this.parser.parseFindText(input);
            return formatMatchingTasks(this.tasks.find(searchText));
        } catch (IllegalArgumentException e) {
            this.commandType = "ErrorCommand";
            return "There are no matching tasks.";
        }
    }

    /** Adds a task and returns its confirmation message. */
    private String addTask(Task task) {
        this.tasks.add(task);
        String response = String.format(
                "Understood child, adding to your task list:%n%s", task);
        return appendSavingErrorIfNeeded(response);
    }

    /** Formats the confirmation for a mark or unmark operation. */
    private String formatMarkChange(Task task) {
        String change = task.getIsDone() ? "marked" : "unmarked";
        return String.format(
                "Your prayers are heard child, I have %s the specified task:%n%s",
                change, task);
    }

    /** Formats every task with one-based numbering. */
    private String formatTaskList() {
        StringBuilder response = new StringBuilder("Here are your current tasks child:");
        appendNumberedTasks(response, this.tasks.getTasks());
        return response.toString();
    }

    /** Formats events that are ongoing at the current time. */
    private String formatOngoingEvents() {
        List<Event> events = this.tasks.getEventsOngoingAt(LocalDateTime.now());
        StringBuilder response = new StringBuilder("Here are your ongoing events, child:");
        appendNumberedTasks(response, events);
        if (events.isEmpty()) {
            response.append(System.lineSeparator()).append("You have no ongoing events.");
        }
        return response.toString();
    }

    /** Formats deadlines due on the supplied date. */
    private String formatDeadlinesDueOn(LocalDate date) {
        List<Deadline> deadlines = this.tasks.getDeadlinesDueOn(date);
        StringBuilder response = new StringBuilder(String.format(
                "Here are your deadlines due on %s, child:", date));
        appendNumberedTasks(response, deadlines);
        if (deadlines.isEmpty()) {
            response.append(System.lineSeparator())
                    .append("You have no deadlines due on this date.");
        }
        return response.toString();
    }

    /** Formats tasks whose descriptions match the supplied search text. */
    private String formatMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "There are no matching tasks.";
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        appendNumberedTasks(response, matchingTasks);
        return response.toString();
    }

    /** Appends tasks to a message using one-based numbering. */
    private void appendNumberedTasks(StringBuilder response, List<? extends Task> listedTasks) {
        for (int i = 0; i < listedTasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(String.format("%d. %s", i + 1, listedTasks.get(i)));
        }
    }

    /** Saves a mutation and adds a user-facing warning if persistence fails. */
    private String appendSavingErrorIfNeeded(String response) {
        try {
            this.storage.save(this.tasks.getTasks());
            return response;
        } catch (IOException e) {
            return response + System.lineSeparator() + "Could not save tasks.";
        }
    }

    /** Maps parser commands onto the style categories understood by the GUI. */
    private String mapCommandType(Command command) {
        return switch (command) {
            case TODO, EVENT, DEADLINE -> "AddCommand";
            case MARK, UNMARK -> "ChangeMarkCommand";
            case DELETE -> "DeleteCommand";
            case LIST, FIND, DUE_TODAY, ONGOING_NOW, DUE_THIS_DATE -> "ListCommand";
            case BYE -> "ByeCommand";
        };
    }
}
