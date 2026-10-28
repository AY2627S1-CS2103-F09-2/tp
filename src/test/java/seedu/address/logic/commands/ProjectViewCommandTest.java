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
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.ContactView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.testutil.PersonBuilder;

public class ProjectViewCommandTest {

    private static final Project PROJECT = new Project("Client Portal");
    private static final Project OTHER_PROJECT = new Project("Website Redesign");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(PROJECT);
        model.addProject(OTHER_PROJECT);
        // Membership order differs from contact order.
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.addClientProject(new ClientProject(BENSON, OTHER_PROJECT));
        model.addClientProject(new ClientProject(CARL, OTHER_PROJECT));
    }

    @Test
    public void constructor_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectViewCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectViewCommand(PROJECT).execute(null));
    }

    @Test
    public void execute_projectWithClients_filtersInContactOrderAndPreservesData() {
        assertViewSuccess(new ProjectViewCommand(PROJECT), "Listed 2 clients in project: Client Portal",
                List.of(ALICE, BENSON));
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_viewsExistingProject() {
        assertViewSuccess(new ProjectViewCommand(new Project("  cLiEnT   pOrTaL  ")),
                "Listed 2 clients in project: Client Portal", List.of(ALICE, BENSON));
    }

    @Test
    public void execute_projectWithOneClient_usesSingularInMessage() {
        model.removeClientProject(new ClientProject(BENSON, PROJECT));
        assertViewSuccess(new ProjectViewCommand(PROJECT), "Listed 1 client in project: Client Portal",
                List.of(ALICE));
    }

    @Test
    public void execute_projectWithoutClients_clearsDisplayedListAndShowsEmptyMessage() {
        Project emptyProject = new Project("Internal Tools");
        model.addProject(emptyProject);
        assertViewSuccess(new ProjectViewCommand(emptyProject), "Project Internal Tools has no clients.", List.of());
    }

    @Test
    public void execute_projectInEmptyClientBook_showsEmptyMessage() {
        model = new ModelManager();
        model.addProject(PROJECT);
        assertViewSuccess(new ProjectViewCommand(PROJECT), "Project Client Portal has no clients.", List.of());
    }

    @Test
    public void execute_filteredList_replacesPreviousFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertViewSuccess(new ProjectViewCommand(PROJECT), "Listed 2 clients in project: Client Portal",
                List.of(ALICE, BENSON));
    }

    @Test
    public void execute_emptyDisplayedList_findsClientsOutsidePreviousFilter() {
        model.updateFilteredPersonList(person -> false);
        assertViewSuccess(new ProjectViewCommand(PROJECT), "Listed 2 clients in project: Client Portal",
                List.of(ALICE, BENSON));
    }

    @Test
    public void execute_switchProjects_replacesProjectFilter() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        assertViewSuccess(new ProjectViewCommand(OTHER_PROJECT), "Listed 2 clients in project: Website Redesign",
                List.of(BENSON, CARL));
    }

    @Test
    public void execute_missingProject_throwsCommandExceptionAndPreservesFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertCommandFailure(new ProjectViewCommand(new Project("Missing Project")), model,
                "Project does not exist: Missing Project");
    }

    @Test
    public void execute_partialProjectName_throwsCommandException() {
        assertCommandFailure(new ProjectViewCommand(new Project("Client")), model,
                "Project does not exist: Client");
    }

    @Test
    public void execute_listAfterView_restoresAllContacts() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(ContactView.allContacts(), model.getContactView());
    }

    @Test
    public void execute_membershipAddedAfterView_includesNewMember() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        model.addClientProject(new ClientProject(CARL, PROJECT));
        assertEquals(List.of(ALICE, BENSON, CARL), model.getFilteredPersonList());
        assertEquals(ContactView.project(PROJECT), model.getContactView());
    }

    @Test
    public void execute_memberRemovedAfterView_excludesDetachedClient() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        new ProjectRemoveCommand(INDEX_FIRST_PERSON, PROJECT).execute(model);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertTrue(model.hasPerson(ALICE));
        assertTrue(model.hasClientProject(new ClientProject(BENSON, OTHER_PROJECT)));
    }

    @Test
    public void execute_projectDeletedAfterView_showsAllContactsAndKeepsClients() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        new ProjectDeleteCommand(PROJECT).execute(model);
        assertEquals(model.getClientBook().getPersonList(), model.getFilteredPersonList());
        assertEquals(ContactView.allContacts(), model.getContactView());
        assertTrue(model.hasPerson(ALICE));
        assertTrue(model.hasPerson(BENSON));
    }

    @Test
    public void execute_clientRenamedAfterView_keepsUpdatedClientVisible() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        Person renamedAlice = new PersonBuilder(ALICE).withName("Alice Renamed").build();
        model.setPerson(ALICE, renamedAlice);
        assertEquals(List.of(renamedAlice, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_filterChangedAfterView_membershipChangesPreserveNewFilter() throws Exception {
        new ProjectViewCommand(PROJECT).execute(model);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        model.removeClientProject(new ClientProject(BENSON, PROJECT));
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        assertEquals(ContactView.filtered(), model.getContactView());
    }

    @Test
    public void equals() {
        ProjectViewCommand command = new ProjectViewCommand(PROJECT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectViewCommand(new Project("  client   portal  "))));
        assertFalse(command.equals(new ProjectViewCommand(OTHER_PROJECT)));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        String expected = ProjectViewCommand.class.getCanonicalName() + "{project=" + PROJECT + "}";
        assertEquals(expected, new ProjectViewCommand(PROJECT).toString());
    }

    /**
     * Verifies the result and displayed clients while ensuring the client book remains unchanged.
     */
    private void assertViewSuccess(ProjectViewCommand command, String expectedMessage, List<Person> expectedClients) {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(expectedClients::contains);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedClients, model.getFilteredPersonList());
        assertEquals(ContactView.Type.PROJECT, model.getContactView().getType());
    }
}
