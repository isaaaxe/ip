import java.util.Scanner;

public class Apollo {
    final static String greeting = "Greetings young mortal! Apollo here to answer any queries under the sun!";
    final static String exit = "To the end of the west wind, where fresh flowers bloom.";
    static boolean validSession = true;
    static Task[] record  = new Task[100];
    static Scanner scanner;
    static int listCount = 0;

    public static void main(String[] args) {
        //new scanner object
        scanner = new Scanner(System.in);

        //main process
        printBarrier();
        System.out.println(greeting);
        while (validSession) {
            printBarrier();
            String fullCommand = scanner.nextLine();
            String[] commandArgs = fullCommand.trim().split(" ");
            //command switch case
            if (commandArgs[0].equals("bye")) {
                validSession = false;
            } else if (commandArgs[0].equals("list")) {
                for (int i = 0; i < listCount; i++) {
                    System.out.print(String.format("%d. ", i+1));
                    printTask(record[i]);
                }
            } else if (commandArgs[0].equals("mark")) {
                try {
                    int index = Integer.parseInt(commandArgs[1]) - 1;
                    Task task = record[index];
                    task.markAsDone();
                    printMarkChanges(task.isDone);
                    printTask(task);
                } catch (Exception e) {
                    printErrorMessage();
                }
                 
            } else if (commandArgs[0].equals("unmark")) { 
                try {
                    int index = Integer.parseInt(commandArgs[1]) - 1;
                    Task task = record[index];
                    task.markAsUndone();     
                    printMarkChanges(task.isDone);
                    printTask(task);
                } catch (Exception e) {
                    printErrorMessage();
                }
            } else {
                record[listCount] = new Task(fullCommand);
                listCount += 1;
                printCommand(fullCommand);
            }

        }
        printBarrier();

        System.out.println(exit);
        scanner.close();
    }

    public static void printBarrier() {
        System.out.println("=======================================================================");
    } 
    public static void printCommand(String command) {
        System.out.println("    added: " + command);
    }

    public static void printTask(Task task) {
        System.out.println(String.format("[%s] %s",task.getStatusIcon(), task.description));
    }

    public static void printErrorMessage() {
        System.out.println("Invalid command passed, please try again.");
    }

    public static void printMarkChanges(boolean isMarked) {
        System.out.println(String.format("Your prayers are heard child, I have %s the specified task:", isMarked ? "marked" : "unmarked"));
    } 
}
