package seedu.address.logic.parser;

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
        // other text after list is ignored, as before
        assertParseSuccess(parser, " 3", new ListCommand());
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
