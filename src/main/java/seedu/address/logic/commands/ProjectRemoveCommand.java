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
 * Detaches a client identified by their displayed index from an existing project.
 */
public class ProjectRemoveCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "remove";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Detaches a client from an existing project. "
            + "Use one comma between client indexes; spaces around commas are allowed. "
            + "Parameters: CLIENT_INDEX[,CLIENT_INDEX...] PROJECT\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1,2 Client Portal";

    public static final String MESSAGE_SUCCESS = "Client %1$s removed from project: %2$s";
    public static final String MESSAGE_SUCCESS_MULTIPLE = "Clients removed from project %1$s: %2$s";
    public static final String MESSAGE_SKIPPED = "Skipped clients not in project: %1$s";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "Project does not exist: %1$s";
    public static final String MESSAGE_CLIENT_NOT_IN_PROJECT = "This client does not belong to the project.";

    private final List<Index> targetIndexes;
    private final Project project;

    /**
     * Creates a ProjectRemoveCommand to detach the client at {@code targetIndex} from {@code project}.
     */
    public ProjectRemoveCommand(Index targetIndex, Project project, Index... unused) {
        this(List.of(targetIndex), project);
    }

    /**
     * Creates a ProjectRemoveCommand to detach clients at {@code targetIndexes} from {@code project}.
     */
    public ProjectRemoveCommand(List<Index> targetIndexes, Project project) {
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
            if (!model.hasClientProject(membership)) {
                throw new CommandException(MESSAGE_CLIENT_NOT_IN_PROJECT);
            }
            model.removeClientProject(membership);
            return new CommandResult(String.format(MESSAGE_SUCCESS, client.getName(), existingProject));
        }

        List<Person> removed = new ArrayList<>();
        List<Person> skipped = new ArrayList<>();
        for (Person client : clients) {
            ClientProject membership = new ClientProject(client, existingProject);
            if (!model.hasClientProject(membership)) {
                skipped.add(client);
            } else {
                model.removeClientProject(membership);
                removed.add(client);
            }
        }

        StringBuilder feedback = new StringBuilder();
        if (!removed.isEmpty()) {
            feedback.append(String.format(MESSAGE_SUCCESS_MULTIPLE, existingProject,
                    removed.stream().map(person -> person.getName().fullName).collect(Collectors.joining(", "))));
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

        if (!(other instanceof ProjectRemoveCommand otherProjectRemoveCommand)) {
            return false;
        }

        return targetIndexes.equals(otherProjectRemoveCommand.targetIndexes)
                && project.equals(otherProjectRemoveCommand.project);
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
