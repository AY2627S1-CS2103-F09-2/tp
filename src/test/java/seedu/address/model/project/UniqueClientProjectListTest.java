package seedu.address.model.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.project.exceptions.ClientProjectNotFoundException;
import seedu.address.model.project.exceptions.DuplicateClientProjectException;
import seedu.address.testutil.PersonBuilder;

public class UniqueClientProjectListTest {

    @Test
    public void membershipListOperations_success() {
        Project hackathon = new Project("Hackathon");
        ClientProject membership = new ClientProject(ALICE, hackathon);
        UniqueClientProjectList memberships = new UniqueClientProjectList();
        memberships.add(membership);

        assertTrue(memberships.contains(new ClientProject(ALICE, new Project("hackathon"))));
        assertEquals(List.of(membership), memberships.asUnmodifiableObservableList());
        assertEquals(List.of(membership), List.copyOf(memberships.asUnmodifiableObservableList()));
        assertEquals(memberships, memberships);
        assertFalse(memberships.equals(null));
        assertFalse(memberships.equals("membership"));
        assertEquals(memberships.hashCode(), memberships.hashCode());
        assertEquals("[" + membership + "]", memberships.toString());
    }

    @Test
    public void remove_missingMembership_throws() {
        UniqueClientProjectList memberships = new UniqueClientProjectList();

        assertThrows(ClientProjectNotFoundException.class, () ->
                memberships.remove(new ClientProject(ALICE, new Project("Hackathon"))));
    }

    @Test
    public void removeForClientAndProject_removesMatchingMemberships() {
        Project hackathon = new Project("Hackathon");
        Project portal = new Project("Portal");
        UniqueClientProjectList memberships = new UniqueClientProjectList();
        memberships.add(new ClientProject(ALICE, hackathon));
        memberships.add(new ClientProject(ALICE, portal));
        memberships.add(new ClientProject(BENSON, hackathon));

        memberships.removeForClient(ALICE);
        assertEquals(List.of(new ClientProject(BENSON, hackathon)),
                List.copyOf(memberships.asUnmodifiableObservableList()));

        memberships.removeForProject(hackathon);
        assertTrue(memberships.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void replaceClient_successAndDuplicate_throws() {
        Project hackathon = new Project("Hackathon");
        Person updatedAlice = new PersonBuilder(ALICE).withPhone("91234567").build();
        UniqueClientProjectList memberships = new UniqueClientProjectList();
        memberships.add(new ClientProject(ALICE, hackathon));
        memberships.add(new ClientProject(BENSON, new Project("Portal")));

        memberships.replaceClient(ALICE, updatedAlice);
        assertEquals(updatedAlice, List.copyOf(memberships.asUnmodifiableObservableList()).get(0).getClient());

        UniqueClientProjectList duplicateMemberships = new UniqueClientProjectList();
        duplicateMemberships.add(new ClientProject(ALICE, hackathon));
        duplicateMemberships.add(new ClientProject(BENSON, hackathon));
        assertThrows(DuplicateClientProjectException.class, () ->
                duplicateMemberships.replaceClient(BENSON, ALICE));
    }

    @Test
    public void setClientProjects_duplicate_throws() {
        ClientProject membership = new ClientProject(ALICE, new Project("Hackathon"));
        UniqueClientProjectList memberships = new UniqueClientProjectList();

        memberships.setClientProjects(List.of(membership));
        assertThrows(DuplicateClientProjectException.class, () ->
                memberships.setClientProjects(List.of(membership, membership)));
    }
}
