package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameOrCompanyContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_resetView_returnsShowAllCommand() {
        FindCommand expectedCommand = new FindCommand(PREDICATE_SHOW_ALL_PERSONS);
        assertParseSuccess(parser, "reset view", expectedCommand);
        assertParseSuccess(parser, " \n ReSeT \n \t ViEw  \t", expectedCommand);
    }

    @Test
    public void parse_otherResetKeywords_returnsNameSearch() {
        assertParseSuccess(parser, "reset",
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("reset"))));
        assertParseSuccess(parser, "view",
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("view"))));
        assertParseSuccess(parser, "view reset",
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("view", "reset"))));
        assertParseSuccess(parser, "reset view Alice",
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("reset", "view", "Alice"))));
        assertParseSuccess(parser, "reset views",
                new FindCommand(new NameOrCompanyContainsKeywordsPredicate(List.of("reset", "views"))));
    }

}
