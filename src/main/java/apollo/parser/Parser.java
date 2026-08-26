package apollo.parser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Todo;

public class Parser {
    public enum Command {
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        EVENT,
        DEADLINE,
        DUETODAY,
        ONGOINGNOW,
        DUETHISDATE,
        FIND,
        BYE
    }

    public Command parseCommand(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        return Command.valueOf(commandWord.toUpperCase());
    }

    public int parseIndex(String input) {
        String[] inputArgs = input.trim().split("\\s+");
        return Integer.parseInt(inputArgs[1]) - 1;
    }

    public Todo parseTodo(String input) {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Todo description cannot be empty");
        }
        return new Todo(description);
    }

    public Deadline parseDeadline(String input) {
        String deadlineArgs = input.substring("deadline".length()).trim();
        int byIndex = deadlineArgs.indexOf("/by");
        String description = deadlineArgs.substring(0, byIndex).trim();
        String dateString = deadlineArgs.substring(byIndex + "/by".length()).trim();
        LocalDateTime by = DateParser.parseDateTime(dateString, LocalTime.of(23, 59));
        return new Deadline(description, by);
    }

    public Event parseEvent(String input) {
        String eventArgs = input.substring("event".length()).trim();
        int fromIndex = eventArgs.indexOf("/from");
        int toIndex = eventArgs.indexOf("/to");
        String description = eventArgs.substring(0, fromIndex).trim();
        String fromString = eventArgs.substring(fromIndex + "/from".length(), toIndex).trim();
        String toString = eventArgs.substring(toIndex + "/to".length()).trim();
        LocalDateTime from = DateParser.parseDateTime(fromString, LocalTime.MIDNIGHT);
        LocalDateTime to = DateParser.parseDateTime(toString, LocalTime.of(23, 59));

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end cannot be before its start");
        }
        return new Event(description, from, to);
    }

    public LocalDate parseDueDate(String input) {
        String dateString = input.substring("dueThisDate".length()).trim();
        return DateParser.parseDate(dateString);
    }

    /**
     * Parses command to get the search text.
     *
     * @param input command line input
     * @return search text used to filter for tasks
     */
    public String parseFindText(String input) {
        String searchText = input.substring("find".length()).trim();

        if (searchText.isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }

        return searchText;
    }
}
