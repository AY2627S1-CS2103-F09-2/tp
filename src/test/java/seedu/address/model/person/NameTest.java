package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
        assertThrows(IllegalArgumentException.class, () -> new Name("   "));
        assertThrows(IllegalArgumentException.class, () -> new Name("John\tDoe"));
    }

    @Test
    public void constructor_extraSpaces_normalizesName() {
        Name name = new Name("  John   Doe  ");
        assertEquals("John Doe", name.fullName);
        assertEquals("John Doe", name.toString());
        assertEquals(new Name("John Doe"), name);
        assertEquals(new Name("John Doe").hashCode(), name.hashCode());

        assertEquals("Dr. Tan, Ah Kow", new Name("Dr.  Tan,   Ah  Kow").fullName);
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains a character that names do not use
        assertFalse(Name.isValidName("John@Doe")); // contains a character that names do not use
        assertFalse(Name.isValidName("-Peter")); // starts with a hyphen
        assertFalse(Name.isValidName("'")); // only an apostrophe
        assertFalse(Name.isValidName("John\tDoe")); // internal tabs are not spaces
        assertFalse(Name.isValidName("John\nDoe")); // internal newlines are not spaces

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Mary O\u2019Neil")); // typographic apostrophe
        assertTrue(Name.isValidName("Jean-Luc Picard")); // hyphen
        assertTrue(Name.isValidName("Jos\u00e9 N\u00fa\u00f1ez")); // accented letters
        assertTrue(Name.isValidName("Raj s/o Kumar")); // slash, as in "son of"
        assertTrue(Name.isValidName("Dr. Tan, Ah Kow")); // period and comma
        assertTrue(Name.isValidName("  John   Doe  ")); // extra spaces are normalized
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));

        // capitalization is preserved for equality of stored values
        assertFalse(name.equals(new Name("valid name")));
    }
}
