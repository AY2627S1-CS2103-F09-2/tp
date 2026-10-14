package seedu.address.model.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class ClientProjectTest {

    @Test
    public void equals_usesClientIdentityAndProjectName() {
        ClientProject membership = new ClientProject(ALICE, new Project("Hackathon"));
        ClientProject sameMembership = new ClientProject(
                new PersonBuilder(ALICE).withPhone("91234567").build(), new Project("hackathon"));

        assertEquals(membership, sameMembership);
        assertTrue(membership.isSameClientProject(sameMembership));
    }

    @Test
    public void withClient_replacesClientOnly() {
        Project project = new Project("Hackathon");
        ClientProject membership = new ClientProject(ALICE, project);
        ClientProject updated = membership.withClient(new PersonBuilder(ALICE).withPhone("91234567").build());

        assertEquals(project, updated.getProject());
        assertEquals("Alice Pauline", updated.getClient().getName().fullName);
        assertEquals("91234567", updated.getClient().getPhone().value);
    }

    @Test
    public void objectMethods_handleDifferentObjects() {
        ClientProject membership = new ClientProject(ALICE, new Project("Hackathon"));

        assertEquals(membership, membership);
        assertFalse(membership.isSameClientProject(null));
        assertFalse(membership.equals("membership"));
        assertEquals(membership.hashCode(), new ClientProject(ALICE, new Project("hackathon")).hashCode());
        assertTrue(membership.toString().contains("client="));
    }
}
