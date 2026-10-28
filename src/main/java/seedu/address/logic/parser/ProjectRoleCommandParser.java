package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ProjectRoleCommand;
import seedu.address.logic.parser.exceptions.InvalidIndexException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.project.Project;

/** Parses arguments for changing a client's project role. */
public class ProjectRoleCommandParser implements Parser<ProjectRoleCommand> {

    @Override
    public ProjectRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE));
        }

        // The tokenizer recognizes spaces, so normalize whitespace immediately before role prefixes.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                arguments[1].replaceAll("\\s+(?=" + PREFIX_ROLE + ")", " "), PREFIX_ROLE);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE);

        try {
            Index index = ParserUtil.parseIndex(arguments[0]);
            if (argMultimap.getValue(PREFIX_ROLE).isEmpty()) {
                throw new ParseException(ProjectRoleCommand.MESSAGE_USAGE);
            }
            Project project = ParserUtil.parseProject(argMultimap.getPreamble());
            String role = ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get());
            return new ProjectRoleCommand(index, project, role);
        } catch (InvalidIndexException iie) {
            throw iie;
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProjectRoleCommand.MESSAGE_USAGE), pe);
        }
    }
}
