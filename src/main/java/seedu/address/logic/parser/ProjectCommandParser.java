package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.commands.ProjectAllCommand;
import seedu.address.logic.commands.ProjectCreateCommand;
import seedu.address.logic.commands.ProjectDeleteCommand;
import seedu.address.logic.commands.ProjectListCommand;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.commands.ProjectRoleCommand;
import seedu.address.logic.commands.ProjectViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses project subcommands and creates the corresponding command.
 */
public class ProjectCommandParser implements Parser<Command> {

    public static final String MESSAGE_USAGE = ProjectCreateCommand.MESSAGE_USAGE + "\n"
            + ProjectAddCommand.MESSAGE_USAGE + "\n" + ProjectRemoveCommand.MESSAGE_USAGE + "\n"
            + ProjectListCommand.MESSAGE_USAGE + "\n" + ProjectRoleCommand.MESSAGE_USAGE + "\n"
            + ProjectViewCommand.MESSAGE_USAGE + "\n"
            + ProjectAllCommand.MESSAGE_USAGE + "\n"
            + ProjectDeleteCommand.MESSAGE_USAGE;

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
            case ProjectDeleteCommand.SUBCOMMAND_WORD -> {
                if (subcommandArgs.isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectDeleteCommand.MESSAGE_USAGE));
                }
                yield new ProjectDeleteCommand(ParserUtil.parseProject(subcommandArgs));
            }
            case ProjectViewCommand.SUBCOMMAND_WORD -> {
                if (subcommandArgs.isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectViewCommand.MESSAGE_USAGE));
                }
                yield new ProjectViewCommand(ParserUtil.parseProject(subcommandArgs));
            }
            case ProjectAllCommand.SUBCOMMAND_WORD -> {
                if (!subcommandArgs.isEmpty()) {
                    throw new ParseException(
                            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAllCommand.MESSAGE_USAGE));
                }
                yield new ProjectAllCommand();
            }
            case ProjectAddCommand.SUBCOMMAND_WORD -> new ProjectAddCommandParser().parse(subcommandArgs);
            case ProjectRemoveCommand.SUBCOMMAND_WORD -> new ProjectRemoveCommandParser().parse(subcommandArgs);
            case ProjectListCommand.SUBCOMMAND_WORD -> new ProjectListCommandParser().parse(subcommandArgs);
            case ProjectRoleCommand.SUBCOMMAND_WORD -> new ProjectRoleCommandParser().parse(subcommandArgs);
            default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        };
    }
}
