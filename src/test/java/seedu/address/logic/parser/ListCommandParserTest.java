package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.Status;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noStatus_returnsListAllCommand() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, "   ", new ListCommand());
    }

    @Test
    public void parse_otherParameters_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        // a filter list does not support, a status prefix in the wrong case, and stray text
        for (String args : new String[] {" t/friends", " S/active", " 3", " abc", " extra s/active"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_status_returnsFilteredListCommand() {
        assertParseSuccess(parser, " s/active", new ListCommand(Status.ACTIVE));
        assertParseSuccess(parser, " s/On Hold", new ListCommand(Status.ON_HOLD));
        assertParseSuccess(parser, " s/none", new ListCommand(Status.NONE));
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        assertParseFailure(parser, " s/pending", Status.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " s/", Status.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedStatus_throwsParseException() {
        assertParseFailure(parser, " s/active s/done", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS));
    }
}
