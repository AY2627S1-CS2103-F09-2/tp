package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.commands.ProjectListCommand;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
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
    public void execute_editRemoveAddress_savesContactWithoutChangingOtherFields() throws Exception {
        Person person = new PersonBuilder(AMY).withCompany("Acme Pte Ltd").withStatus(Status.ACTIVE)
                .withNote("Prefers email").build();
        model.addPerson(person);
        model.addPerson(ALICE);
        Person editedPerson = new PersonBuilder(person).withoutAddress().build();
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(person, editedPerson);

        assertCommandSuccess("edit 1 a/",
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        assertEquals(expectedModel.getClientBook(), storage.readClientBook().orElseThrow());
    }

    @Test
    public void execute_editRemoveMissingAddress_success() throws Exception {
        Person person = new PersonBuilder(AMY).withoutAddress().build();
        model.addPerson(person);
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());

        assertCommandSuccess("edit 1 a/",
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(person)), expectedModel);
    }

    @Test
    public void execute_editRemoveAddressAfterFind_usesDisplayedIndex() throws Exception {
        model.addPerson(AMY);
        model.addPerson(ALICE);
        logic.execute("find Alice");
        Person editedPerson = new PersonBuilder(ALICE).withoutAddress().build();
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(ALICE, editedPerson);

        assertCommandSuccess("edit 1 a/",
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
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
    public void execute_projectRemove_savesDetachmentAndRetainsClientAndProject() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        logic.execute("project add 1 Client Portal");
        ClientBook expectedClientBook = new ClientBook(model.getClientBook());
        expectedClientBook.setClientProjects(List.of());
        Model expectedModel = new ModelManager(expectedClientBook, new UserPrefs());

        assertCommandSuccess("project remove 1   client   portal  ",
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, AMY.getName(), "Client Portal"), expectedModel);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        ReadOnlyClientBook savedClientBook = storage.readClientBook().orElseThrow();
        assertEquals(expectedClientBook, savedClientBook);
        assertEquals(List.of(AMY), savedClientBook.getPersonList());
        assertEquals("Client Portal", savedClientBook.getProjectList().get(0).getName());
        assertEquals(List.of(), savedClientBook.getClientProjectList());
    }

    @Test
    public void execute_projectRemoveNonMember_throwsCommandException() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        assertCommandException("project remove 1 Client Portal", ProjectRemoveCommand.MESSAGE_CLIENT_NOT_IN_PROJECT);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        assertEquals(model.getClientBook(), storage.readClientBook().orElseThrow());
    }

    @Test
    public void execute_projectRemoveRepeated_throwsCommandException() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        logic.execute("project add 1 Client Portal");
        logic.execute("project remove 1 Client Portal");
        assertCommandException("project remove 1 client portal", ProjectRemoveCommand.MESSAGE_CLIENT_NOT_IN_PROJECT);

        JsonClientBookStorage storage = new JsonClientBookStorage(temporaryFolder.resolve("clientBook.json"));
        assertEquals(model.getClientBook(), storage.readClientBook().orElseThrow());
    }

    @Test
    public void execute_projectRemoveMissingProject_throwsCommandException() {
        model.addPerson(AMY);
        assertCommandException("project remove 1 Client Portal",
                String.format(ProjectRemoveCommand.MESSAGE_PROJECT_NOT_FOUND, "Client Portal"));
    }

    @Test
    public void execute_projectRemoveInvalidIndex_throwsCommandException() {
        model.addPerson(AMY);
        Project project = new Project("Client Portal");
        model.addProject(project);
        model.addClientProject(new ClientProject(AMY, project));
        assertCommandException("project remove 2 Client Portal",
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, 1));
    }

    @Test
    public void execute_projectRemoveMissingName_throwsParseException() {
        assertParseException("project remove 1   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_projectListAfterMembershipChanges_showsCurrentProjects() throws Exception {
        model.addPerson(AMY);
        logic.execute("project create Client Portal");
        logic.execute("project create Website Redesign");
        logic.execute("project add 1 Website Redesign");
        logic.execute("project add 1 client portal");
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());

        assertCommandSuccess("project list 1",
                String.format(ProjectListCommand.MESSAGE_SUCCESS, AMY.getName(), "Website Redesign\nClient Portal"),
                expectedModel);

        logic.execute("project remove 1 Website Redesign");
        expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess("project list 1",
                String.format(ProjectListCommand.MESSAGE_SUCCESS, AMY.getName(), "Client Portal"), expectedModel);

        logic.execute("project remove 1 Client Portal");
        expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess("project list 1",
                String.format(ProjectListCommand.MESSAGE_EMPTY, AMY.getName()), expectedModel);
    }

    @Test
    public void execute_projectListNoProjects_showsEmptyMessage() throws Exception {
        model.addPerson(AMY);
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess("project list 1",
                String.format(ProjectListCommand.MESSAGE_EMPTY, AMY.getName()), expectedModel);
    }

    @Test
    public void execute_projectListAfterFind_usesDisplayedIndex() throws Exception {
        model.addPerson(AMY);
        model.addPerson(ALICE);
        Project project = new Project("Client Portal");
        model.addProject(project);
        model.addClientProject(new ClientProject(ALICE, project));
        logic.execute("find Alice");
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.isSamePerson(ALICE));

        assertCommandSuccess("project list 1",
                String.format(ProjectListCommand.MESSAGE_SUCCESS, ALICE.getName(), "Client Portal"), expectedModel);
    }

    @Test
    public void execute_projectListInvalidIndex_throwsCommandException() {
        model.addPerson(AMY);
        assertCommandException("project list 2", String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, 1));
    }

    @Test
    public void execute_projectListExtraArguments_throwsParseException() {
        assertParseException("project list 1 Client Portal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE));
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
