package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the client book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS = "Phone numbers should start with a digit, + or (, "
            + "contain at least 3 digits, and may also contain spaces, letters (e.g. HP, ext) "
            + "and the characters + - ( ) . /";

    /*
     * Phone numbers are written in many ways, e.g. +65 9123 4567, (65) 9123-4567 or 1234 5678 (HP),
     * so spaces, letters for labels, and the usual separators are allowed alongside the digits.
     * The lookahead requires at least 3 digits, and the first character must be a digit, + or (,
     * so that blank or word-only input stays invalid.
     */
    public static final String VALIDATION_REGEX = "(?=(?:\\D*\\d){3})[\\d+(][\\p{L}\\d +()./-]*";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
