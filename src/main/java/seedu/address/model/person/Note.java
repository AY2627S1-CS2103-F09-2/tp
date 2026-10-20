package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a short note kept against a contact, e.g. their timezone or how they prefer to be contacted.
 * Guarantees: immutable; any text is valid, and an empty note means the contact has none.
 */
public class Note {

    /** The note of a contact who has none. */
    public static final Note EMPTY = new Note("");

    public final String value;

    /**
     * Constructs a {@code Note}. Spaces at the start and end are removed.
     *
     * @param note Any text, possibly empty.
     */
    public Note(String note) {
        requireNonNull(note);
        value = note.strip();
    }

    /**
     * Returns true if the contact has no note.
     */
    public boolean isEmpty() {
        return value.isEmpty();
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
        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
