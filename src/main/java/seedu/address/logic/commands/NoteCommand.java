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
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

/**
 * Adds, replaces or removes the note kept against a contact in the displayed list.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Keeps a short note against the contact identified by the index number "
            + "used in the displayed contact list, replacing any existing note. "
            + "Leave out the text to remove the note.\n"
            + "Parameters: INDEX (must be a positive integer) [TEXT]\n"
            + "Example: " + COMMAND_WORD + " 1 Based in London, prefers email";

    public static final String MESSAGE_ADD_SUCCESS = "Added a note to %1$s: %2$s";
    public static final String MESSAGE_REMOVE_SUCCESS = "Removed the note from %1$s.";
    public static final String MESSAGE_UNCHANGED = "%1$s already has this note. Nothing has changed.";
    public static final String MESSAGE_NOTHING_TO_REMOVE = "%1$s has no note to remove.";

    private static final Logger logger = LogsCenter.getLogger(NoteCommand.class);

    private final Index targetIndex;
    private final Note note;

    /**
     * Creates a command that gives the contact at {@code targetIndex} the given note.
     *
     * @param targetIndex Index of the contact in the displayed list.
     * @param note The new note, or {@link Note#EMPTY} to remove it.
     */
    public NoteCommand(Index targetIndex, Note note) {
        requireAllNonNull(targetIndex, note);
        this.targetIndex = targetIndex;
        this.note = note;
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
        if (target.getNote().equals(note)) {
            return new CommandResult(note.isEmpty()
                    ? String.format(MESSAGE_NOTHING_TO_REMOVE, name)
                    : String.format(MESSAGE_UNCHANGED, name));
        }

        Person updated = target.withNote(note);
        assert updated.isSamePerson(target) : "changing the note must not change who the contact is";
        model.setPerson(target, updated);
        logger.fine("Note of " + name + " changed");

        return new CommandResult(note.isEmpty()
                ? String.format(MESSAGE_REMOVE_SUCCESS, name)
                : String.format(MESSAGE_ADD_SUCCESS, name, note));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NoteCommand otherNoteCommand)) {
            return false;
        }

        return targetIndex.equals(otherNoteCommand.targetIndex)
                && note.equals(otherNoteCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("note", note)
                .toString();
    }
}
