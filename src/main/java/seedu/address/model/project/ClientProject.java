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

    private final Person client;
    private final Project project;

    /**
     * Constructs a {@code ClientProject}.
     *
     * @param client client assigned to project.
     * @param project project assigned to client.
     */
    public ClientProject(Person client, Project project) {
        requireNonNull(client);
        requireNonNull(project);
        this.client = client;
        this.project = project;
    }

    public Person getClient() {
        return client;
    }

    public Project getProject() {
        return project;
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
        return new ClientProject(updatedClient, project);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ClientProject otherClientProject)) {
            return false;
        }

        return isSameClientProject(otherClientProject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client.getName().fullName.toLowerCase(Locale.ROOT), project);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("client", client)
                .add("project", project)
                .toString();
    }
}
