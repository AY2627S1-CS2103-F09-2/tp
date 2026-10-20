package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents where a client's engagement stands: lead, active, on hold or done.
 * {@link #NONE} is used for a contact without a status, e.g. a client's finance officer.
 */
public enum Status {
    NONE("none"),
    LEAD("lead"),
    ACTIVE("active"),
    ON_HOLD("on hold"),
    DONE("done");

    public static final String MESSAGE_CONSTRAINTS =
            "Status should be one of: lead, active, on hold, done (or none to clear it)";

    private final String label;

    Status(String label) {
        this.label = label;
    }

    /**
     * Returns the status written as {@code text}, if there is one.
     * Case is ignored, and hyphens, underscores and repeated spaces count as one space,
     * so "On-Hold", "on_hold" and "on   hold" all mean {@link #ON_HOLD}. "onhold" is accepted too.
     */
    public static Optional<Status> fromText(String text) {
        requireNonNull(text);
        String spaced = text.trim().toLowerCase(Locale.ROOT).replaceAll("[-_\\s]+", " ");
        String normalized = spaced.equals("onhold") ? ON_HOLD.label : spaced;
        return Arrays.stream(values())
                .filter(status -> status.label.equals(normalized))
                .findFirst();
    }

    /**
     * Returns true if {@code text} names a status.
     */
    public static boolean isValidStatus(String text) {
        return fromText(text).isPresent();
    }

    /**
     * Returns true if this is the status of a contact who has none.
     */
    public boolean isNone() {
        return this == NONE;
    }

    @Override
    public String toString() {
        return label;
    }
}
