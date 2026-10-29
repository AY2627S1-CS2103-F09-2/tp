package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectRoleCommandTest {

    private static final Project PROJECT = new Project("Client Portal");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT, "developer"));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> new ProjectRoleCommand(null, PROJECT, "developer"));
        assertThrows(NullPointerException.class, ()
                -> new ProjectRoleCommand(INDEX_FIRST_PERSON, null, "developer"));
        assertThrows(NullPointerException.class, ()
                -> new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, null));
    }

    @Test
    public void execute_validRole_updatesMembershipInPlace() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.setClientProjectRole(new ClientProject(ALICE, PROJECT, "developer"), "UI");

        assertCommandSuccess(new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, "UI"), model,
                "Role for client Alice Pauline on project Client Portal changed to UI", expectedModel);
        assertEquals("UI", model.getClientBook().getClientProjectList().get(0).getRole());
    }

    @Test
    public void execute_emptyRole_removesRole() {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.setClientProjectRole(new ClientProject(ALICE, PROJECT, "developer"),
                ClientProject.DEFAULT_ROLE);

        assertCommandSuccess(new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, ClientProject.DEFAULT_ROLE), model,
                "Role for client Alice Pauline on project Client Portal removed", expectedModel);
        assertEquals("", model.getClientBook().getClientProjectList().get(0).getRole());
    }

    @Test
    public void execute_missingMembership_throwsCommandException() {
        Model emptyModel = new ModelManager(getTypicalClientBook(), new UserPrefs());
        emptyModel.addProject(PROJECT);

        assertCommandFailure(new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, "UI"), emptyModel,
                ProjectRoleCommand.MESSAGE_CLIENT_NOT_IN_PROJECT);
    }

    @Test
    public void execute_missingProject_throwsCommandException() {
        Project missingProject = new Project("Website Redesign");

        assertCommandFailure(new ProjectRoleCommand(INDEX_FIRST_PERSON, missingProject, "UI"), model,
                String.format(ProjectRoleCommand.MESSAGE_PROJECT_NOT_FOUND, missingProject));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        assertCommandFailure(new ProjectRoleCommand(
                        seedu.address.commons.core.index.Index.fromOneBased(100), PROJECT, "UI"), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                        model.getFilteredPersonList().size()));
    }

    @Test
    public void equalsAndToString_includeRole() {
        ProjectRoleCommand command = new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, "developer");

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, "developer")));
        assertFalse(command.equals(new ProjectRoleCommand(INDEX_FIRST_PERSON, PROJECT, "UI")));
        assertFalse(command.equals(null));
        assertTrue(command.toString().contains("role=developer"));
    }
}
