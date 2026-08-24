import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Apollo {
    final static String greeting = "Greetings young mortal! Apollo here to answer any queries under the sun!";
    final static String exit = "To the end of the west wind, where fresh flowers bloom.";
    static boolean validSession = true;
    static ArrayList<Task> record  = new ArrayList<Task>();
    static Scanner scanner;
    static int listCount = 0;
    static enum Command {
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
        BYE
    };

    public static void main(String[] args) {

        //load the apollo.txt task list to record
        loadTasks();

        //new scanner object
        scanner = new Scanner(System.in);

        //main process
        printBarrier();
        System.out.println(greeting);
        while (validSession) {
            
            String input = scanner.nextLine();
            String[] inputArgs = input.trim().split(" ");
            printSeparator();

            
            //command switch case
            try {
                Command command = Command.valueOf(inputArgs[0].trim().toUpperCase());
                switch (command) {
                    case BYE:
                        validSession = false;
                        break;
                    case LIST:
                        printList();
                        break;
                    case MARK:
                        try {
                            int indexMark = Integer.parseInt(inputArgs[1]) - 1;
                            markAsDone(indexMark);
                        } catch (Exception e) {
                            System.out.println("Give a valid index mortal");
                        } 
                        break;
                        
                    case UNMARK:
                        try {
                            int indexUnmark = Integer.parseInt(inputArgs[1]) - 1;
                            markAsUndone(indexUnmark);
                        } catch (Exception e) {
                            System.out.println("Give a valid index mortal");
                        }
                        break;
                    case TODO:
                        try {
                            String todoDescription = input.substring("todo".length()).trim();
                            if (todoDescription.trim().length() < 1) {
                                throw new Exception("Invalid arguments");
                            }
                            Todo todo = new Todo(todoDescription);
                            addTask(todo);
                        } catch (Exception e) {
                            System.out.println("The description cannot be empty mortal!");
                        }
                        break;
                    case EVENT:
                        try {
                            String eventArgs = input.substring("event".length()).trim();
                            int fromIndex = eventArgs.indexOf("/from");
                            int toIndex = eventArgs.indexOf("/to");

                            String eventDescription = eventArgs.substring(0, fromIndex);
                            String fromString = eventArgs.substring(fromIndex + "/from".length(), toIndex);
                            String toString = eventArgs.substring(toIndex + "/to".length());
                            LocalDateTime from = DateParser.parseDateTime(fromString.trim(), LocalTime.MIDNIGHT);
                            LocalDateTime to = DateParser.parseDateTime(toString.trim(), LocalTime.of(23, 59));
                            if (to.isBefore(from)) {
                                throw new IllegalArgumentException("Event end cannot be before its start");
                            }
                            Event event = new Event(eventDescription.trim(), from, to);
                            addTask(event);
                        } catch (DateTimeParseException e) {
                            System.out.println("Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!");
                        } catch (Exception e) {
                            System.out.println("Give me good arguments mortal!");
                        }
                        break;
                    case DEADLINE:
                        try {
                            String deadlineArgs = input.substring("deadline".length()).trim();
                            int deadlineIndex = deadlineArgs.indexOf("/by");

                            String deadlineDescription = deadlineArgs.substring(0, deadlineIndex);
                            String deadlineString = deadlineArgs.substring(deadlineIndex + "/by".length());
                            LocalDateTime by = DateParser.parseDateTime(deadlineString.trim(), LocalTime.of(23, 59));
                            Deadline deadline = new Deadline(deadlineDescription.trim(), by);
                            addTask(deadline);
                        } catch (DateTimeParseException e) {
                            System.out.println("Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!");
                        } catch (Exception e) {
                            System.out.println("Give me good arguments mortal!");
                        }
                        break;
                    case DUETODAY:
                        dueToday();
                        break;
                    case ONGOINGNOW:
                        ongoingNow();
                        break;
                    case DUETHISDATE:
                        try {
                            String dateString = input.substring("dueThisDate".length()).trim();
                            dueThisDate(DateParser.parseDate(dateString));
                        } catch (DateTimeParseException e) {
                            System.out.println("Use a date in d/M/yyyy format, mortal!");
                        }
                        break;
                    case DELETE:
                        try {
                            int indexDelete = Integer.parseInt(inputArgs[1]) - 1;
                            deleteTask(indexDelete);
                        } catch (Exception e) {
                            System.out.println("Give a valid index mortal");
                        }
                        break;
            }
        } catch (Exception e) {
            printErrorMessage();
        } finally { 
            printSeparator();
            }
        }
        System.out.println(exit);
        printBarrier();
        scanner.close();
        saveTasks();
    }

    public static void printBarrier() {
        System.out.println("=======================================================================");
    } 

    public static void printSeparator() {
        System.out.println("-----------------------------------------------------------------------");
    }

    public static void printErrorMessage() {
        System.out.println("Invalid command given, try again mortal.");
    }

    public static void printMarkChanges(Task task) {
        System.out.println(String.format("Your prayers are heard child, I have %s the specified task:", task.isDone ? "marked" : "unmarked"));
        System.out.println(task);
    }

    public static void printAddedTask(Task task) {
        System.out.println("Understood child, adding to your task list:");
        System.out.println(task);
    }

    public static void printDeleteTask(Task task) {
        System.out.println("Understood young one, I have removed this task:");
        System.out.println(task);
        System.out.println(String.format("You now have %d tasks left.", record.size()));

    }

    //Apollo save/load info
    public static void saveTasks() {
        Path folder = Path.of("data");
        Path file = folder.resolve("apollo.txt");

        try {
            Files.createDirectories(folder);
            List<String> lines = new ArrayList<>();

            for (Task task : record) {
                lines.add(task.toFileString());
            }

            Files.write(file, lines);

        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }

    public static void loadTasks() {
        Path file = Path.of("data", "apollo.txt");

        if (!Files.exists(file)) {
            return;
        }

        try {
            List<String> lines = Files.readAllLines(file);

            for (String line : lines) {
                String[] parts = line.split("\\s*\\|\\s*");

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = null;

                switch (type) {
                case "T":
                    task = new Todo(description);
                    break;

                case "D":
                    task = new Deadline(description,
                            LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    break;

                case "E":
                    task = new Event(description,
                            LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    break;

                default:
                    break;
                }

                if (task != null) {
                        task.markAsDone(isDone);
                    record.add(task);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }


    //Apollo Command functions
    
    //list: directly prints the wanted output
    public static void printList() {
        System.out.println("Here are your current tasks child: ");
        for (int i = 0; i < record.size(); i++) {
            System.out.println(String.format("%d. %s", i+1, record.get(i)));
        }
    }

    //add task
    public static void addTask(Task task) {
        record.add(task);
        printAddedTask(task);
    }

    //mark
    public static void markAsDone(int index) {
        Task currTask = record.get(index);
        currTask.markAsDone(true);
        printMarkChanges(currTask);
    }

    //unmark
    public static void markAsUndone(int index) {
        Task currTask = record.get(index);
        currTask.markAsDone(false);
        printMarkChanges(currTask);
    }

    //delete
    public static void deleteTask(int index) {
        Task deleteTask = record.remove(index);
        printDeleteTask(deleteTask);
    }

    /** Shows all deadlines due on the current date. */
    public static void dueToday() {
        dueThisDate(LocalDate.now());
    }

    /** Shows all events whose time range includes the current time. */
    public static void ongoingNow() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Here are your ongoing events, child:");
        int count = 0;

        for (Task task : record) {
            if (task instanceof Event) {
                Event event = (Event) task;
                boolean hasStarted = !now.isBefore(event.getFrom());
                boolean hasNotEnded = !now.isAfter(event.getTo());
                if (hasStarted && hasNotEnded) {
                    count++;
                    System.out.println(String.format("%d. %s", count, event));
                }
            }
        }

        if (count == 0) {
            System.out.println("You have no ongoing events.");
        }
    }

    /**
     * Shows all deadlines due on the supplied date.
     *
     * @param date date for which deadlines should be shown
     */
    public static void dueThisDate(LocalDate date) {
        System.out.println(String.format("Here are your deadlines due on %s, child:", date));
        int count = 0;

        for (Task task : record) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.getBy().toLocalDate().equals(date)) {
                    count++;
                    System.out.println(String.format("%d. %s", count, deadline));
                }
            }
        }

        if (count == 0) {
            System.out.println("You have no deadlines due on this date.");
        }
    }
}
