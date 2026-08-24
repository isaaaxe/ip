import java.util.Scanner;
import java.util.List;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Apollo {
    final static String greeting = "Greetings young mortal! Apollo here to answer any queries under the sun!";
    final static String exit = "To the end of the west wind, where fresh flowers bloom.";
    static boolean validSession = true;
    static TaskList tasks = new TaskList();
    static Storage storage = new Storage("data/apollo.txt");
    static Parser parser = new Parser();
    static Scanner scanner;
    static int listCount = 0;

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
            printSeparator();

            
            //command switch case
            try {
                Parser.Command command = parser.parseCommand(input);
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
                            System.out.println("Give a valid index mortal");
                        } 
                        break;
                        
                    case UNMARK:
                        try {
                            int indexUnmark = parser.parseIndex(input);
                            markAsUndone(indexUnmark);
                        } catch (Exception e) {
                            System.out.println("Give a valid index mortal");
                        }
                        break;
                    case TODO:
                        try {
                            addTask(parser.parseTodo(input));
                        } catch (Exception e) {
                            System.out.println("The description cannot be empty mortal!");
                        }
                        break;
                    case EVENT:
                        try {
                            addTask(parser.parseEvent(input));
                        } catch (DateTimeParseException e) {
                            System.out.println("Use dates in d/M/yyyy or d/M/yyyy HHmm format, mortal!");
                        } catch (Exception e) {
                            System.out.println("Give me good arguments mortal!");
                        }
                        break;
                    case DEADLINE:
                        try {
                            addTask(parser.parseDeadline(input));
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
                            dueThisDate(parser.parseDueDate(input));
                        } catch (DateTimeParseException e) {
                            System.out.println("Use a date in d/M/yyyy format, mortal!");
                        }
                        break;
                    case DELETE:
                        try {
                            int indexDelete = parser.parseIndex(input);
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
        System.out.println(String.format("You now have %d tasks left.", tasks.size()));

    }

    // Apollo save/load info
    public static void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            System.out.println("Could not save tasks.");
        }
    }

    public static void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }


    //Apollo Command functions
    
    //list: directly prints the wanted output
    public static void printList() {
        System.out.println("Here are your current tasks child: ");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(String.format("%d. %s", i+1, tasks.get(i)));
        }
    }

    //add task
    public static void addTask(Task task) {
        tasks.add(task);
        printAddedTask(task);
    }

    //mark
    public static void markAsDone(int index) {
        Task currTask = tasks.get(index);
        currTask.markAsDone(true);
        printMarkChanges(currTask);
    }

    //unmark
    public static void markAsUndone(int index) {
        Task currTask = tasks.get(index);
        currTask.markAsDone(false);
        printMarkChanges(currTask);
    }

    //delete
    public static void deleteTask(int index) {
        Task deleteTask = tasks.delete(index);
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
        List<Event> ongoingEvents = tasks.getEventsOngoingAt(now);
        for (int i = 0; i < ongoingEvents.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, ongoingEvents.get(i)));
        }

        if (ongoingEvents.isEmpty()) {
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
        List<Deadline> dueDeadlines = tasks.getDeadlinesDueOn(date);
        for (int i = 0; i < dueDeadlines.size(); i++) {
            System.out.println(String.format("%d. %s", i + 1, dueDeadlines.get(i)));
        }

        if (dueDeadlines.isEmpty()) {
            System.out.println("You have no deadlines due on this date.");
        }
    }
}
