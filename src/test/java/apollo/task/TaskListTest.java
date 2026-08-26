package apollo.task;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;


public class TaskListTest {
    @TempDir
    Path tempDir;

    @Test
    public void add_addTodo_todoInTaskList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("todo");

        taskList.add(todo);

        //assert
        Assertions.assertEquals(1, taskList.size());
        Assertions.assertSame(todo, taskList.get(0));
    }

    @Test
    public void delete_deleteMiddleTask_returnDeletedTask() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("todo");

        LocalDateTime from = LocalDateTime.parse("2026-08-24T00:00:00");
        LocalDateTime to = LocalDateTime.parse("2026-08-25T23:59:00");
        Event event = new Event("event", from, to);

        Deadline deadline = new Deadline("deadline", to);

        taskList.add(todo);
        taskList.add(event);
        taskList.add(deadline);

        Task removedTask = taskList.delete(1);

        //assert
        Assertions.assertEquals(2, taskList.size());
        Assertions.assertSame(todo, taskList.get(0));
        Assertions.assertSame(deadline, taskList.get(1));
        Assertions.assertSame(removedTask, event);
    }

    @Test
    public void getDeadlinesDueOn_mixedTasks_returnOnlyMatchingDeadlines() {
        TaskList taskList = new TaskList();

        LocalDateTime aug25 = LocalDateTime.parse("2026-08-25T23:59:00");
        LocalDateTime aug24 = LocalDateTime.parse("2026-08-24T00:00:00");

        Todo todo = new Todo("dummy");
        Deadline matchingDeadline = new Deadline("deadline1", aug25);
        Deadline differentDeadline = new Deadline("deadline2", aug24);

        taskList.add(matchingDeadline);
        taskList.add(differentDeadline);
        taskList.add(todo);

        List<Deadline> results = taskList.getDeadlinesDueOn(LocalDate.of(2026, 8, 25));

        Assertions.assertSame(matchingDeadline, results.get(0));
    }
}
