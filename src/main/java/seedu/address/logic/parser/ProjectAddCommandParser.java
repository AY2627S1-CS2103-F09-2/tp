package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.Project;

/**
 * Parses arguments for attaching a client to a project.
 */
public class ProjectAddCommandParser implements Parser<ProjectAddCommand> {

    /**
     * Parses the client index and treats the entire remainder as the project name.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ProjectAddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(arguments[0]);
            Project project = ParserUtil.parseProject(arguments[1]);
            return new ProjectAddCommand(index, project);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE), pe);
        }
    }
}
