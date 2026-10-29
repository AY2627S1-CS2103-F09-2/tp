package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INDEX_NOT_IN_LIST =
            "The contact index provided is invalid. Pick one of the numbers shown in the contact list, "
            + "starting from 1.";
    public static final String MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX =
            "The contact index provided is invalid. Pick a number from 1 to %1$d.";
    public static final String MESSAGE_NO_CONTACTS_DISPLAYED =
            "The displayed contact list contains no contacts.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d contact(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_STATUS_NOT_SET_HERE =
            "%1$s does not set a status (s/). Use the status command instead, e.g. status 1 active.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an invalid index message for a displayed contact list of {@code size}.
     */
    public static String getInvalidContactDisplayedIndexMessage(int size) {
        assert size >= 0;

        return size == 0
                ? MESSAGE_NO_CONTACTS_DISPLAYED
                : String.format(MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, size);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail());
        if (!person.getAddress().isEmpty()) {
            builder.append("; Address: ").append(person.getAddress());
        }
        if (!person.getCompany().isEmpty()) {
            builder.append("; Company: ").append(person.getCompany());
        }
        if (!person.getTags().isEmpty()) {
            builder.append("; Tags: ");
            person.getTags().forEach(builder::append);
        }
        return builder.toString();
    }

}
