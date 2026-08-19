import java.util.Scanner;

public class Apollo {
    final static String greeting = "Greetings young mortal! Apollo here to answer any queries under the sun!";
    final static String exit = "To the end of the west wind, where fresh flowers bloom.";
    static boolean validSession = true;
    static Task[] record  = new Task[100];
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
        BYE
    };

    public static void main(String[] args) {
        //new scanner object
        scanner = new Scanner(System.in);

        //main process
        printBarrier();
        System.out.println(greeting);
        while (validSession) {
            
            String input = scanner.nextLine();
            String[] inputArgs = input.trim().split(" ");
            printSeparator();

            Command command = Command.valueOf(inputArgs[0].trim().toUpperCase());
            //command switch case
            switch (command) {
                case BYE:
                    validSession = false;
                    break;
                case LIST:
                    printList();
                    break;
                case MARK:
                    int indexMark = Integer.parseInt(inputArgs[1]) - 1;
                    markAsDone(indexMark);
                    break;
                case UNMARK:
                    int indexUnmark = Integer.parseInt(inputArgs[1]) - 1;
                    markAsUndone(indexUnmark);
                    break;
                case TODO:
                    String todoDescription = input.substring("todo".length()).trim();
                    Todo todo = new Todo(todoDescription);
                    addTask(todo);
                    break;
                case EVENT:
                    String eventArgs = input.substring("event".length()).trim();
                    int fromIndex = eventArgs.indexOf("/from");
                    int toIndex = eventArgs.indexOf("/to");

                    String eventDescription = eventArgs.substring(0, fromIndex);
                    String fromString = eventArgs.substring(fromIndex + "/from".length(), toIndex);
                    String toString = eventArgs.substring(toIndex + "/to".length());
                    Event event = new Event(eventDescription, fromString, toString);
                    addTask(event);

                    break;
                case DEADLINE:
                    String deadlineArgs = input.substring("deadline".length()).trim();
                    int deadlineIndex = deadlineArgs.indexOf("/by");

                    String deadlineDescription = deadlineArgs.substring(0, deadlineIndex);
                    String deadlineString = deadlineArgs.substring(deadlineIndex + "/by".length());
                    Deadline deadline  = new Deadline(deadlineDescription, deadlineString);
                    addTask(deadline);
                    break;
                case DELETE:
                    //not implemented yet
                    break;
            }
            printSeparator();

            // if (commandArgs[0].equals("bye")) {
            //     validSession = false;
            // } else if (commandArgs[0].equals("list")) {
            //     for (int i = 0; i < listCount; i++) {
            //         System.out.print(String.format("%d. ", i+1));
            //         printTask(record[i]);
            //     }
            // } else if (commandArgs[0].equals("mark")) {
            //     try {
            //         int index = Integer.parseInt(commandArgs[1]) - 1;
            //         Task task = record[index];
            //         task.markAsDone();
            //         printMarkChanges(task.isDone);
            //         printTask(task);
            //     } catch (Exception e) {
            //         printErrorMessage();
            //     }
                 
            // } else if (commandArgs[0].equals("unmark")) { 
            //     try {
            //         int index = Integer.parseInt(commandArgs[1]) - 1;
            //         Task task = record[index];
            //         task.markAsUndone();     
            //         printMarkChanges(task.isDone);
            //         printTask(task);
            //     } catch (Exception e) {
            //         printErrorMessage();
            //     }
            // } else {
            //     record[listCount] = new Task(fullCommand);
            //     listCount += 1;
            //     printCommand(fullCommand);
            // }

        }
        System.out.println(exit);
        printBarrier();
        scanner.close();
    }

    public static void printBarrier() {
        System.out.println("=======================================================================");
    } 

    public static void printSeparator() {
        System.out.println("-----------------------------------------------------------------------");
    }

    public static void printErrorMessage() {
        System.out.println("Invalid command passed, please try again.");
    }

    public static void printMarkChanges(Task task) {
        System.out.println(String.format("Your prayers are heard child, I have %s the specified task:", task.isDone ? "marked" : "unmarked"));
        System.out.println(task);
    }

    public static void printAddedTask(Task task) {
        System.out.println("Understood child, adding to your task list:");
        System.out.println(task);
    }

    //Apollo Command functions
    
    //list: directly prints the wanted output
    public static void printList() {
        for (int i = 0; i < listCount; i++) {
            System.out.println(String.format("%d. %s", i+1, record[i]));
        }
    }

    //add task
    public static void addTask(Task task) {
        record[listCount] = task;
        listCount += 1;
        printAddedTask(task);
    }

    //mark
    public static void markAsDone(int index) {
        Task currTask = record[index];
        currTask.markAsDone(true);
        printMarkChanges(currTask);
    }

    //unmark
    public static void markAsUndone(int index) {
        Task currTask = record[index];
        currTask.markAsDone(false);
        printMarkChanges(currTask);
    }

    //delete
    public static void deleteTask(int index) {
        //not implemented yet
    }
}
