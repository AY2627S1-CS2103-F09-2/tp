package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses project subcommands and creates the corresponding command.
 */
public class ProjectCommandParser implements Parser<Command> {

    /**
     * Parses a project subcommand followed by its arguments.
     * The entire remainder after {@code create} is treated as the project name.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2 || !ProjectCreateCommand.SUBCOMMAND_WORD.equalsIgnoreCase(arguments[0])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
        }

        return new ProjectCreateCommand(ParserUtil.parseProject(arguments[1]));
    }
}
