import java.util.List;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class Apollo {
    static boolean validSession = true;
    static TaskList tasks = new TaskList();
    static Storage storage = new Storage("data/apollo.txt");
    static Parser parser = new Parser();
    static Ui ui = new Ui();

    public static void main(String[] args) {

        //load the apollo.txt task list to record
        loadTasks();

        //main process
        ui.showGreeting();
        while (validSession) {
            
            String input = ui.readCommand();
            ui.showSeparator();

            
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

    // Apollo save/load info
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


    //Apollo Command functions
    
    //list: directly prints the wanted output
    public static void printList() {
        ui.showTaskList(tasks);
    }

    //add task
    public static void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task);
    }

    //mark
    public static void markAsDone(int index) {
        Task currTask = tasks.get(index);
        currTask.markAsDone(true);
        ui.showMarkChange(currTask);
    }

    //unmark
    public static void markAsUndone(int index) {
        Task currTask = tasks.get(index);
        currTask.markAsDone(false);
        ui.showMarkChange(currTask);
    }

    //delete
    public static void deleteTask(int index) {
        Task deleteTask = tasks.delete(index);
        ui.showTaskDeleted(deleteTask, tasks.size());
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
