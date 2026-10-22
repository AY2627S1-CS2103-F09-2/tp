package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * Lists every project with its number of clients.
 */
public class ProjectAllCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "all";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Lists every project with its number of clients. No parameters are required.\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Projects:\n%1$s";
    public static final String MESSAGE_EMPTY = "No projects to list.";
    public static final String MESSAGE_PROJECT_SUMMARY = "%1$s: %2$d %3$s";

    private static final Logger logger = LogsCenter.getLogger(ProjectAllCommand.class);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Project> projects = model.getClientBook().getProjectList();
        logger.fine("Listing " + projects.size() + " projects");
        if (projects.isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY);
        }

        List<ClientProject> memberships = model.getClientBook().getClientProjectList();
        String summary = projects.stream()
                .map(project -> {
                    long count = memberships.stream()
                            .filter(membership -> membership.getProject().isSameProject(project))
                            .count();
                    assert count <= model.getClientBook().getPersonList().size()
                            : "A project's client count must not exceed the total number of contacts";
                    return String.format(MESSAGE_PROJECT_SUMMARY, project, count,
                            count == 1 ? "client" : "clients");
                })
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, summary));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ProjectAllCommand;
    }
}
