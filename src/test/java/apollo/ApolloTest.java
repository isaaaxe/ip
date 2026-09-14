package apollo;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ApolloTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_addTodo_returnsResponseAndPersistsTask() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);

        String addResponse = apollo.getResponse("todo read book");
        Apollo reloadedApollo = new Apollo(storagePath);
        String listResponse = reloadedApollo.getResponse("list");

        Assertions.assertTrue(addResponse.contains("read book"));
        Assertions.assertEquals(ResponseType.ADD, apollo.getResponseType());
        Assertions.assertTrue(listResponse.contains("read book"));
    }

    @Test
    public void getResponse_bye_requestsExit() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());

        String response = apollo.getResponse("bye");

        Assertions.assertTrue(apollo.isExitRequested());
        Assertions.assertTrue(response.contains("west wind"));
    }

    @Test
    public void getResponse_undoAdd_removesTaskAndPersistsChange() {
        String storagePath = tempDir.resolve("tasks.txt").toString();
        Apollo apollo = new Apollo(storagePath);
        Assertions.assertFalse(apollo.canUndo());

        apollo.getResponse("todo read book");
        Assertions.assertTrue(apollo.canUndo());

        String undoResponse = apollo.getResponse("undo");
        Apollo reloadedApollo = new Apollo(storagePath);

        Assertions.assertTrue(undoResponse.contains("undone"));
        Assertions.assertFalse(apollo.canUndo());
        Assertions.assertFalse(reloadedApollo.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_undoDelete_restoresTaskAtOriginalPosition() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo first");
        apollo.getResponse("todo second");
        apollo.getResponse("todo third");
        apollo.getResponse("delete 2");

        apollo.getResponse("undo");
        String listResponse = apollo.getResponse("list");

        Assertions.assertTrue(listResponse.indexOf("first") < listResponse.indexOf("second"));
        Assertions.assertTrue(listResponse.indexOf("second") < listResponse.indexOf("third"));
    }

    @Test
    public void getResponse_undoMark_restoresPreviousCompletionState() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo read book");
        apollo.getResponse("mark 1");

        apollo.getResponse("undo");
        String listResponse = apollo.getResponse("list");

        Assertions.assertTrue(listResponse.contains("[T] [ ] read book"));
    }

    @Test
    public void getResponse_sixAdds_onlyFiveNewestActionsCanBeUndone() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        for (int i = 1; i <= 6; i++) {
            apollo.getResponse("todo task " + i);
        }

        for (int i = 0; i < 5; i++) {
            apollo.getResponse("undo");
        }
        String emptyHistoryResponse = apollo.getResponse("undo");

        Assertions.assertTrue(emptyHistoryResponse.contains("nothing to undo"));
        Assertions.assertEquals(ResponseType.ERROR, apollo.getResponseType());

        String listResponse = apollo.getResponse("list");
        Assertions.assertTrue(listResponse.contains("task 1"));
        Assertions.assertFalse(listResponse.contains("task 2"));
    }

    @Test
    public void getResponse_markWithoutStateChange_doesNotConsumeUndoHistory() {
        Apollo apollo = new Apollo(tempDir.resolve("tasks.txt").toString());
        apollo.getResponse("todo read book");
        apollo.getResponse("mark 1");
        apollo.getResponse("mark 1");

        apollo.getResponse("undo");
        apollo.getResponse("undo");
        String emptyHistoryResponse = apollo.getResponse("undo");

        Assertions.assertTrue(emptyHistoryResponse.contains("nothing to undo"));
    }
}
