package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectRoleCommand;
import seedu.address.logic.parser.exceptions.InvalidIndexException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.Project;

/** Parses arguments for changing a client's project role. */
public class ProjectRoleCommandParser implements Parser<ProjectRoleCommand> {

    private static final Pattern ROLE_SUFFIX = Pattern.compile("(?s)^(.+?)\\s+r/(.*)$");

    @Override
    public ProjectRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE));
        }

        try {
            Index index = ParserUtil.parseIndex(arguments[0]);
            Matcher roleMatcher = ROLE_SUFFIX.matcher(arguments[1]);
            if (!roleMatcher.matches()) {
                throw new ParseException(ProjectRoleCommand.MESSAGE_USAGE);
            }
            Project project = ParserUtil.parseProject(roleMatcher.group(1));
            String role = ParserUtil.parseRole(roleMatcher.group(2));
            return new ProjectRoleCommand(index, project, role);
        } catch (InvalidIndexException iie) {
            throw iie;
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE), pe);
        }
    }
}
