package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectAllCommandTest {

    private static final Project PROJECT = new Project("Client Portal");
    private static final Project OTHER_PROJECT = new Project("Website Redesign");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectAllCommand().execute(null));
    }

    @Test
    public void execute_noProjects_showsEmptyMessage() {
        assertSummary("No projects to list.");
    }

    @Test
    public void execute_emptyClientBook_showsEmptyMessage() {
        model = new ModelManager();
        assertSummary("No projects to list.");
    }

    @Test
    public void execute_projectWithoutClients_listsZeroClients() {
        model = new ModelManager();
        model.addProject(PROJECT);
        assertSummary("Projects:\nClient Portal: 0 clients");
    }

    @Test
    public void execute_multipleProjects_listsCountsInProjectCreationOrder() {
        // Creation order differs from alphabetical order and membership order.
        model.addProject(OTHER_PROJECT);
        model.addProject(PROJECT);
        model.addProject(new Project("Internal Tools"));
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        model.addClientProject(new ClientProject(ALICE, OTHER_PROJECT));

        assertSummary("Projects:\nWebsite Redesign: 1 client\nClient Portal: 2 clients\nInternal Tools: 0 clients");
    }

    @Test
    public void execute_filteredContacts_countsAllClientsAndPreservesFilter() {
        model.addProject(PROJECT);
        model.addProject(OTHER_PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        model.addClientProject(new ClientProject(BENSON, OTHER_PROJECT));
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertSummary("Projects:\nClient Portal: 2 clients\nWebsite Redesign: 1 client");
    }

    @Test
    public void execute_emptyDisplayedList_countsClientsOutsideFilter() {
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.updateFilteredPersonList(person -> false);

        assertSummary("Projects:\nClient Portal: 1 client");
    }

    @Test
    public void execute_membershipWithNormalizedProjectName_countsUnderStoredName() {
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, new Project("  cLiEnT   pOrTaL  ")));

        assertSummary("Projects:\nClient Portal: 1 client");
    }

    @Test
    public void execute_membershipWithCaseEquivalentUnicodeName_countsUnderStoredName() {
        model.addProject(new Project("Client \u0130"));
        model.addClientProject(new ClientProject(ALICE, new Project("Client i")));

        assertSummary("Projects:\nClient \u0130: 1 client");
    }

    @Test
    public void execute_membershipsChanged_reportsCurrentCounts() {
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        assertSummary("Projects:\nClient Portal: 1 client");

        model.addClientProject(new ClientProject(BENSON, PROJECT));
        assertSummary("Projects:\nClient Portal: 2 clients");

        model.removeClientProject(new ClientProject(ALICE, PROJECT));
        assertSummary("Projects:\nClient Portal: 1 client");

        model.deletePerson(BENSON);
        assertSummary("Projects:\nClient Portal: 0 clients");
    }

    @Test
    public void execute_projectDeleted_omitsDeletedProject() {
        model.addProject(PROJECT);
        model.addProject(OTHER_PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.deleteProject(PROJECT);

        assertSummary("Projects:\nWebsite Redesign: 0 clients");
    }

    @Test
    public void execute_projectNamedAll_listsItAndAllowsViewingItsClients() throws Exception {
        Project allProject = new Project("all");
        model.addProject(allProject);
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, allProject));
        model.addClientProject(new ClientProject(BENSON, PROJECT));

        new ProjectViewCommand(allProject).execute(model);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
        assertSummary("Projects:\nall: 1 client\nClient Portal: 1 client");
    }

    @Test
    public void equals() {
        ProjectAllCommand command = new ProjectAllCommand();
        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectAllCommand()));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
    }

    /**
     * Verifies the summary while ensuring the client book and displayed contacts remain unchanged.
     */
    private void assertSummary(String expectedMessage) {
        List<Person> displayedClients = List.copyOf(model.getFilteredPersonList());
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(displayedClients::contains);
        assertCommandSuccess(new ProjectAllCommand(), model, expectedMessage, expectedModel);
    }
}
