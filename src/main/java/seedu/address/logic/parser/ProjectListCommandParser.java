package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for listing a client's projects.
 */
public class ProjectListCommandParser implements Parser<ProjectListCommand> {

    /**
     * Parses a single client index.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ProjectListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ProjectListCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectListCommand.MESSAGE_USAGE), pe);
        }
    }
}
