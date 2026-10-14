package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ProjectCreateCommandTest {

    private final Model model = new ModelManager();

    @Test
    public void constructor_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ProjectCreateCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ProjectCreateCommand command = new ProjectCreateCommand(new Project("Client Portal"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_newProject_success() {
        Project project = new Project("Client Portal");
        Model expectedModel = new ModelManager();
        expectedModel.addProject(project);

        assertCommandSuccess(new ProjectCreateCommand(project), model,
                String.format(ProjectCreateCommand.MESSAGE_SUCCESS, project), expectedModel);
    }

    @Test
    public void execute_nameWithExtraSpaces_successWithNormalizedName() {
        Project project = new Project("  Client   Portal  ");
        Model expectedModel = new ModelManager();
        expectedModel.addProject(new Project("Client Portal"));

        assertCommandSuccess(new ProjectCreateCommand(project), model,
                String.format(ProjectCreateCommand.MESSAGE_SUCCESS, "Client Portal"), expectedModel);
        assertEquals("Client Portal", model.getClientBook().getProjectList().get(0).getName());
    }

    @Test
    public void execute_existingClientsProjectsAndMemberships_preservesDataAndFilter() {
        Project existingProject = new Project("Website Redesign");
        ClientBook clientBook = new ClientBook();
        clientBook.addPerson(ALICE);
        clientBook.addProject(existingProject);
        clientBook.addClientProject(new ClientProject(ALICE, existingProject));
        model.setClientBook(clientBook);
        model.updateFilteredPersonList(person -> false);

        Project newProject = new Project("Client Portal");
        Model expectedModel = new ModelManager(clientBook, new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> false);
        expectedModel.addProject(newProject);

        assertCommandSuccess(new ProjectCreateCommand(newProject), model,
                String.format(ProjectCreateCommand.MESSAGE_SUCCESS, newProject), expectedModel);
    }

    @Test
    public void execute_duplicateProject_throwsCommandException() {
        model.addProject(new Project("Client Portal"));
        assertCommandFailure(new ProjectCreateCommand(new Project("Client Portal")), model,
                ProjectCreateCommand.MESSAGE_DUPLICATE_PROJECT);
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_throwsCommandException() {
        model.addProject(new Project("Client Portal"));
        for (String name : List.of("client portal", "Client   Portal", "  cLiEnT   pOrTaL  ")) {
            assertCommandFailure(new ProjectCreateCommand(new Project(name)), model,
                    ProjectCreateCommand.MESSAGE_DUPLICATE_PROJECT);
        }
    }

    @Test
    public void equals() {
        ProjectCreateCommand command = new ProjectCreateCommand(new Project("Client Portal"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ProjectCreateCommand(new Project("Client Portal"))));
        assertTrue(command.equals(new ProjectCreateCommand(new Project("  client   portal  "))));
        assertFalse(command.equals(new ProjectCreateCommand(new Project("Website Redesign"))));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
    }

    @Test
    public void toStringMethod() {
        Project project = new Project("Client Portal");
        ProjectCreateCommand command = new ProjectCreateCommand(project);
        String expected = ProjectCreateCommand.class.getCanonicalName() + "{toCreate=" + project + "}";
        assertEquals(expected, command.toString());
    }
}
