package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.storage.JsonClientBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonClientBookStorage clientBookStorage =
                new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(clientBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_EMPTY, model);
    }

    @Test
    public void execute_projectCreate_savesProject() throws Exception {
        Project project = new Project("Client Portal");
        Model expectedModel = new ModelManager();
        expectedModel.addProject(project);

        assertCommandSuccess("project create   Client   Portal  ",
                String.format(ProjectCreateCommand.MESSAGE_SUCCESS, project), expectedModel);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        ReadOnlyClientBook savedClientBook = storage.readClientBook().orElseThrow();
        assertEquals(model.getClientBook(), savedClientBook);
        assertEquals("Client Portal", savedClientBook.getProjectList().get(0).getName());
    }

    @Test
    public void execute_projectCreateDuplicate_throwsCommandException() throws Exception {
        logic.execute("project create Client Portal");
        assertCommandException("project create   client   portal  ", ProjectCreateCommand.MESSAGE_DUPLICATE_PROJECT);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        assertEquals(model.getClientBook(), storage.readClientBook().orElseThrow());
    }

    @Test
    public void execute_projectCreateMissingName_throwsParseException() {
        assertParseException("project create   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_projectAdd_savesMembershipWithExistingProjectName() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        Project project = new Project("Client Portal");
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(AMY, project));

        assertCommandSuccess("project add 1   client   portal  ",
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, AMY.getName(), project), expectedModel);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        ReadOnlyClientBook savedClientBook = storage.readClientBook().orElseThrow();
        assertEquals(model.getClientBook(), savedClientBook);
        assertEquals("Client Portal", savedClientBook.getClientProjectList().get(0).getProject().getName());
    }

    @Test
    public void execute_projectAddDuplicate_throwsCommandException() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        logic.execute("project add 1 Client Portal");
        assertCommandException("project add 1   client   portal  ", ProjectAddCommand.MESSAGE_DUPLICATE_CLIENT_PROJECT);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        assertEquals(model.getClientBook(), storage.readClientBook().orElseThrow());
    }

    @Test
    public void execute_projectAddMissingProject_throwsCommandException() {
        model.addPerson(AMY);
        assertCommandException("project add 1 Client Portal",
                String.format(ProjectAddCommand.MESSAGE_PROJECT_NOT_FOUND, "Client Portal"));
    }

    @Test
    public void execute_projectAddInvalidIndex_throwsCommandException() {
        model.addPerson(AMY);
        model.addProject(new Project("Client Portal"));
        assertCommandException("project add 2 Client Portal",
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, 1));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonClientBookStorage that throws the IOException e when saving
        JsonClientBookStorage clientBookStorage = new JsonClientBookStorage(prefPath) {
            @Override
            public void saveClientBook(ReadOnlyClientBook clientBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(clientBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveClientBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
