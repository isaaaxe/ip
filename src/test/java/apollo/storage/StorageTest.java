package apollo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws IOException {
        // Setting up
        Path missingFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(missingFile.toString());

        // Action to test
        List<Task> tasks = storage.load();

        // Assertion
        Assertions.assertTrue(tasks.isEmpty());
    }

    @Test
    public void save_todo_writesExpectedFileContent() throws IOException {
        // Setting up
        Path tempFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(tempFile.toString());
        Todo todo = new Todo("test 1");

        // Action to test
        storage.save(List.of(todo));

        // Assertion
        List<String> lines = Files.readAllLines(tempFile);
        Assertions.assertEquals(List.of("T | 0 | test 1"), lines);
    }

    @Test
    public void load_todoFile_returnsTodo() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("T | 0 | test todo"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Todo.class, tasks.get(0));
        Assertions.assertEquals("T | 0 | test todo", tasks.get(0).toFileString());
    }

    @Test
    public void load_eventFile_returnsEvent() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("E | 0 | test event | 2026-08-24T00:00:00 | 2026-08-25T23:59:00"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Event.class, tasks.get(0));
        Assertions.assertEquals(
                "E | 0 | test event | 2026-08-24T00:00:00 | 2026-08-25T23:59:00",
                tasks.get(0).toFileString());
    }

    @Test
    public void load_deadlineFile_returnsDeadline() throws IOException {
        Path tempFile = tempDir.resolve("tasks.txt");
        Files.write(tempFile, List.of("D | 0 | test deadline | 2026-08-25T23:59:00"));
        Storage storage = new Storage(tempFile.toString());

        List<Task> tasks = storage.load();

        Assertions.assertEquals(1, tasks.size());
        Assertions.assertInstanceOf(Deadline.class, tasks.get(0));
        Assertions.assertEquals("D | 0 | test deadline | 2026-08-25T23:59:00", tasks.get(0).toFileString());
    }
}
