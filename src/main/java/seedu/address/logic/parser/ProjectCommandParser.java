package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses project subcommands and creates the corresponding command.
 */
public class ProjectCommandParser implements Parser<Command> {

    public static final String MESSAGE_USAGE = ProjectCreateCommand.MESSAGE_USAGE + "\n"
            + ProjectAddCommand.MESSAGE_USAGE;

    /**
     * Parses a project subcommand followed by its arguments.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        String subcommandArgs = arguments.length == 2 ? arguments[1] : "";

        return switch (arguments[0].toLowerCase(Locale.ROOT)) {
            case ProjectCreateCommand.SUBCOMMAND_WORD -> {
                if (subcommandArgs.isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectCreateCommand.MESSAGE_USAGE));
                }
                yield new ProjectCreateCommand(ParserUtil.parseProject(subcommandArgs));
            }
            case ProjectAddCommand.SUBCOMMAND_WORD -> new ProjectAddCommandParser().parse(subcommandArgs);
            default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        };
    }
}
