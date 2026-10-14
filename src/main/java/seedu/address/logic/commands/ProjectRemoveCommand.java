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

/**
 * Detaches a client identified by their displayed index from an existing project.
 */
public class ProjectRemoveCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "remove";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Detaches a client from an existing project. "
            + "Parameters: CLIENT_INDEX (must be a positive integer) PROJECT\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 Client Portal";

    public static final String MESSAGE_SUCCESS = "Client %1$s removed from project: %2$s";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";
    public static final String MESSAGE_CLIENT_NOT_IN_PROJECT = "This client does not belong to the project.";

    private final Index targetIndex;
    private final Project project;

    /**
     * Creates a ProjectRemoveCommand to detach the client at {@code targetIndex} from {@code project}.
     */
    public ProjectRemoveCommand(Index targetIndex, Project project) {
        requireAllNonNull(targetIndex, project);
        this.targetIndex = targetIndex;
        this.project = project;
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
        Project existingProject = model.getClientBook().getProjectList().stream()
                .filter(project::isSameProject)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_PROJECT_NOT_FOUND, project)));
        ClientProject membership = new ClientProject(client, existingProject);
        if (!model.hasClientProject(membership)) {
            throw new CommandException(MESSAGE_CLIENT_NOT_IN_PROJECT);
        }

        model.removeClientProject(membership);
        return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), existingProject));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectRemoveCommand otherProjectRemoveCommand)) {
            return false;
        }

        return targetIndex.equals(otherProjectRemoveCommand.targetIndex)
                && project.equals(otherProjectRemoveCommand.project);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("project", project)
                .toString();
    }
}
