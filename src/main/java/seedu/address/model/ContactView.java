package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.model.project.Project;

/** Describes which contact collection is currently displayed. */
public final class ContactView {

    /** Supported contact-list view types. */
    public enum Type {
        ALL_CONTACTS,
        PROJECT,
        FILTERED
    }

    private static final ContactView ALL_CONTACTS = new ContactView(Type.ALL_CONTACTS, null);
    private static final ContactView FILTERED = new ContactView(Type.FILTERED, null);

    private final Type type;
    private final Project project;

    private ContactView(Type type, Project project) {
        this.type = requireNonNull(type);
        this.project = project;
    }

    /** Returns the view representing every contact. */
    public static ContactView allContacts() {
        return ALL_CONTACTS;
    }

    /** Returns the view representing a non-project contact filter. */
    public static ContactView filtered() {
        return FILTERED;
    }

    /** Returns the view representing clients belonging to {@code project}. */
    public static ContactView project(Project project) {
        return new ContactView(Type.PROJECT, requireNonNull(project));
    }

    public Type getType() {
        return type;
    }

    public Optional<Project> getProject() {
        return Optional.ofNullable(project);
    }

    /** Returns true when this view represents {@code project}. */
    public boolean isProject(Project project) {
        return type == Type.PROJECT && this.project.isSameProject(project);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContactView otherView)) {
            return false;
        }
        return type == otherView.type && Objects.equals(project, otherView.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, project);
    }

    @Override
    public String toString() {
        return project == null ? type.toString() : type + ": " + project;
    }
}
