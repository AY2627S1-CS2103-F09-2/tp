package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/** Changes role for a client's membership in a project. */
public class ProjectRoleCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "role";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Changes a client's role in a project. Parameters: CLIENT_INDEX (must be a positive integer) "
            + "PROJECT r/ROLE (leave ROLE empty to remove the role)\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 Client Portal r/developer";

    public static final String MESSAGE_SUCCESS = "Role for client %1$s on project %2$s changed to %3$s";
    public static final String MESSAGE_ROLE_REMOVED = "Role for client %1$s on project %2$s removed";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";
    public static final String MESSAGE_CLIENT_NOT_IN_PROJECT = "This client does not belong to the project.";

    private final Index targetIndex;
    private final Project project;
    private final String role;

    /** Creates a command to update role for the displayed client. */
    public ProjectRoleCommand(Index targetIndex, Project project, String role) {
        requireAllNonNull(targetIndex, project, role);
        this.targetIndex = targetIndex;
        this.project = project;
        this.role = role.trim().replaceAll("\\s+", " ");
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.getInvalidContactDisplayedIndexMessage(lastShownList.size()));
        }

        Person client = lastShownList.get(targetIndex.getZeroBased());
        Project existingProject = model.getClientBook().getProjectList().stream()
                .filter(project::isSameProject)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_PROJECT_NOT_FOUND, project)));
        ClientProject membership = model.getClientBook().getClientProjectList().stream()
                .filter(candidate -> candidate.getClient().isSamePerson(client)
                        && candidate.getProject().equals(existingProject))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_CLIENT_NOT_IN_PROJECT));

        model.setClientProjectRole(membership, role);
        if (role.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_ROLE_REMOVED, client.getName(), existingProject));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), existingProject, role));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectRoleCommand otherProjectRoleCommand)) {
            return false;
        }

        return targetIndex.equals(otherProjectRoleCommand.targetIndex)
                && project.equals(otherProjectRoleCommand.project)
                && role.equals(otherProjectRoleCommand.role);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("project", project)
                .add("role", role)
                .toString();
    }
}
