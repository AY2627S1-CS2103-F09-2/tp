package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_surroundingSpaces_removed() {
        assertEquals("Prefers email", new Note("  Prefers email \t").value);
    }

    @Test
    public void isEmpty() {
        assertTrue(Note.EMPTY.isEmpty());
        assertTrue(new Note("   ").isEmpty());
        assertFalse(new Note("Based in London").isEmpty());
    }

    @Test
    public void equals() {
        Note note = new Note("Prefers email");

        assertTrue(note.equals(note));
        assertTrue(note.equals(new Note(" Prefers email ")));
        assertFalse(note.equals(new Note("Prefers calls")));
        assertFalse(note.equals(null));
        assertFalse(note.equals("Prefers email"));
        assertEquals(note.hashCode(), new Note("Prefers email").hashCode());
    }
}
