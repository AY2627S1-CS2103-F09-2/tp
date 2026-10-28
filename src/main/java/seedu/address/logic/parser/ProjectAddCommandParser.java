package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectAddCommand;
import seedu.address.logic.parser.exceptions.InvalidIndexException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * Parses arguments for attaching a client to a project.
 */
public class ProjectAddCommandParser implements Parser<ProjectAddCommand> {

    private static final Pattern INDEXES_AND_PROJECT =
            Pattern.compile("^([^\\s,]+(?:\\s*,\\s*[^\\s,]+)*)\\s+(.+)$");
    private static final Pattern ROLE_SUFFIX = Pattern.compile("(?s)^(.+?)\\s+r/(.*)$");

    /**
     * Parses the client index and treats the entire remainder as the project name.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ProjectAddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher argumentsMatcher = INDEXES_AND_PROJECT.matcher(args.trim());
        if (!argumentsMatcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE));
        }

        try {
            List<Index> indexes = ParserUtil.parseIndexes(argumentsMatcher.group(1));
            String projectName = argumentsMatcher.group(2);
            String role = ClientProject.DEFAULT_ROLE;
            Matcher roleMatcher = ROLE_SUFFIX.matcher(projectName);
            if (roleMatcher.matches()) {
                projectName = roleMatcher.group(1);
                role = ParserUtil.parseRole(roleMatcher.group(2));
            }
            Project project = ParserUtil.parseProject(projectName);
            return new ProjectAddCommand(indexes, project, role);
        } catch (InvalidIndexException iie) {
            throw iie;
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE), pe);
        }
    }
}
