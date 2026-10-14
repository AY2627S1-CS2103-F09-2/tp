package seedu.address.model.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ProjectTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Project(null));
    }

    @Test
    public void constructor_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Project("   "));
    }

    @Test
    public void constructor_trimsAndCollapsesSpaces() {
        assertEquals("Hackathon 2026", new Project("  Hackathon   2026  ").getName());
    }

    @Test
    public void equals_ignoresCase() {
        assertEquals(new Project("Hackathon"), new Project("hackathon"));
        assertTrue(new Project("Hackathon").isSameProject(new Project("HACKATHON")));
        assertFalse(new Project("Hackathon").equals(new Project("Client Portal")));
    }
}
