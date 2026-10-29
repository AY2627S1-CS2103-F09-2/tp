package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectRemoveCommand;
import seedu.address.logic.parser.exceptions.InvalidIndexException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.Project;

/**
 * Parses arguments for detaching a client from a project.
 */
public class ProjectRemoveCommandParser implements Parser<ProjectRemoveCommand> {

    private static final Pattern INDEXES_AND_PROJECT =
            Pattern.compile("^([^\\s,]+(?:\\s*,\\s*[^\\s,]+)*)\\s+(.+)$");

    /**
     * Parses the client index and treats the entire remainder as the project name.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ProjectRemoveCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher argumentsMatcher = INDEXES_AND_PROJECT.matcher(args.trim());
        if (!argumentsMatcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE));
        }

        try {
            List<Index> indexes = ParserUtil.parseIndexes(argumentsMatcher.group(1));
            Project project = ParserUtil.parseProject(argumentsMatcher.group(2));
            return new ProjectRemoveCommand(indexes, project);
        } catch (InvalidIndexException iie) {
            throw iie;
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRemoveCommand.MESSAGE_USAGE), pe);
        }
    }
}
