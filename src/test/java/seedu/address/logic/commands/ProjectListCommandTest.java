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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectListCommandTest {

    private static final Project PROJECT = new Project("Client Portal");
    private static final Project OTHER_PROJECT = new Project("Website Redesign");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(OTHER_PROJECT);
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectListCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ProjectListCommand command = new ProjectListCommand(INDEX_FIRST_PERSON);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_singleProject_listsOnlyAssignedProject() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess(new ProjectListCommand(INDEX_FIRST_PERSON), model,
                "Projects for client Alice Pauline:\nClient Portal", expectedModel);
    }

    @Test
    public void execute_multipleProjects_listsProjectsInMembershipOrder() {
        model.addClientProject(new ClientProject(ALICE, OTHER_PROJECT));
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());

        assertCommandSuccess(new ProjectListCommand(INDEX_FIRST_PERSON), model,
                "Projects for client Alice Pauline:\nClient Portal\nWebsite Redesign", expectedModel);
    }

    @Test
    public void execute_clientWithNoProjects_showsEmptyMessage() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        assertCommandSuccess(new ProjectListCommand(INDEX_SECOND_PERSON), model,
                "Client Benson Meier has no projects.", expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_listsDisplayedClientProjectsAndPreservesFilter() {
        model.addClientProject(new ClientProject(BENSON, OTHER_PROJECT));
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(new ProjectListCommand(INDEX_FIRST_PERSON), model,
                "Projects for client Benson Meier:\nWebsite Redesign", expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ProjectListCommand(invalidIndex), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getClientBook().getPersonList().size());

        assertCommandFailure(new ProjectListCommand(INDEX_SECOND_PERSON), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new ProjectListCommand(INDEX_FIRST_PERSON), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, 0));
    }

    @Test
    public void equals() {
        ProjectListCommand command = new ProjectListCommand(INDEX_FIRST_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectListCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(new ProjectListCommand(INDEX_SECOND_PERSON)));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        ProjectListCommand command = new ProjectListCommand(INDEX_FIRST_PERSON);
        String expected = ProjectListCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, command.toString());
    }
}
