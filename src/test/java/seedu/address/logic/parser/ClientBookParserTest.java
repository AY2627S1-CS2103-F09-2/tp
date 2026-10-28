package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INDEX_NOT_IN_LIST;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectAllCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.commands.ProjectListCommand;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.commands.ProjectViewCommand;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameOrCompanyContainsKeywordsPredicate;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.project.Project;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class ClientBookParserTest {

    private final ClientBookParser parser = new ClientBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameOrCompanyContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_note() throws Exception {
        assertEquals(new NoteCommand(INDEX_FIRST_PERSON, new Note("Prefers email")),
                parser.parseCommand(NoteCommand.COMMAND_WORD + " 1 Prefers email"));
        assertEquals(new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY), parser.parseCommand("NOTE 1"));
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
        assertEquals(new ListCommand(Status.ACTIVE), parser.parseCommand(ListCommand.COMMAND_WORD + " s/active"));
    }

    @Test
    public void parseCommand_status() throws Exception {
        assertEquals(new StatusCommand(INDEX_FIRST_PERSON, Status.ON_HOLD),
                parser.parseCommand(StatusCommand.COMMAND_WORD + " 1 on hold"));
        assertEquals(new StatusCommand(INDEX_FIRST_PERSON, Status.DONE), parser.parseCommand("STATUS 1 done"));
    }

    @Test
    public void parseCommand_projectCreate() throws Exception {
        ProjectCreateCommand expectedCommand = new ProjectCreateCommand(new Project("Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand("project create Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand(" \tPrOjEcT CrEaTe  Client   Portal  "));
    }

    @Test
    public void parseCommand_invalidProjectCommand_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCommandParser.MESSAGE_USAGE);
        for (String input : List.of("project", "project unknown Portal")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_projectCreateMissingName_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE);
        for (String input : List.of("project create", "project create   ")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_projectAdd() throws Exception {
        ProjectAddCommand expectedCommand = new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand("project add 1 Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand(" \tPrOjEcT AdD  1  Client   Portal  "));
    }

    @Test
    public void parseCommand_invalidProjectAdd_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE);
        for (String input : List.of("project add", "project add 1", "project add abc Portal")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
        assertThrows(ParseException.class, MESSAGE_INDEX_NOT_IN_LIST, () ->
                parser.parseCommand("project add 0 Portal"));
    }

    @Test
    public void parseCommand_projectRemove() throws Exception {
        ProjectRemoveCommand expectedCommand =
                new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand("project remove 1 Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand(" \tPrOjEcT ReMoVe  1  Client   Portal  "));
    }

    @Test
    public void parseCommand_invalidProjectRemove_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE);
        for (String input : List.of("project remove", "project remove 1", "project remove abc Portal")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
        assertThrows(ParseException.class, MESSAGE_INDEX_NOT_IN_LIST, () ->
                parser.parseCommand("project remove 0 Portal"));
    }

    @Test
    public void parseCommand_projectAll() throws Exception {
        assertEquals(new ProjectAllCommand(), parser.parseCommand("project all"));
        assertEquals(new ProjectAllCommand(), parser.parseCommand(" \tPrOjEcT AlL  "));
    }

    @Test
    public void parseCommand_projectAllWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAllCommand.MESSAGE_USAGE);
        for (String input : List.of("project all all", "project all Client Portal", "project all 1")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_projectViewNamedAll() throws Exception {
        assertEquals(new ProjectViewCommand(new Project("all")), parser.parseCommand("project view all"));
    }

    @Test
    public void parseCommand_projectView() throws Exception {
        ProjectViewCommand expectedCommand = new ProjectViewCommand(new Project("Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand("project view Client Portal"));
        assertEquals(expectedCommand, parser.parseCommand(" \tPrOjEcT ViEw  Client   Portal  "));
    }

    @Test
    public void parseCommand_projectViewMissingName_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectViewCommand.MESSAGE_USAGE);
        for (String input : List.of("project view", "project view   ")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_projectList() throws Exception {
        ProjectListCommand expectedCommand = new ProjectListCommand(INDEX_FIRST_PERSON);
        assertEquals(expectedCommand, parser.parseCommand("project list 1"));
        assertEquals(expectedCommand, parser.parseCommand(" \tPrOjEcT LiSt  1  "));
    }

    @Test
    public void parseCommand_invalidProjectList_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE);
        for (String input : List.of("project list", "project list abc", "project list 1 Portal")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
        assertThrows(ParseException.class, MESSAGE_INDEX_NOT_IN_LIST, () -> parser.parseCommand("project list 0"));
    }

    @Test
    public void parseCommand_commandWordsIgnoreCase() throws Exception {
        Person person = new PersonBuilder().build();
        assertTrue(parser.parseCommand("LIST") instanceof ListCommand);
        assertEquals(new AddCommand(person), parser.parseCommand(PersonUtil.getAddCommand(person)
                .replaceFirst("add", "Add")));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
