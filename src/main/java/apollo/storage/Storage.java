package apollo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.Todo;

/** Loads tasks from and saves tasks to a text file. */
public class Storage {
    private final Path filePath;

    /** Creates a storage manager for the specified file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return the loaded tasks, or an empty list if the file does not exist
     * @throws IOException if the file cannot be read
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(this.filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(this.filePath)) {
            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Saves all tasks to the storage file.
     *
     * @param tasks tasks to serialize
     * @throws IOException if the file or its parent directory cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentFolder = this.filePath.getParent();
        if (parentFolder != null) {
            Files.createDirectories(parentFolder);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }
        Files.write(this.filePath, lines);
    }

    /** Converts one line from the storage format into a task. */
    private Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        Task task;

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
                return null;
        }

        task.markAsDone(isDone);
        return task;
    }
}
