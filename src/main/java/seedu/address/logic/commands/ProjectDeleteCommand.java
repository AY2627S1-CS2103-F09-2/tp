package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Project;

/**
 * Deletes a project from the client book and removes it from every client in it.
 * The clients themselves are kept.
 */
public class ProjectDeleteCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Deletes a project and removes it from every client in it. The clients are kept. "
            + "Parameters: PROJECT\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " Client Portal";

    public static final String MESSAGE_SUCCESS = "Deleted project: %1$s (removed from %2$d %3$s)";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";

    private final Project project;

    /**
     * Creates a command that deletes the project with the given name.
     *
     * @param project The project to delete, matched by name ignoring case and extra spaces.
     */
    public ProjectDeleteCommand(Project project) {
        requireNonNull(project);
        this.project = project;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Project existingProject = model.getClientBook().getProjectList().stream()
                .filter(project::isSameProject)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_PROJECT_NOT_FOUND, project)));
        long memberCount = model.getClientBook().getClientProjectList().stream()
                .filter(membership -> membership.getProject().isSameProject(existingProject))
                .count();

        model.deleteProject(existingProject);
        return new CommandResult(String.format(MESSAGE_SUCCESS, existingProject, memberCount,
                memberCount == 1 ? "client" : "clients"));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ProjectDeleteCommand otherProjectDeleteCommand)) {
            return false;
        }

        return project.equals(otherProjectDeleteCommand.project);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("project", project)
                .toString();
    }
}
