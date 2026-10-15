package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectDeleteCommandTest {

    private static final Project PROJECT = new Project("Client Portal");
    private static final Project OTHER_PROJECT = new Project("Website Redesign");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        model.addProject(PROJECT);
        model.addProject(OTHER_PROJECT);
        model.addClientProject(new ClientProject(ALICE, PROJECT));
        model.addClientProject(new ClientProject(BENSON, PROJECT));
        model.addClientProject(new ClientProject(ALICE, OTHER_PROJECT));
    }

    @Test
    public void constructor_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectDeleteCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectDeleteCommand(PROJECT).execute(null));
    }

    @Test
    public void execute_projectWithMembers_deletesProjectAndItsMembershipsOnly() {
        Model expectedModel = expectedModelWith(List.of(OTHER_PROJECT), new ClientProject(ALICE, OTHER_PROJECT));

        assertCommandSuccess(new ProjectDeleteCommand(PROJECT), model,
                String.format(ProjectDeleteCommand.MESSAGE_SUCCESS, PROJECT, 2, "clients"), expectedModel);
        assertFalse(model.hasProject(PROJECT));
        assertTrue(model.hasPerson(ALICE));
        assertTrue(model.hasPerson(BENSON));
    }

    @Test
    public void execute_projectWithOneMember_usesSingularInMessage() {
        Model expectedModel = expectedModelWith(List.of(PROJECT),
                new ClientProject(ALICE, PROJECT), new ClientProject(BENSON, PROJECT));

        assertCommandSuccess(new ProjectDeleteCommand(OTHER_PROJECT), model,
                String.format(ProjectDeleteCommand.MESSAGE_SUCCESS, OTHER_PROJECT, 1, "client"), expectedModel);
    }

    @Test
    public void execute_projectWithoutMembers_deletesProject() {
        Project emptyProject = new Project("Internal Tools");
        model.addProject(emptyProject);
        Model expectedModel = expectedModelWith(List.of(PROJECT, OTHER_PROJECT),
                new ClientProject(ALICE, PROJECT), new ClientProject(BENSON, PROJECT),
                new ClientProject(ALICE, OTHER_PROJECT));

        assertCommandSuccess(new ProjectDeleteCommand(emptyProject), model,
                String.format(ProjectDeleteCommand.MESSAGE_SUCCESS, emptyProject, 0, "clients"), expectedModel);
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_deletesExistingProject() {
        Model expectedModel = expectedModelWith(List.of(OTHER_PROJECT), new ClientProject(ALICE, OTHER_PROJECT));

        assertCommandSuccess(new ProjectDeleteCommand(new Project("  cLiEnT   pOrTaL  ")), model,
                String.format(ProjectDeleteCommand.MESSAGE_SUCCESS, PROJECT, 2, "clients"), expectedModel);
    }

    @Test
    public void execute_missingProject_throwsCommandException() {
        Project missingProject = new Project("Mobile App");
        assertCommandFailure(new ProjectDeleteCommand(missingProject), model,
                String.format(ProjectDeleteCommand.MESSAGE_PROJECT_NOT_FOUND, missingProject));
    }

    @Test
    public void execute_projectAlreadyDeleted_throwsCommandException() {
        ProjectDeleteCommand command = new ProjectDeleteCommand(PROJECT);
        assertCommandSuccess(command, model,
                String.format(ProjectDeleteCommand.MESSAGE_SUCCESS, PROJECT, 2, "clients"),
                expectedModelWith(List.of(OTHER_PROJECT), new ClientProject(ALICE, OTHER_PROJECT)));

        assertCommandFailure(command, model, String.format(ProjectDeleteCommand.MESSAGE_PROJECT_NOT_FOUND, PROJECT));
    }

    @Test
    public void equals() {
        ProjectDeleteCommand command = new ProjectDeleteCommand(PROJECT);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectDeleteCommand(new Project("  client   portal  "))));
        assertFalse(command.equals(new ProjectDeleteCommand(OTHER_PROJECT)));
        assertFalse(command.equals(new ProjectCreateCommand(PROJECT)));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        ProjectDeleteCommand command = new ProjectDeleteCommand(PROJECT);
        String expected = ProjectDeleteCommand.class.getCanonicalName() + "{project=" + PROJECT + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Copies the current clients, keeping only the given projects and memberships.
     */
    private Model expectedModelWith(List<Project> projects, ClientProject... memberships) {
        ClientBook expectedClientBook = new ClientBook(model.getClientBook());
        expectedClientBook.setClientProjects(List.of(memberships));
        expectedClientBook.setProjects(projects);
        return new ModelManager(expectedClientBook, new UserPrefs());
    }
}
