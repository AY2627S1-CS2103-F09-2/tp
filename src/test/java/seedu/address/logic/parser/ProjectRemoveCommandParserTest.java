package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.model.project.Project;

public class ProjectRemoveCommandParserTest {

    private final ProjectRemoveCommandParser parser = new ProjectRemoveCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsProjectRemoveCommand() {
        assertParseSuccess(parser, "1 Portal", new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Portal")));
        assertParseSuccess(parser, "1 Client Portal",
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Client Portal")));
        assertParseSuccess(parser, "1,2 Client Portal",
                new ProjectRemoveCommand(List.of(INDEX_FIRST_PERSON, Index.fromOneBased(2)),
                        new Project("Client Portal")));
        assertParseSuccess(parser, "1 2026 Portal",
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("2026 Portal")));
        assertParseSuccess(parser, Integer.MAX_VALUE + " Client Portal",
                new ProjectRemoveCommand(Index.fromOneBased(Integer.MAX_VALUE), new Project("Client Portal")));
    }

    @Test
    public void parse_extraWhitespace_returnsProjectRemoveCommand() {
        assertParseSuccess(parser, " \t 1 \t Client   Portal \t ",
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Client Portal")));
    }

    @Test
    public void parse_missingArguments_throwsParseException() {
        for (String args : List.of("", "   ", " \t\r\n ", "1", "1   ", "1 \t\r\n ", "Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String index : List.of("0", "-1", "+1", "1.5", "abc", "2147483648", "99999999999999999999")) {
            assertParseFailure(parser, index + " Client Portal",
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_nameContainingCommandWordsAndPrefixes_returnsProjectRemoveCommand() {
        assertParseSuccess(parser, "1 add client n/Website - Phase 1",
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("add client n/Website - Phase 1")));
    }
}
