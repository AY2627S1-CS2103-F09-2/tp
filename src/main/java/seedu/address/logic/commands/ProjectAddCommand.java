package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * Attaches a client identified by their displayed index to an existing project.
 */
public class ProjectAddCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Attaches a client to an existing project. "
            + "Parameters: CLIENT_INDEX[,CLIENT_INDEX...] PROJECT [r/ROLE]\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1,2 Client Portal r/developer";

    public static final String MESSAGE_SUCCESS = "Client %1$s added to project: %2$s";
    public static final String MESSAGE_SUCCESS_MULTIPLE = "Clients added to project %1$s: %2$s";
    public static final String MESSAGE_SKIPPED = "Skipped clients already in project: %1$s";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";
    public static final String MESSAGE_DUPLICATE_CLIENT_PROJECT = "This client already belongs to the project.";

    private final List<Index> targetIndexes;
    private final Project project;
    private final String role;

    /**
     * Creates a ProjectAddCommand to attach the client at {@code targetIndex} to {@code project}.
     */
    public ProjectAddCommand(Index targetIndex, Project project, Index... unused) {
        this(targetIndex, project, ClientProject.DEFAULT_ROLE);
    }

    /** Creates a ProjectAddCommand with a role for each new membership. */
    public ProjectAddCommand(Index targetIndex, Project project, String role) {
        this(List.of(targetIndex), project, role);
    }

    /**
     * Creates a ProjectAddCommand to attach clients at {@code targetIndexes} to {@code project}.
     */
    public ProjectAddCommand(List<Index> targetIndexes, Project project) {
        this(targetIndexes, project, ClientProject.DEFAULT_ROLE);
    }

    /** Creates a ProjectAddCommand with a role for each new membership. */
    public ProjectAddCommand(List<Index> targetIndexes, Project project, String role) {
        requireAllNonNull(targetIndexes, project);
        requireAllNonNull(targetIndexes);
        requireNonNull(role);
        this.targetIndexes = List.copyOf(targetIndexes);
        this.project = project;
        this.role = role.trim().replaceAll("\\s+", " ");
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                        lastShownList.size()));
            }
        }

        Project existingProject = model.getClientBook().getProjectList().stream()
                .filter(project::isSameProject)
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_PROJECT_NOT_FOUND, project)));

        List<Person> clients = targetIndexes.stream()
                .map(index -> lastShownList.get(index.getZeroBased()))
                .toList();
        if (clients.size() == 1) {
            Person client = clients.get(0);
            ClientProject membership = new ClientProject(client, existingProject, role);
            if (model.hasClientProject(membership)) {
                throw new CommandException(MESSAGE_DUPLICATE_CLIENT_PROJECT);
            }
            model.addClientProject(membership);
            return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), existingProject));
        }

        List<Person> added = new ArrayList<>();
        List<Person> skipped = new ArrayList<>();
        for (Person client : clients) {
            ClientProject membership = new ClientProject(client, existingProject, role);
            if (model.hasClientProject(membership)) {
                skipped.add(client);
            } else {
                model.addClientProject(membership);
                added.add(client);
            }
        }

        StringBuilder feedback = new StringBuilder();
        if (!added.isEmpty()) {
            feedback.append(String.format(MESSAGE_SUCCESS_MULTIPLE, existingProject,
                    added.stream().map(person -> person.getName().fullName).collect(Collectors.joining(", "))));
        }
        if (!skipped.isEmpty()) {
            if (!feedback.isEmpty()) {
                feedback.append("\n");
            }
            feedback.append(String.format(MESSAGE_SKIPPED,
                    skipped.stream().map(person -> person.getName().fullName).collect(Collectors.joining(", "))));
        }
        return new CommandResult(feedback.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectAddCommand otherProjectAddCommand)) {
            return false;
        }

        return targetIndexes.equals(otherProjectAddCommand.targetIndexes)
                && project.equals(otherProjectAddCommand.project)
                && role.equals(otherProjectAddCommand.role);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add(targetIndexes.size() == 1 ? "targetIndex" : "targetIndexes",
                        targetIndexes.size() == 1 ? targetIndexes.get(0) : targetIndexes)
                .add("project", project);
        if (!role.isEmpty()) {
            builder.add("role", role);
        }
        return builder.toString();
    }
}
