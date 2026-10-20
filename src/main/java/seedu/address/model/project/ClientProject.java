package seedu.address.model.project;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;

/**
 * Represents a client's membership in a project.
 * Guarantees: client and project are present and not null, immutable.
 */
public class ClientProject {

    public static final String DEFAULT_ROLE = "";
    public static final String MESSAGE_ROLE_CONSTRAINTS = "Role must not be blank.";

    private final Person client;
    private final Project project;
    private final String role;

    /**
     * Constructs a {@code ClientProject}.
     *
     * @param client client assigned to project.
     * @param project project assigned to client.
     */
    public ClientProject(Person client, Project project) {
        this(client, project, DEFAULT_ROLE);
    }

    /**
     * Constructs a {@code ClientProject} with a role.
     *
     * @param client client assigned to project.
     * @param project project assigned to client.
     * @param role role held by client in project; empty means no role was specified.
     */
    public ClientProject(Person client, Project project, String role) {
        requireNonNull(client);
        requireNonNull(project);
        requireNonNull(role);
        this.client = client;
        this.project = project;
        this.role = role.trim().replaceAll("\\s+", " ");
    }

    public Person getClient() {
        return client;
    }

    public Project getProject() {
        return project;
    }

    public String getRole() {
        return role;
    }

    /** Returns true if a role is non-blank. */
    public static boolean isValidRole(String role) {
        return role != null && !role.trim().isEmpty();
    }

    /**
     * Returns true if both objects represent same client-project membership.
     */
    public boolean isSameClientProject(ClientProject otherClientProject) {
        return otherClientProject != null
                && client.isSamePerson(otherClientProject.client)
                && project.equals(otherClientProject.project);
    }

    /**
     * Returns a membership with same project and updated client object.
     */
    public ClientProject withClient(Person updatedClient) {
        return new ClientProject(updatedClient, project, role);
    }

    /** Returns a membership with the same client and project and an updated role. */
    public ClientProject withRole(String updatedRole) {
        return new ClientProject(client, project, updatedRole);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClientProject otherClientProject)) {
            return false;
        }

        return isSameClientProject(otherClientProject)
                && role.equals(otherClientProject.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client.getName().fullName.toLowerCase(Locale.ROOT), project, role);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("client", client)
                .add("project", project)
                .add("role", role)
                .toString();
    }
}
