package seedu.address.model.project;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Represents a project in the client book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidProjectName(String)}.
 */
public class Project {

    public static final String MESSAGE_CONSTRAINTS =
            "Project names must not be blank or contain the role prefix r/.";

    private static final Pattern ROLE_PREFIX = Pattern.compile("\\s+r/");

    public final String projectName;

    /**
     * Constructs a {@code Project}.
     *
     * @param name project name.
     */
    public Project(String name) {
        requireNonNull(name);
        checkArgument(isValidProjectName(name), MESSAGE_CONSTRAINTS);
        this.projectName = name.trim().replaceAll(" +", " ");
    }

    /**
     * Returns true if a given string is a valid project name.
     */
    public static boolean isValidProjectName(String test) {
        return test != null && !test.trim().isEmpty() && !ROLE_PREFIX.matcher(test).find();
    }

    public String getName() {
        return projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    /**
     * Returns true if both projects represent the same project, ignoring case.
     */
    public boolean isSameProject(Project otherProject) {
        return otherProject != null && projectName.equalsIgnoreCase(otherProject.projectName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Project otherProject)) {
            return false;
        }

        return projectName.equalsIgnoreCase(otherProject.projectName);
    }

    @Override
    public int hashCode() {
        return projectName.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return projectName;
    }
}
