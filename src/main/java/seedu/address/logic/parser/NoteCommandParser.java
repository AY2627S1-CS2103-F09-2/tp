package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Note;

/**
 * Parses input arguments and creates a new {@code NoteCommand} object.
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    /**
     * Parses {@code args}, of the form {@code INDEX [TEXT]}, into a {@code NoteCommand}.
     * Everything after the index is the note, so it may contain any characters, including prefixes such as t/.
     *
     * @throws ParseException if the index is missing or invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] parts = args.trim().split("\\s+", 2);

        Index index;
        try {
            index = ParserUtil.parseIndex(parts[0]);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE), pe);
        }
        String text = parts.length == 2 ? parts[1] : "";
        return new NoteCommand(index, new Note(text));
    }
}
