package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectAddCommandTest {

    private static final Project PROJECT = new Project("Client Portal");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(PROJECT);
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectAddCommand((Index) null, PROJECT));
        assertThrows(NullPointerException.class, () -> new ProjectAddCommand(INDEX_FIRST_PERSON, null));
        assertThrows(NullPointerException.class, () ->
                new ProjectAddCommand((List<Index>) null, PROJECT));
        assertThrows(NullPointerException.class, () ->
                new ProjectAddCommand(List.of((Index) null), PROJECT));
        assertThrows(NullPointerException.class, () ->
                new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT, (String) null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ProjectAddCommand command = new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_usesExistingProjectName() {
        Project inputProject = new Project("  cLiEnT   pOrTaL  ");
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(INDEX_FIRST_PERSON, inputProject), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT), expectedModel);
        assertSame(PROJECT, model.getClientBook().getClientProjectList().get(0).getProject());
        assertEquals("Client Portal", model.getClientBook().getProjectsForPerson(ALICE).get(0).getName());
    }

    @Test
    public void execute_validIndexFilteredList_attachesDisplayedClientAndPreservesFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, BENSON.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ProjectAddCommand(invalidIndex, PROJECT), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getClientBook().getPersonList().size());

        assertCommandFailure(new ProjectAddCommand(INDEX_SECOND_PERSON, PROJECT), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT), model,
                Messages.MESSAGE_NO_CONTACTS_DISPLAYED);
    }

    @Test
    public void execute_missingProject_throwsCommandException() {
        Project missingProject = new Project("Website Redesign");
        assertCommandFailure(new ProjectAddCommand(INDEX_FIRST_PERSON, missingProject), model,
                String.format(ProjectAddCommand.MESSAGE_PROJECT_NOT_FOUND, missingProject));
    }

    @Test
    public void execute_duplicateMembership_throwsCommandException() {
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        for (String name : List.of("Client Portal", "client portal", "  cLiEnT   pOrTaL  ")) {
            assertCommandFailure(new ProjectAddCommand(INDEX_FIRST_PERSON, new Project(name)), model,
                    ProjectAddCommand.MESSAGE_DUPLICATE_CLIENT_PROJECT);
        }
    }

    @Test
    public void execute_projectAlreadyHasAnotherClient_success() {
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(INDEX_SECOND_PERSON, PROJECT), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, BENSON.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_multipleClients_success() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT));
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                "Clients added to project Client Portal: Alice Pauline, Benson Meier", expectedModel);
    }

    @Test
    public void execute_multipleClients_skipsExistingMemberships() {
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                "Clients added to project Client Portal: Benson Meier\n"
                        + "Skipped clients already in project: Alice Pauline", expectedModel);
    }

    @Test
    public void execute_clientAlreadyHasAnotherProject_success() {
        Project otherProject = new Project("Website Redesign");
        model.addProject(otherProject);
        model.addClientProject(new ClientProject(ALICE, otherProject));
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT));

        assertCommandSuccess(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_role_storesRoleOnMembership() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT, "developer"));

        assertCommandSuccess(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT, "developer"), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT), expectedModel);
        assertEquals("developer", model.getClientBook().getClientProjectList().get(0).getRole());
    }

    @Test
    public void execute_multipleClients_addsAllWithRole() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(ALICE, PROJECT, "senior developer"));
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT, "senior developer"));

        assertCommandSuccess(new ProjectAddCommand(
                        List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT, " senior   developer "), model,
                String.format(ProjectAddCommand.MESSAGE_SUCCESS_MULTIPLE, PROJECT, "Alice Pauline, Benson Meier"),
                expectedModel);
    }

    @Test
    public void execute_multipleClients_skipsExistingAndAddsRemaining() {
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addClientProject(new ClientProject(BENSON, PROJECT, "developer"));

        String expectedMessage = String.format(ProjectAddCommand.MESSAGE_SUCCESS_MULTIPLE,
                PROJECT, "Benson Meier") + "\n"
                + String.format(ProjectAddCommand.MESSAGE_SKIPPED, "Alice Pauline");
        assertCommandSuccess(new ProjectAddCommand(
                        List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT, "developer"), model,
                expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleClients_allSkipped() {
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        String expectedMessage = String.format(ProjectAddCommand.MESSAGE_SKIPPED,
                "Alice Pauline, Benson Meier");

        assertCommandSuccess(new ProjectAddCommand(
                        List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                expectedMessage, new ModelManager(model.getClientBook(), new UserPrefs()));
    }

    @Test
    public void equals() {
        ProjectAddCommand command = new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT)));
        assertTrue(command.equals(new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("  client   portal  "))));
        assertFalse(command.equals(new ProjectAddCommand(INDEX_SECOND_PERSON, PROJECT)));
        assertFalse(command.equals(new ProjectAddCommand(INDEX_FIRST_PERSON, new Project("Website Redesign"))));
        assertFalse(command.equals(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT, "developer")));
        assertFalse(command.equals(new ProjectAddCommand(
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT)));
        assertFalse(command.equals(new ProjectCreateCommand(PROJECT)));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        ProjectAddCommand command = new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT);
        String expected = ProjectAddCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", project=" + PROJECT + "}";
        assertEquals(expected, command.toString());
        assertTrue(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT, "developer")
                .toString().contains("role=developer"));
        assertTrue(new ProjectAddCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT)
                .toString().contains("targetIndexes="));
    }
}
