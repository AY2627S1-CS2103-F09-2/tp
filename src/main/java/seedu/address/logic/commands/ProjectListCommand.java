package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;

/**
 * Lists the projects assigned to a client identified by their displayed index.
 */
public class ProjectListCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Lists the projects assigned to a client. "
            + "Parameters: CLIENT_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Projects for client %1$s:\n%2$s";
    public static final String MESSAGE_EMPTY = "Client %1$s has no projects.";

    private final Index targetIndex;

    /**
     * Creates a ProjectListCommand to list projects for the client at {@code targetIndex}.
     */
    public ProjectListCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                    lastShownList.size()));
        }

        Person client = lastShownList.get(targetIndex.getZeroBased());
        List<ClientProject> memberships = model.getClientBook().getClientProjectsForPerson(client);
        if (memberships.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_EMPTY, client.getName()));
        }

        String projectNames = memberships.stream()
                .map(ProjectListCommand::formatMembership)
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), projectNames));
    }

    private static String formatMembership(ClientProject membership) {
        String projectName = membership.getProject().getName();
        return membership.getRole().isEmpty()
                ? projectName : projectName + " (" + membership.getRole() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectListCommand otherProjectListCommand)) {
            return false;
        }

        return targetIndex.equals(otherProjectListCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
