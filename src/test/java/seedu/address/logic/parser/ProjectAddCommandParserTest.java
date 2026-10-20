package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.model.project.Project;

public class ProjectAddCommandParserTest {

    private final ProjectAddCommandParser parser = new ProjectAddCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsProjectAddCommand() {
        assertParseSuccess(parser, "1 Portal", new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Portal")));
        assertParseSuccess(parser, "1 Client Portal",
                new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Client Portal")));
        assertParseSuccess(parser, "1,2 Client Portal",
                new ProjectAddCommand(List.of(INDEX_FIRST_PERSON, Index.fromOneBased(2)),
                        new Project("Client Portal")));
        assertParseSuccess(parser, "1 2026 Portal",
                new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("2026 Portal")));
        assertParseSuccess(parser, Integer.MAX_VALUE + " Client Portal",
                new ProjectAddCommand(Index.fromOneBased(Integer.MAX_VALUE), new Project("Client Portal")));
    }

    @Test
    public void parse_extraWhitespace_returnsProjectAddCommand() {
        assertParseSuccess(parser, " \t 1 \t Client   Portal \t ",
                new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Client Portal")));
    }

    @Test
    public void parse_missingArguments_throwsParseException() {
        for (String args : List.of("", "   ", " \t\r\n ", "1", "1   ", "1 \t\r\n ", "Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String index : List.of("0", "-1", "+1", "1.5", "abc", "2147483648", "99999999999999999999")) {
            assertParseFailure(parser, index + " Client Portal",
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_nameContainingCommandWordsAndPrefixes_returnsProjectAddCommand() {
        assertParseSuccess(parser, "1 create client n/Website - Phase 1",
                new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("create client n/Website - Phase 1")));
    }
}
