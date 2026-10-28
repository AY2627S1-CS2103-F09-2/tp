package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.model.Model;
import seedu.address.model.person.Status;

/**
 * Lists all contacts in the client book to the user, or only those with a given status.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all contacts.";
    public static final String MESSAGE_EMPTY = "No contacts to list.";
    public static final String MESSAGE_FILTERED_SUCCESS = "Listed %1$d %2$s with the status %3$s.";
    public static final String MESSAGE_FILTERED_EMPTY = "No contacts have the status %1$s.";

    private final Optional<Status> statusFilter;

    /**
     * Creates a command that lists every contact.
     */
    public ListCommand() {
        statusFilter = Optional.empty();
    }

    /**
     * Creates a command that lists only the contacts with the given status.
     */
    public ListCommand(Status status) {
        requireNonNull(status);
        statusFilter = Optional.of(status);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (statusFilter.isEmpty()) {
            model.showAllContacts();
            return new CommandResult(model.getFilteredPersonList().isEmpty() ? MESSAGE_EMPTY : MESSAGE_SUCCESS);
        }

        Status status = statusFilter.get();
        model.updateFilteredPersonList(person -> person.getStatus() == status);
        int count = model.getFilteredPersonList().size();
        if (count == 0) {
            return new CommandResult(String.format(MESSAGE_FILTERED_EMPTY, status));
        }
        return new CommandResult(String.format(MESSAGE_FILTERED_SUCCESS, count,
                count == 1 ? "contact" : "contacts", status));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return statusFilter.equals(otherListCommand.statusFilter);
    }
}
