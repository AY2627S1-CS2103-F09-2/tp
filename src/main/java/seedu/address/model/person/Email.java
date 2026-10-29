package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's email in the client book.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    // RFC 5321's 256-octet path limit includes '<' and '>' (see RFC 3696 erratum 1690).
    public static final int MAX_LENGTH = 254;
    public static final String MESSAGE_LENGTH_CONSTRAINTS =
            "Email addresses can contain at most " + MAX_LENGTH + " characters.";

    private static final String SPECIAL_CHARACTERS = "+_.-";
    public static final String MESSAGE_CONSTRAINTS = "Emails should be of the format local-part@domain "
            + "and adhere to the following constraints:\n"
            + "1. The local-part should only contain alphanumeric characters and these special characters, excluding "
            + "the parentheses, (" + SPECIAL_CHARACTERS + "). The local-part may not start or end with any special "
            + "characters.\n"
            + "2. The local-part is followed by an '@' and then a domain name. The domain name is made up of domain "
            + "labels separated by periods.\n"
            + "The domain name must:\n"
            + "    - end with a domain label at least 2 characters long\n"
            + "    - have each domain label start and end with alphanumeric characters\n"
            + "    - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.\n"
            + "3. The email address should not exceed " + MAX_LENGTH + " characters.";
    // alphanumeric and special characters
    // Possessive quantifiers avoid recursive backtracking when checking the format of overlong emails.
    private static final String ALPHANUMERIC_NO_UNDERSCORE = "[^\\W_]++"; // alphanumeric characters except underscore
    private static final String LOCAL_PART_REGEX = "^" + ALPHANUMERIC_NO_UNDERSCORE + "([" + SPECIAL_CHARACTERS + "]"
            + ALPHANUMERIC_NO_UNDERSCORE + ")*+";
    private static final String DOMAIN_PART_REGEX = ALPHANUMERIC_NO_UNDERSCORE
            + "(-" + ALPHANUMERIC_NO_UNDERSCORE + ")*+";
    // Two domain-part matches require adjacent alphanumeric characters in the last label.
    private static final String DOMAIN_LAST_PART_REGEX = "(?=[^.]*[^\\W_]{2})" + DOMAIN_PART_REGEX + "$";
    private static final String DOMAIN_REGEX = "(" + DOMAIN_PART_REGEX + "\\.)*+" + DOMAIN_LAST_PART_REGEX;
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_REGEX;

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), getValidationMessage(email));
        value = email;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidEmail(String test) {
        // Reject overlong input before running the regex; check its format only when selecting an error message.
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the length constraint message only if the email is overlong and otherwise has a valid format.
     * Returns the full constraints message for other invalid emails.
     */
    public static String getValidationMessage(String email) {
        return email.length() > MAX_LENGTH && email.matches(VALIDATION_REGEX)
                ? MESSAGE_LENGTH_CONSTRAINTS : MESSAGE_CONSTRAINTS;
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
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
