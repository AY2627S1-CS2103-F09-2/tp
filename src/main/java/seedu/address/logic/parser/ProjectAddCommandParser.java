package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

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
            Pattern.compile("(?s)^([^\\s,]+(?:\\s*,\\s*[^\\s,]+)*)\\s+(.+)$");

    /**
     * Parses the client indexes, project name and optional role.
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

        // The tokenizer recognizes spaces, so normalize whitespace immediately before role prefixes.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                argumentsMatcher.group(2).replaceAll("\\s+(?=" + PREFIX_ROLE + ")", " "), PREFIX_ROLE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE);
        if (argMultimap.getValue(PREFIX_ROLE).filter(String::isBlank).isPresent()) {
            throw new ParseException(ProjectAddCommand.MESSAGE_ROLE_BLANK);
        }

        try {
            List<Index> indexes = ParserUtil.parseIndexes(argumentsMatcher.group(1));
            Project project = ParserUtil.parseProject(argMultimap.getPreamble());
            String role = argMultimap.getValue(PREFIX_ROLE).isPresent()
                    ? ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get())
                    : ClientProject.DEFAULT_ROLE;
            return new ProjectAddCommand(indexes, project, role);
        } catch (InvalidIndexException iie) {
            throw iie;
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectAddCommand.MESSAGE_USAGE), pe);
        }
    }
}
