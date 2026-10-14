package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectListCommand;

public class ProjectListCommandParserTest {

    private final ProjectListCommandParser parser = new ProjectListCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validIndex_returnsProjectListCommand() {
        ProjectListCommand expectedCommand = new ProjectListCommand(INDEX_FIRST_PERSON);
        assertParseSuccess(parser, "1", expectedCommand);
        assertParseSuccess(parser, " \t 1 \t ", expectedCommand);
        assertParseSuccess(parser, Integer.toString(Integer.MAX_VALUE),
                new ProjectListCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        for (String args : List.of("", "   ", " \t\r\n ")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String args : List.of("0", "-1", "+1", "1.5", "abc", "2147483648", "99999999999999999999")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        for (String args : List.of("1 2", "1 Client Portal", "1 n/Alice")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE));
        }
    }
}
