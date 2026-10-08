package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the client book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)};
 * has no leading or trailing whitespace and no repeated spaces.
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS = "Names should start with a letter or digit, and may contain "
            + "only letters (including accented letters), digits, spaces and the characters ' - . , /";

    /*
     * Real names contain more than English letters and digits, e.g. O'Brien, Jean-Luc, Raj s/o Kumar, or names
     * with accented letters, so letters from any language, apostrophes (straight or curly), hyphens, periods,
     * commas and slashes are allowed.
     * The first character must be a letter or digit, otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{M}\\p{N} '\u2019.,/-]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     * Trims leading and trailing whitespace and collapses repeated spaces, preserving capitalization.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name.trim().replaceAll(" +", " ");
    }

    /**
     * Returns true if a given string is a valid name after trimming leading and trailing whitespace.
     */
    public static boolean isValidName(String test) {
        return test.trim().matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
