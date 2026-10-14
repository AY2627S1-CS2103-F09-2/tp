package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Project;

/**
 * Creates a project in the client book.
 */
public class ProjectCreateCommand extends Command {

    public static final String COMMAND_WORD = "project";
    public static final String SUBCOMMAND_WORD = "create";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Creates a project in the client book. "
            + "Parameters: NAME\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " Client Portal";

    public static final String MESSAGE_SUCCESS = "New project created: %1$s";
    public static final String MESSAGE_DUPLICATE_PROJECT = "This project already exists in the client book.";

    private final Project toCreate;

    /**
     * Creates a ProjectCreateCommand to create the specified {@code Project}.
     */
    public ProjectCreateCommand(Project project) {
        requireNonNull(project);
        toCreate = project;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasProject(toCreate)) {
            throw new CommandException(MESSAGE_DUPLICATE_PROJECT);
        }

        model.addProject(toCreate);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toCreate));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProjectCreateCommand otherProjectCreateCommand)) {
            return false;
        }

        return toCreate.equals(otherProjectCreateCommand.toCreate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toCreate", toCreate)
                .toString();
    }
}
