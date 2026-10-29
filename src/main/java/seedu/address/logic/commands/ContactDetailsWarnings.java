package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.person.Person;

/**
 * Builds warnings for contact details shared with other contacts in the client book.
 */
final class ContactDetailsWarnings {

    static final String MESSAGE_DUPLICATE_NAME_WARNING =
            "Warning: Name %1$s already belongs to another contact.";
    static final String MESSAGE_DUPLICATE_EMAIL_WARNING =
            "Warning: Email %1$s already belongs to another contact.";
    static final String MESSAGE_DUPLICATE_PHONE_WARNING =
            "Warning: Phone %1$s already belongs to another contact.";

    private ContactDetailsWarnings() {}

    /**
     * Returns warnings for all shared details before adding {@code toAdd} to {@code contacts}.
     */
    static String forAdd(List<Person> contacts, Person toAdd) {
        return getWarnings(contacts, toAdd, null);
    }

    /**
     * Returns warnings only for changed details before replacing {@code personToEdit} in {@code contacts}.
     */
    static String forEdit(List<Person> contacts, Person personToEdit, Person editedPerson) {
        requireNonNull(personToEdit);
        return getWarnings(contacts, editedPerson, personToEdit);
    }

    /**
     * Checks all contacts, excluding {@code originalPerson}. A null original person denotes an addition.
     * Name comparisons ignore case and normalized spaces; phone comparisons ignore spaces.
     */
    private static String getWarnings(List<Person> contacts, Person updatedPerson, Person originalPerson) {
        requireNonNull(contacts);
        requireNonNull(updatedPerson);

        boolean checkName = originalPerson == null
                || !originalPerson.getName().fullName.equalsIgnoreCase(updatedPerson.getName().fullName);
        boolean checkEmail = originalPerson == null || !originalPerson.getEmail().equals(updatedPerson.getEmail());
        boolean checkPhone = originalPerson == null || !originalPerson.getPhone().equals(updatedPerson.getPhone());
        if (!checkName && !checkEmail && !checkPhone) {
            return "";
        }

        boolean hasDuplicateName = false;
        boolean hasDuplicateEmail = false;
        boolean hasDuplicatePhone = false;
        for (Person person : contacts) {
            if (originalPerson != null && person.isSamePerson(originalPerson)) {
                continue;
            }
            hasDuplicateName |= checkName
                    && person.getName().fullName.equalsIgnoreCase(updatedPerson.getName().fullName);
            hasDuplicateEmail |= checkEmail && person.getEmail().equals(updatedPerson.getEmail());
            hasDuplicatePhone |= checkPhone && person.getPhone().equals(updatedPerson.getPhone());
        }

        StringBuilder warning = new StringBuilder();
        if (hasDuplicateName) {
            warning.append("\n").append(String.format(MESSAGE_DUPLICATE_NAME_WARNING, updatedPerson.getName()));
        }
        if (hasDuplicateEmail) {
            warning.append("\n").append(String.format(MESSAGE_DUPLICATE_EMAIL_WARNING, updatedPerson.getEmail()));
        }
        if (hasDuplicatePhone) {
            warning.append("\n").append(String.format(MESSAGE_DUPLICATE_PHONE_WARNING, updatedPerson.getPhone()));
        }
        return warning.toString();
    }
}
