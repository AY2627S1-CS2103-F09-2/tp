package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ProjectRoleCommand;
import seedu.address.model.project.Project;

public class ProjectRoleCommandParserTest {

    private final ProjectRoleCommandParser parser = new ProjectRoleCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsProjectRoleCommand() {
        assertParseSuccess(parser, "1 Client Portal r/developer",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "developer"));
        assertParseSuccess(parser, "1 Client   Portal r/UI",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "UI"));
        assertParseSuccess(parser, "1 Client Portal r/Lead   developer",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "Lead developer"));
        assertParseSuccess(parser, "1 Client Portal r/developer/lead",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "developer/lead"));
    }

    @Test
    public void parse_extraWhitespace_returnsProjectRoleCommand() {
        assertParseSuccess(parser, " \t 1 \t Client   Portal\tr/Lead\n developer \t ",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "Lead developer"));
        assertParseSuccess(parser, "1 Client\tPortal r/developer",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client\tPortal"), "developer"));
    }

    @Test
    public void parse_missingOrInvalidRole_throwsParseException() {
        List<String> invalidArgs = List.of("", "1", "1 Client Portal", "1 Client Portal role/developer",
                "1 Client Portal r/");
        for (String args : invalidArgs) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_repeatedRole_throwsParseException() {
        for (String roles : List.of("r/dev r/lead", "r/dev r/dev", "r/ r/lead", "r/dev r/", "r/ r/",
                "r/dev r/lead r/tester", "r/dev\tr/lead", "r/dev\nr/lead")) {
            assertParseFailure(parser, "1 Client Portal " + roles,
                    getErrorMessageForDuplicatePrefixes(PREFIX_ROLE));
        }
    }
}
