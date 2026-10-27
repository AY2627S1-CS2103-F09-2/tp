package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_NOT_IN_LIST;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.StatusCommand;
import seedu.address.model.person.Status;

public class StatusCommandParserTest {

    private final StatusCommandParser parser = new StatusCommandParser();

    @Test
    public void parse_validArgs_returnsStatusCommand() {
        assertParseSuccess(parser, "1 active", new StatusCommand(INDEX_FIRST_PERSON, Status.ACTIVE));
        assertParseSuccess(parser, " 2   On Hold ", new StatusCommand(INDEX_SECOND_PERSON, Status.ON_HOLD));
        assertParseSuccess(parser, "1 on-hold", new StatusCommand(INDEX_FIRST_PERSON, Status.ON_HOLD));
        assertParseSuccess(parser, "1 none", new StatusCommand(INDEX_FIRST_PERSON, Status.NONE));
    }

    @Test
    public void parse_missingParts_throwsParseException() {
        for (String args : List.of("", "   ", "1", "active")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "abc active",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE));
        for (String args : List.of("0 active", "-1 active")) {
            assertParseFailure(parser, args, MESSAGE_INDEX_NOT_IN_LIST);
        }
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        assertParseFailure(parser, "1 pending", Status.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 active now", Status.MESSAGE_CONSTRAINTS);
    }
}
