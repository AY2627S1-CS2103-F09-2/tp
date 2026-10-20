package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.Project;

/**
 * Parses arguments for detaching a client from a project.
 */
public class ProjectRemoveCommandParser implements Parser<ProjectRemoveCommand> {

    /**
     * Parses the client index and treats the entire remainder as the project name.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ProjectRemoveCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
        }

        try {
            List<Index> indexes = ParserUtil.parseIndexes(arguments[0]);
            Project project = ParserUtil.parseProject(arguments[1]);
            return new ProjectRemoveCommand(indexes, project);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE), pe);
        }
    }
}
