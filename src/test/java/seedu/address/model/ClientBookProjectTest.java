package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.model.project.exceptions.DuplicateClientProjectException;
import seedu.address.model.project.exceptions.DuplicateProjectException;
import seedu.address.model.project.exceptions.ProjectNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class ClientBookProjectTest {

    @Test
    public void projectLifecycle() {
        ClientBook clientBook = new ClientBook();
        Project hackathon = new Project("Hackathon");
        clientBook.addPerson(ALICE);
        clientBook.addProject(hackathon);

        ClientProject membership = new ClientProject(ALICE, hackathon);
        clientBook.addClientProject(membership);

        assertTrue(clientBook.hasProject(hackathon));
        assertTrue(clientBook.hasClientProject(membership));
        assertEquals(List.of(hackathon), clientBook.getProjectsForPerson(ALICE));
    }

    @Test
    public void duplicateProjectOrMembership_throws() {
        ClientBook clientBook = new ClientBook();
        Project hackathon = new Project("Hackathon");
        clientBook.addPerson(ALICE);
        clientBook.addProject(hackathon);
        clientBook.addClientProject(new ClientProject(ALICE, hackathon));

        assertThrows(DuplicateProjectException.class, () -> clientBook.addProject(new Project("hackathon")));
        ClientProject duplicateMembership = new ClientProject(ALICE, new Project("HACKATHON"));
        assertThrows(DuplicateClientProjectException.class, () -> clientBook.addClientProject(duplicateMembership));
    }

    @Test
    public void removePerson_removesMemberships() {
        ClientBook clientBook = new ClientBook();
        Project hackathon = new Project("Hackathon");
        clientBook.addPerson(ALICE);
        clientBook.addProject(hackathon);
        clientBook.addClientProject(new ClientProject(ALICE, hackathon));

        clientBook.removePerson(ALICE);

        assertTrue(clientBook.getClientProjectList().isEmpty());
        assertTrue(clientBook.getProjectList().contains(hackathon));
    }

    @Test
    public void setPerson_updatesMembershipClient() {
        ClientBook clientBook = new ClientBook();
        Project hackathon = new Project("Hackathon");
        Person editedAlice = new PersonBuilder(ALICE).withPhone("91234567").build();
        clientBook.addPerson(ALICE);
        clientBook.addProject(hackathon);
        clientBook.addClientProject(new ClientProject(ALICE, hackathon));

        clientBook.setPerson(ALICE, editedAlice);

        assertEquals(List.of(hackathon), clientBook.getProjectsForPerson(editedAlice));
        assertEquals(editedAlice, clientBook.getClientProjectList().get(0).getClient());
    }

    @Test
    public void removeProject_removesMemberships() {
        ClientBook clientBook = new ClientBook();
        Project hackathon = new Project("Hackathon");
        clientBook.addPerson(ALICE);
        clientBook.addProject(hackathon);
        clientBook.addClientProject(new ClientProject(ALICE, hackathon));

        clientBook.removeProject(hackathon);

        assertFalse(clientBook.hasProject(hackathon));
        assertTrue(clientBook.getClientProjectList().isEmpty());
    }

    @Test
    public void addClientProject_missingProject_throws() {
        ClientBook clientBook = new ClientBook();
        clientBook.addPerson(ALICE);
        ClientProject membership = new ClientProject(ALICE, new Project("Hackathon"));

        assertThrows(ProjectNotFoundException.class, () -> clientBook.addClientProject(membership));
    }
}
