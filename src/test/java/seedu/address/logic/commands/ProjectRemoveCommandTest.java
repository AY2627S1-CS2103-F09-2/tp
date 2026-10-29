package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectRemoveCommandTest {

    private static final Project PROJECT = new Project("Client Portal");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectRemoveCommand((Index) null, PROJECT));
        assertThrows(NullPointerException.class, () -> new ProjectRemoveCommand(INDEX_FIRST_PERSON, null));
        assertThrows(NullPointerException.class, () ->
                new ProjectRemoveCommand((List<Index>) null, PROJECT));
        assertThrows(NullPointerException.class, () ->
                new ProjectRemoveCommand(List.of((Index) null), PROJECT));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ProjectRemoveCommand command = new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_lastMember_detachesMembershipAndRetainsClientAndProject() {
        assertCommandSuccess(new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT),
                expectedModelWithMemberships());
        assertTrue(model.hasProject(PROJECT));
        assertTrue(model.hasPerson(ALICE));
        assertTrue(model.getClientBook().getClientProjectList().isEmpty());
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_usesExistingProjectName() {
        Project inputProject = new Project("  cLiEnT   pOrTaL  ");

        assertCommandSuccess(new ProjectRemoveCommand(INDEX_FIRST_PERSON, inputProject), model,
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT),
                expectedModelWithMemberships());
        assertEquals("Client Portal", model.getClientBook().getProjectList().get(0).getName());
    }

    @Test
    public void execute_validIndexFilteredList_detachesDisplayedClientAndPreservesFilter() {
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Model expectedModel = expectedModelWithMemberships(new ClientProject(ALICE, PROJECT));
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, BENSON.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ProjectRemoveCommand(invalidIndex, PROJECT), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getClientBook().getPersonList().size());

        assertCommandFailure(new ProjectRemoveCommand(INDEX_SECOND_PERSON, PROJECT), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT), model,
                Messages.MESSAGE_NO_CONTACTS_DISPLAYED);
    }

    @Test
    public void execute_missingProject_throwsCommandException() {
        Project missingProject = new Project("Website Redesign");
        assertCommandFailure(new ProjectRemoveCommand(INDEX_FIRST_PERSON, missingProject), model,
                String.format(ProjectRemoveCommand.MESSAGE_PROJECT_NOT_FOUND, missingProject));
    }

    @Test
    public void execute_clientNotInProject_throwsCommandException() {
        for (String name : List.of("Client Portal", "client portal", "  cLiEnT   pOrTaL  ")) {
            assertCommandFailure(new ProjectRemoveCommand(INDEX_SECOND_PERSON, new Project(name)), model,
                    ProjectRemoveCommand.MESSAGE_CLIENT_NOT_IN_PROJECT);
        }
    }

    @Test
    public void execute_membershipAlreadyRemoved_throwsCommandException() {
        ProjectRemoveCommand command = new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT);
        assertCommandSuccess(command, model,
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT),
                expectedModelWithMemberships());

        assertCommandFailure(command, model, ProjectRemoveCommand.MESSAGE_CLIENT_NOT_IN_PROJECT);
    }

    @Test
    public void execute_otherMembershipsExist_preservesOtherClientsAndProjects() {
        Project otherProject = new Project("Website Redesign");
        model.addProject(otherProject);
        ClientProject otherClientMembership = new ClientProject(BENSON, PROJECT);
        ClientProject otherProjectMembership = new ClientProject(ALICE, otherProject);
        model.addClientProject(otherClientMembership);
        model.addClientProject(otherProjectMembership);
        Model expectedModel = expectedModelWithMemberships(otherClientMembership, otherProjectMembership);

        assertCommandSuccess(new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT), model,
                String.format(ProjectRemoveCommand.MESSAGE_SUCCESS, ALICE.getName(), PROJECT), expectedModel);
    }

    @Test
    public void execute_multipleClients_removesAllExisting() {
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        String expectedMessage = String.format(ProjectRemoveCommand.MESSAGE_SUCCESS_MULTIPLE,
                PROJECT, "Alice Pauline, Benson Meier");

        assertCommandSuccess(new ProjectRemoveCommand(
                        List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                expectedMessage, expectedModelWithMemberships());
    }

    @Test
    public void execute_multipleClients_skipsMissingAndRemovesExisting() {
        String expectedMessage = String.format(ProjectRemoveCommand.MESSAGE_SUCCESS_MULTIPLE,
                PROJECT, "Alice Pauline") + "\n"
                + String.format(ProjectRemoveCommand.MESSAGE_SKIPPED, "Benson Meier");

        assertCommandSuccess(new ProjectRemoveCommand(
                        List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                expectedMessage, expectedModelWithMemberships());
    }

    @Test
    public void execute_multipleClients_allSkipped() {
        String expectedMessage = String.format(ProjectRemoveCommand.MESSAGE_SKIPPED,
                "Benson Meier, Carl Kurz");

        assertCommandSuccess(new ProjectRemoveCommand(
                        List.of(INDEX_SECOND_PERSON, Index.fromOneBased(3)), PROJECT), model,
                expectedMessage, expectedModelWithMemberships(new ClientProject(ALICE, PROJECT)));
    }

    @Test
    public void execute_multipleClients_success() {
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        Model expectedModel = expectedModelWithMemberships();

        assertCommandSuccess(new ProjectRemoveCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                "Clients removed from project Client Portal: Alice Pauline, Benson Meier", expectedModel);
    }

    @Test
    public void execute_multipleClients_skipsMissingMemberships() {
        Model expectedModel = expectedModelWithMemberships();

        assertCommandSuccess(new ProjectRemoveCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT), model,
                "Clients removed from project Client Portal: Alice Pauline\n"
                        + "Skipped clients not in project: Benson Meier", expectedModel);
    }

    @Test
    public void equals() {
        ProjectRemoveCommand command = new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT)));
        assertTrue(command.equals(new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("  client   portal  "))));
        assertFalse(command.equals(new ProjectRemoveCommand(INDEX_SECOND_PERSON, PROJECT)));
        assertFalse(command.equals(new ProjectRemoveCommand(INDEX_FIRST_PERSON, new Project("Website Redesign"))));
        assertFalse(command.equals(new ProjectRemoveCommand(
                List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT)));
        assertFalse(command.equals(new ProjectAddCommand(INDEX_FIRST_PERSON, PROJECT)));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        ProjectRemoveCommand command = new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT);
        String expected = ProjectRemoveCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", project=" + PROJECT + "}";
        assertEquals(expected, command.toString());
        assertTrue(new ProjectRemoveCommand(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), PROJECT)
                .toString().contains("targetIndexes="));
    }

    /**
     * Copies the current clients and projects with only the expected memberships.
     */
    private Model expectedModelWithMemberships(ClientProject... memberships) {
        ClientBook expectedClientBook = new ClientBook(model.getClientBook());
        expectedClientBook.setClientProjects(List.of(memberships));
        return new ModelManager(expectedClientBook, new UserPrefs());
    }
}
