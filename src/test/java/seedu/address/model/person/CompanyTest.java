package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CompanyTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Company(null));
    }

    @Test
    public void constructor_invalidCompany_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Company(""));
        assertThrows(IllegalArgumentException.class, () -> new Company(" Acme"));
    }

    @Test
    public void isValidCompany() {
        // null company
        assertThrows(NullPointerException.class, () -> Company.isValidCompany(null));

        // invalid companies
        assertFalse(Company.isValidCompany("")); // empty string
        assertFalse(Company.isValidCompany(" ")); // spaces only

        // valid companies
        assertTrue(Company.isValidCompany("Acme"));
        assertTrue(Company.isValidCompany("A")); // one character
        assertTrue(Company.isValidCompany("Yu & Co. (Pte) Ltd")); // symbols
        assertTrue(Company.isValidCompany("\u00dcnited Caf\u00e9 Holdings 2")); // non-English letters and numbers
    }

    @Test
    public void isEmpty() {
        assertTrue(Company.EMPTY.isEmpty());
        assertFalse(new Company("Acme").isEmpty());
        assertEquals("", Company.EMPTY.toString());
    }

    @Test
    public void equals() {
        Company company = new Company("Acme");

        // same values -> returns true
        assertTrue(company.equals(new Company("Acme")));
        assertEquals(company.hashCode(), new Company("Acme").hashCode());

        // same object -> returns true
        assertTrue(company.equals(company));

        // null -> returns false
        assertFalse(company.equals(null));

        // different types -> returns false
        assertFalse(company.equals(5.0f));

        // different values -> returns false
        assertFalse(company.equals(new Company("Globex")));
        assertFalse(company.equals(Company.EMPTY));
    }
}
