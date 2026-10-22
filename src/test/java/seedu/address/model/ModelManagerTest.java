package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameOrCompanyContainsKeywordsPredicate;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.model.project.exceptions.ClientProjectNotFoundException;
import seedu.address.model.project.exceptions.DuplicateClientProjectException;
import seedu.address.model.project.exceptions.DuplicateProjectException;
import seedu.address.model.project.exceptions.ProjectNotFoundException;
import seedu.address.testutil.ClientBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new ClientBook(), new ClientBook(modelManager.getClientBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new ClientBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInClientBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInClientBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasProject_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasProject(null));
    }

    @Test
    public void hasProject_projectNotInClientBook_returnsFalse() {
        assertFalse(modelManager.hasProject(new Project("Client Portal")));
    }

    @Test
    public void hasProject_projectInClientBook_returnsTrue() {
        modelManager.addProject(new Project("Client Portal"));
        assertTrue(modelManager.hasProject(new Project("Client Portal")));
        assertTrue(modelManager.hasProject(new Project("  client   portal  ")));
    }

    @Test
    public void addProject_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.addProject(null));
    }

    @Test
    public void addProject_validProject_addsProject() {
        Project project = new Project("Client Portal");
        modelManager.addProject(project);
        assertEquals(List.of(project), modelManager.getClientBook().getProjectList());
    }

    @Test
    public void addProject_duplicateProject_throwsDuplicateProjectException() {
        modelManager.addProject(new Project("Client Portal"));
        Project duplicate = new Project("  client   portal  ");
        assertThrows(DuplicateProjectException.class, () -> modelManager.addProject(duplicate));
        assertEquals(List.of(new Project("Client Portal")), modelManager.getClientBook().getProjectList());
    }

    @Test
    public void hasClientProject_nullMembership_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasClientProject(null));
    }

    @Test
    public void hasClientProject_membershipNotInClientBook_returnsFalse() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addProject(project);
        assertFalse(modelManager.hasClientProject(new ClientProject(ALICE, project)));
    }

    @Test
    public void hasClientProject_membershipInClientBook_returnsTrue() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addProject(project);
        modelManager.addClientProject(new ClientProject(ALICE, project));
        assertTrue(modelManager.hasClientProject(new ClientProject(ALICE, new Project("  client   portal  "))));
        assertFalse(modelManager.hasClientProject(new ClientProject(BENSON, project)));
    }

    @Test
    public void addClientProject_nullMembership_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.addClientProject(null));
    }

    @Test
    public void addClientProject_validMembership_addsMembership() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addProject(project);
        ClientProject membership = new ClientProject(ALICE, project);
        modelManager.addClientProject(membership);
        assertEquals(List.of(membership), modelManager.getClientBook().getClientProjectList());
    }

    @Test
    public void addClientProject_duplicateMembership_throwsDuplicateClientProjectException() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addProject(project);
        ClientProject membership = new ClientProject(ALICE, project);
        modelManager.addClientProject(membership);
        ClientProject duplicate = new ClientProject(ALICE, new Project("  client   portal  "));

        assertThrows(DuplicateClientProjectException.class, () -> modelManager.addClientProject(duplicate));
        assertEquals(List.of(membership), modelManager.getClientBook().getClientProjectList());
    }

    @Test
    public void addClientProject_missingClient_throwsPersonNotFoundException() {
        Project project = new Project("Client Portal");
        modelManager.addProject(project);
        ClientProject membership = new ClientProject(ALICE, project);

        assertThrows(PersonNotFoundException.class, () -> modelManager.addClientProject(membership));
        assertTrue(modelManager.getClientBook().getClientProjectList().isEmpty());
    }

    @Test
    public void addClientProject_missingProject_throwsProjectNotFoundException() {
        modelManager.addPerson(ALICE);
        ClientProject membership = new ClientProject(ALICE, new Project("Client Portal"));

        assertThrows(ProjectNotFoundException.class, () -> modelManager.addClientProject(membership));
        assertTrue(modelManager.getClientBook().getClientProjectList().isEmpty());
    }

    @Test
    public void removeClientProject_nullMembership_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.removeClientProject(null));
    }

    @Test
    public void removeClientProject_missingMembership_throwsClientProjectNotFoundException() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addProject(project);
        ClientProject existingMembership = new ClientProject(ALICE, project);
        modelManager.addClientProject(existingMembership);
        ClientProject missingMembership = new ClientProject(BENSON, project);

        assertThrows(ClientProjectNotFoundException.class, () -> modelManager.removeClientProject(missingMembership));
        assertEquals(List.of(existingMembership), modelManager.getClientBook().getClientProjectList());
        assertEquals(List.of(project), modelManager.getClientBook().getProjectList());
        assertEquals(List.of(ALICE, BENSON), modelManager.getClientBook().getPersonList());
    }

    @Test
    public void removeClientProject_lastMembership_retainsClientAndProject() {
        Project project = new Project("Client Portal");
        modelManager.addPerson(ALICE);
        modelManager.addProject(project);
        modelManager.addClientProject(new ClientProject(ALICE, project));

        modelManager.removeClientProject(new ClientProject(ALICE, new Project("  client   portal  ")));

        assertTrue(modelManager.getClientBook().getClientProjectList().isEmpty());
        assertEquals(List.of(project), modelManager.getClientBook().getProjectList());
        assertEquals(List.of(ALICE), modelManager.getClientBook().getPersonList());
    }

    @Test
    public void removeClientProject_existingMembership_retainsOtherMemberships() {
        Project project = new Project("Client Portal");
        Project otherProject = new Project("Website Redesign");
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addProject(project);
        modelManager.addProject(otherProject);
        ClientProject targetMembership = new ClientProject(ALICE, project);
        ClientProject otherClientMembership = new ClientProject(BENSON, project);
        ClientProject otherProjectMembership = new ClientProject(ALICE, otherProject);
        modelManager.addClientProject(targetMembership);
        modelManager.addClientProject(otherClientMembership);
        modelManager.addClientProject(otherProjectMembership);

        modelManager.removeClientProject(targetMembership);

        assertEquals(List.of(otherClientMembership, otherProjectMembership),
                modelManager.getClientBook().getClientProjectList());
        assertEquals(List.of(project, otherProject), modelManager.getClientBook().getProjectList());
        assertEquals(List.of(ALICE, BENSON), modelManager.getClientBook().getPersonList());
    }

    @Test
    public void deleteProject_nullProject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.deleteProject(null));
    }

    @Test
    public void deleteProject_missingProject_throwsProjectNotFoundException() {
        assertThrows(ProjectNotFoundException.class, () -> modelManager.deleteProject(new Project("Client Portal")));
    }

    @Test
    public void deleteProject_existingProject_removesItsMembershipsAndKeepsClients() {
        Project project = new Project("Client Portal");
        Project otherProject = new Project("Website Redesign");
        modelManager.addPerson(ALICE);
        modelManager.addPerson(BENSON);
        modelManager.addProject(project);
        modelManager.addProject(otherProject);
        modelManager.addClientProject(new ClientProject(ALICE, project));
        modelManager.addClientProject(new ClientProject(BENSON, project));
        ClientProject otherMembership = new ClientProject(ALICE, otherProject);
        modelManager.addClientProject(otherMembership);

        modelManager.deleteProject(new Project("  client   portal  "));

        assertEquals(List.of(otherProject), modelManager.getClientBook().getProjectList());
        assertEquals(List.of(otherMembership), modelManager.getClientBook().getClientProjectList());
        assertEquals(List.of(ALICE, BENSON), modelManager.getClientBook().getPersonList());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        ClientBook clientBook = new ClientBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        ClientBook differentClientBook = new ClientBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(clientBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(clientBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different clientBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentClientBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameOrCompanyContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(clientBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(clientBook, differentUserPrefs)));
    }
}
