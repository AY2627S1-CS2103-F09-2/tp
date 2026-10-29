package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("+65")); // less than 3 numbers after the plus sign
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("HP 91234567")); // starts with a letter
        assertFalse(Phone.isValidPhone("9312*1534")); // contains a character phone numbers do not use

        // valid phone numbers
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
        assertTrue(Phone.isValidPhone("9312 1534")); // spaces within digits
        assertTrue(Phone.isValidPhone("+65 9123 4567")); // country code
        assertTrue(Phone.isValidPhone("(65) 9123-4567")); // brackets and hyphen
        assertTrue(Phone.isValidPhone("1234 5678 (HP) 1111-3333 (Office)")); // labelled numbers
        assertTrue(Phone.isValidPhone("6123 4567 ext 89")); // extension
    }

    @Test
    public void isSameNumber_spacingDiffers_returnsTrue() {
        Phone phone = new Phone("91234567");
        assertTrue(phone.isSameNumber(new Phone("91234567")));
        assertTrue(phone.isSameNumber(new Phone("9123 4567")));
        assertTrue(phone.isSameNumber(new Phone("9 123  4567 ")));
        assertTrue(new Phone("9123 4567").isSameNumber(phone));
        assertTrue(new Phone("+6591234567").isSameNumber(new Phone("+65 9123 4567")));
        assertTrue(new Phone("+886912345678").isSameNumber(new Phone("+ 886 912 345 678")));
    }

    @Test
    public void isSameNumber_otherCharactersDiffer_returnsFalse() {
        Phone phone = new Phone("91234567");
        assertFalse(phone.isSameNumber(null));
        assertFalse(phone.isSameNumber(new Phone("9123 4568")));
        assertFalse(phone.isSameNumber(new Phone("+65 9123 4567")));
        assertFalse(phone.isSameNumber(new Phone("9123-4567")));
        assertFalse(phone.isSameNumber(new Phone("9123 4567 ext 89")));
        assertFalse(new Phone("+65 9123 4567").isSameNumber(new Phone("+886 9123 4567")));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));

        // formatting remains significant for exact equality
        assertFalse(new Phone("91234567").equals(new Phone("9123 4567")));
    }
}
