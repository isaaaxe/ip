package apollo.parser;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import apollo.parser.Parser.Command;
import apollo.task.Deadline;

public class ParseTest {
    
    @Test
    public void parseCommand_differentCapitalisation_returnsCorrectCommand() {
        Parser parser = new Parser();

        Assertions.assertEquals(Command.LIST, parser.parseCommand("LIST"));
        Assertions.assertEquals(Command.LIST, parser.parseCommand("list"));
        Assertions.assertEquals(Command.LIST, parser.parseCommand("liST"));
    }

    @Test
    public void parseIndex_userFacingIndex_returnsZeroBasedIndex() {
        Parser parser = new Parser();

        Assertions.assertEquals(0, parser.parseIndex("mark 1"));
        Assertions.assertEquals(2, parser.parseIndex("delete 3"));
    }

    @Test
    public void parseDeadline_validInput_returnsCorrectDeadline() {
        Parser parser = new Parser();

        Deadline deadline = parser.parseDeadline("deadline test /by 30/8/2026 1800");

        Assertions.assertEquals(
            LocalDateTime.of(2026, 8, 30, 18, 0), 
            deadline.getBy());
        Assertions.assertEquals(
            "D | 0 | test | 2026-08-30T18:00:00", 
            deadline.toFileString());
    }
}
