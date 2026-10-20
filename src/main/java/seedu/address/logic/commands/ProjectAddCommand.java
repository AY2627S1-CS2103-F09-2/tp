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
            + "Parameters: CLIENT_INDEX (must be a positive integer) PROJECT\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 Client Portal";

    public static final String MESSAGE_SUCCESS = "Client %1$s added to project: %2$s";
    public static final String MESSAGE_SUCCESS_MULTIPLE = "Clients added to project %1$s: %2$s";
    public static final String MESSAGE_SKIPPED = "Skipped clients already in project: %1$s";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";
    public static final String MESSAGE_DUPLICATE_CLIENT_PROJECT = "This client already belongs to the project.";

    private final List<Index> targetIndexes;
    private final Project project;

    /**
     * Creates a ProjectAddCommand to attach the client at {@code targetIndex} to {@code project}.
     */
    public ProjectAddCommand(Index targetIndex, Project project) {
        this(List.of(targetIndex), project);
    }

    /**
     * Creates a ProjectAddCommand to attach clients at {@code targetIndexes} to {@code project}.
     */
    public ProjectAddCommand(List<Index> targetIndexes, Project project, boolean... unused) {
        requireAllNonNull(targetIndexes, project);
        requireAllNonNull(targetIndexes);
        this.targetIndexes = List.copyOf(targetIndexes);
        this.project = project;
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
            ClientProject membership = new ClientProject(client, existingProject);
            if (model.hasClientProject(membership)) {
                throw new CommandException(MESSAGE_DUPLICATE_CLIENT_PROJECT);
            }
            model.addClientProject(membership);
            return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), existingProject));
        }

        List<Person> added = new ArrayList<>();
        List<Person> skipped = new ArrayList<>();
        for (Person client : clients) {
            ClientProject membership = new ClientProject(client, existingProject);
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
                && project.equals(otherProjectAddCommand.project);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add(targetIndexes.size() == 1 ? "targetIndex" : "targetIndexes",
                        targetIndexes.size() == 1 ? targetIndexes.get(0) : targetIndexes)
                .add("project", project)
                .toString();
    }
}
