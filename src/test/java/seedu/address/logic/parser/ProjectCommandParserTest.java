package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.model.project.Project;

public class ProjectCommandParserTest {

    private final ProjectCommandParser parser = new ProjectCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingSubcommand_throwsParseException() {
        for (String args : List.of("", "   ", " \t\r\n ")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_missingProjectName_throwsParseException() {
        for (String args : List.of("create", "create   ", " create \t\r\n ")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_unknownSubcommand_throwsParseException() {
        for (String args : List.of("unknown Client Portal", "Client Portal", "createClient Portal", "delete Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_singleWordName_returnsProjectCreateCommand() {
        assertParseSuccess(parser, "create Portal", new ProjectCreateCommand(new Project("Portal")));
    }

    @Test
    public void parse_nameWithSpaces_returnsProjectCreateCommand() {
        assertParseSuccess(parser, "create Client Portal", new ProjectCreateCommand(new Project("Client Portal")));
    }

    @Test
    public void parse_extraWhitespace_returnsProjectCreateCommand() {
        assertParseSuccess(parser, " \t create \t Client   Portal \t ",
                new ProjectCreateCommand(new Project("Client Portal")));
    }

    @Test
    public void parse_subcommandIgnoresCase_returnsProjectCreateCommand() {
        assertParseSuccess(parser, "CrEaTe Client Portal", new ProjectCreateCommand(new Project("Client Portal")));
    }

    @Test
    public void parse_nameContainingCommandWordsAndPrefixes_returnsProjectCreateCommand() {
        assertParseSuccess(parser, "create add client n/Website - Phase 1",
                new ProjectCreateCommand(new Project("add client n/Website - Phase 1")));
    }
}
