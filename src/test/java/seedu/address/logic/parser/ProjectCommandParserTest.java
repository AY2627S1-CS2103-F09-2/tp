package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_NOT_IN_LIST;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectAllCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.commands.ProjectDeleteCommand;
import seedu.address.logic.commands.ProjectListCommand;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.commands.ProjectRoleCommand;
import seedu.address.logic.commands.ProjectViewCommand;
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
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCommandParser.MESSAGE_USAGE));
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
        for (String args : List.of("unknown Client Portal", "Client Portal", "createClient Portal", "deletePortal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCommandParser.MESSAGE_USAGE));
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

    @Test
    public void parse_addSubcommand_returnsProjectAddCommand() {
        ProjectAddCommand expectedCommand = new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Client Portal"));
        assertParseSuccess(parser, "add 1 Client Portal", expectedCommand);
        assertParseSuccess(parser, " \t AdD \t 1 \t Client   Portal \t ", expectedCommand);
    }

    @Test
    public void parse_invalidAddArguments_throwsParseException() {
        for (String args : List.of("add", "add 1", "add 1   ", "add abc Client Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE));
        }
        assertParseFailure(parser, "add 0 Client Portal", MESSAGE_INDEX_NOT_IN_LIST);
    }

    @Test
    public void parse_removeSubcommand_returnsProjectRemoveCommand() {
        ProjectRemoveCommand expectedCommand =
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Client Portal"));
        assertParseSuccess(parser, "remove 1 Client Portal", expectedCommand);
        assertParseSuccess(parser, " \t ReMoVe \t 1 \t Client   Portal \t ", expectedCommand);
    }

    @Test
    public void parse_invalidRemoveArguments_throwsParseException() {
        for (String args : List.of("remove", "remove 1", "remove 1   ", "remove abc Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
        }
        assertParseFailure(parser, "remove 0 Portal", MESSAGE_INDEX_NOT_IN_LIST);
    }

    @Test
    public void parse_deleteSubcommand_returnsProjectDeleteCommand() {
        ProjectDeleteCommand expectedCommand = new ProjectDeleteCommand(new Project("Client Portal"));
        assertParseSuccess(parser, "delete Client Portal", expectedCommand);
        assertParseSuccess(parser, " \t DeLeTe \t Client   Portal \t ", expectedCommand);
    }

    @Test
    public void parse_missingProjectNameForDelete_throwsParseException() {
        for (String args : List.of("delete", "delete   ", " delete \t ")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectDeleteCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_listSubcommand_returnsProjectListCommand() {
        ProjectListCommand expectedCommand = new ProjectListCommand(INDEX_FIRST_PERSON);
        assertParseSuccess(parser, "list 1", expectedCommand);
        assertParseSuccess(parser, " \t LiSt \t 1 \t ", expectedCommand);
    }

    @Test
    public void parse_invalidListArguments_throwsParseException() {
        for (String args : List.of("list", "list   ", "list abc", "list 1 Client Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE));
        }
        assertParseFailure(parser, "list 0", MESSAGE_INDEX_NOT_IN_LIST);
    }

    @Test
    public void parse_roleSubcommand_returnsProjectRoleCommand() {
        ProjectRoleCommand expectedCommand = new ProjectRoleCommand(
                INDEX_FIRST_PERSON, new Project("Client Portal"), "developer");
        assertParseSuccess(parser, "role 1 Client Portal r/developer", expectedCommand);
        assertParseSuccess(parser, " \t RoLe \t 1 \t Client   Portal r/UI ",
                new ProjectRoleCommand(INDEX_FIRST_PERSON, new Project("Client Portal"), "UI"));
    }

    @Test
    public void parse_invalidRoleArguments_throwsParseException() {
        for (String args : List.of("role", "role 1 Client Portal")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE));
        }
        assertParseFailure(parser, "role 0 Client Portal r/UI", MESSAGE_INDEX_NOT_IN_LIST);
    }

    @Test
    public void parse_viewSubcommand_returnsProjectViewCommand() {
        assertParseSuccess(parser, "view Portal", new ProjectViewCommand(new Project("Portal")));
        ProjectViewCommand expectedCommand = new ProjectViewCommand(new Project("Client Portal"));
        assertParseSuccess(parser, "view Client Portal", expectedCommand);
        assertParseSuccess(parser, " \t ViEw \t Client   Portal \t ", expectedCommand);
    }

    @Test
    public void parse_viewNameContainingCommandWordsAndPrefixes_returnsProjectViewCommand() {
        assertParseSuccess(parser, "view add client n/Website - Phase 1",
                new ProjectViewCommand(new Project("add client n/Website - Phase 1")));
    }

    @Test
    public void parse_allSubcommand_returnsProjectAllCommand() {
        assertParseSuccess(parser, "all", new ProjectAllCommand());
        assertParseSuccess(parser, " \t AlL \t\r\n ", new ProjectAllCommand());
    }

    @Test
    public void parse_allSubcommandWithArguments_throwsParseException() {
        for (String args : List.of("all Client Portal", "all all", "all 1", "all s/active")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAllCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_projectNamedAll_returnsCommandForNamedProject() {
        Project project = new Project("all");
        assertParseSuccess(parser, "create all", new ProjectCreateCommand(project));
        assertParseSuccess(parser, "view all", new ProjectViewCommand(project));
        assertParseSuccess(parser, "add 1 all", new ProjectAddCommand(INDEX_FIRST_PERSON, project));
        assertParseSuccess(parser, "remove 1 all", new ProjectRemoveCommand(INDEX_FIRST_PERSON, project));
        assertParseSuccess(parser, "delete all", new ProjectDeleteCommand(project));
    }

    @Test
    public void parse_missingProjectNameForView_throwsParseException() {
        for (String args : List.of("view", "view   ", " view \t\r\n ")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectViewCommand.MESSAGE_USAGE));
        }
    }
}
