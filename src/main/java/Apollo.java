import java.util.Scanner;

public class Apollo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String greeting = "Greetings young mortal! Apollo here to answer any queries under the sun!";
        String exit = "To the end of the west wind, where fresh flowers bloom.";
        boolean validSession = true;
        printBarrier();
        System.out.println(greeting);
        while (validSession) {
            printBarrier();
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                validSession = false;
            } else {
                printCommand(command);
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
        System.out.println("    " + command);
    }
}
