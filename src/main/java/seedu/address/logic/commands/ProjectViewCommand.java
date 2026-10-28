package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Project;

/**
 * Filters the contact list to clients belonging to an existing project.
 */
public class ProjectViewCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Lists the clients belonging to a project. "
            + "Parameters: PROJECT\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " Client Portal";

    public static final String MESSAGE_SUCCESS = "Listed %1$d %2$s in project: %3$s";
    public static final String MESSAGE_EMPTY = "Project %1$s has no clients.";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";

    private static final Logger logger = LogsCenter.getLogger(ProjectViewCommand.class);

    private final Project project;

    /**
     * Creates a command that views the project with the given name.
     *
     * @param project The project to view, matched by name ignoring case and extra spaces.
     */
    public ProjectViewCommand(Project project) {
        this.project = requireNonNull(project);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Project existingProject = model.getClientBook().getProjectList().stream()
                .filter(project::isSameProject)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_PROJECT_NOT_FOUND, project)));

        model.showProjectClients(existingProject);
        int count = model.getFilteredPersonList().size();
        logger.fine("Project view found " + count + " clients for " + existingProject);
        if (count == 0) {
            return new CommandResult(String.format(MESSAGE_EMPTY, existingProject));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, count,
                count == 1 ? "client" : "clients", existingProject));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectViewCommand otherProjectViewCommand)) {
            return false;
        }

        return project.equals(otherProjectViewCommand.project);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("project", project)
                .toString();
    }
}
