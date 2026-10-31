package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

    @Test
    public void equals() {
        Tag tag = new Tag("friends");

        assertTrue(tag.equals(tag));
        assertTrue(tag.equals(new Tag("friends")));

        // differs only in case -> same tag
        assertTrue(tag.equals(new Tag("Friends")));
        assertTrue(tag.equals(new Tag("FRIENDS")));
        assertEquals(tag.hashCode(), new Tag("FRIENDS").hashCode());

        assertFalse(tag.equals(new Tag("friend")));
        assertFalse(tag.equals(null));
        assertFalse(tag.equals("friends"));
    }

    @Test
    public void set_tagsDifferingOnlyInCase_keepsFirst() {
        Set<Tag> tags = new LinkedHashSet<>();
        tags.add(new Tag("Friends"));
        tags.add(new Tag("friends"));
        assertEquals(1, tags.size());
        assertEquals("Friends", tags.iterator().next().tagName);
    }

}
