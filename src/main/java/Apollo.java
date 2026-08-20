import java.util.Scanner;
import java.util.ArrayList;

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
                            Event event = new Event(eventDescription, fromString, toString);
                            addTask(event);
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
                            Deadline deadline  = new Deadline(deadlineDescription, deadlineString);
                            addTask(deadline);
                        } catch (Exception e) {
                            System.out.println("Give me good arguments mortal!");
                        }
                        break;
                    case DELETE:
                        //not implemented yet
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
        //not implemented yet
    }
}
