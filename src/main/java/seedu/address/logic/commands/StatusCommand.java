package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;

/**
 * Sets or clears the engagement status of a contact in the displayed list.
 */
public class StatusCommand extends Command {

    public static final String COMMAND_WORD = "status";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sets the engagement status of the contact identified by the index number "
            + "used in the displayed contact list. Use none to clear it.\n"
            + "Parameters: INDEX (must be a positive integer) STATUS (lead, active, on hold, done or none)\n"
            + "Example: " + COMMAND_WORD + " 1 active";

    public static final String MESSAGE_SET_SUCCESS = "Set the status of %1$s to %2$s.";
    public static final String MESSAGE_CLEAR_SUCCESS = "Cleared the status of %1$s.";
    public static final String MESSAGE_UNCHANGED = "%1$s already has the status %2$s. Nothing has changed.";
    public static final String MESSAGE_NOTHING_TO_CLEAR = "%1$s has no status to clear.";

    private static final Logger logger = LogsCenter.getLogger(StatusCommand.class);

    private final Index targetIndex;
    private final Status status;

    /**
     * Creates a command that gives the contact at {@code targetIndex} the given status.
     *
     * @param targetIndex Index of the contact in the displayed list.
     * @param status The new status, or {@link Status#NONE} to clear it.
     */
    public StatusCommand(Index targetIndex, Status status) {
        requireAllNonNull(targetIndex, status);
        this.targetIndex = targetIndex;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                    lastShownList.size()));
        }

        Person target = lastShownList.get(targetIndex.getZeroBased());
        String name = target.getName().fullName;
        if (target.getStatus() == status) {
            return new CommandResult(status.isNone()
                    ? String.format(MESSAGE_NOTHING_TO_CLEAR, name)
                    : String.format(MESSAGE_UNCHANGED, name, status));
        }

        Person updated = target.withStatus(status);
        assert updated.isSamePerson(target) : "changing the status must not change who the contact is";
        model.setPerson(target, updated);
        logger.fine("Status of " + name + " changed from " + target.getStatus() + " to " + status);

        return new CommandResult(status.isNone()
                ? String.format(MESSAGE_CLEAR_SUCCESS, name)
                : String.format(MESSAGE_SET_SUCCESS, name, status));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StatusCommand otherStatusCommand)) {
            return false;
        }

        return targetIndex.equals(otherStatusCommand.targetIndex)
                && status == otherStatusCommand.status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("status", status)
                .toString();
    }
}
