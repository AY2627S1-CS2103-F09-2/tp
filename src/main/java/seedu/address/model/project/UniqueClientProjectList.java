package seedu.address.model.project;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.project.exceptions.ClientProjectNotFoundException;
import seedu.address.model.project.exceptions.DuplicateClientProjectException;

/**
 * A list of client-project memberships that enforces unique client-project pairs.
 */
public class UniqueClientProjectList implements Iterable<ClientProject> {

    private final ObservableList<ClientProject> internalList = FXCollections.observableArrayList();
    private final ObservableList<ClientProject> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if list contains an equivalent membership.
     */
    public boolean contains(ClientProject toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameClientProject);
    }

    /**
     * Adds a client-project membership to list.
     */
    public void add(ClientProject toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateClientProjectException();
        }
        internalList.add(toAdd);
    }

    /**
     * Removes a client-project membership from list.
     */
    public void remove(ClientProject toRemove) {
        requireNonNull(toRemove);
        if (!internalList.removeIf(toRemove::isSameClientProject)) {
            throw new ClientProjectNotFoundException();
        }
    }

    /** Replaces role for an existing client-project membership. */
    public void setRole(ClientProject target, String role) {
        requireAllNonNull(target, role);
        if (!contains(target)) {
            throw new ClientProjectNotFoundException();
        }
        List<ClientProject> replacementMemberships = internalList.stream()
                .map(membership -> membership.isSameClientProject(target)
                        ? membership.withRole(role) : membership)
                .collect(Collectors.toList());
        internalList.setAll(replacementMemberships);
    }

    /**
     * Removes all memberships for a client.
     */
    public void removeForClient(Person client) {
        requireNonNull(client);
        internalList.removeIf(membership -> membership.getClient().isSamePerson(client));
    }

    /**
     * Removes all memberships for a project.
     */
    public void removeForProject(Project project) {
        requireNonNull(project);
        internalList.removeIf(membership -> membership.getProject().equals(project));
    }

    /**
     * Replaces client object in all matching memberships.
     */
    public void replaceClient(Person target, Person replacement) {
        requireAllNonNull(target, replacement);
        List<ClientProject> replacementMemberships = internalList.stream()
                .map(membership -> membership.getClient().isSamePerson(target)
                        ? membership.withClient(replacement) : membership)
                .collect(Collectors.toList());
        if (!membershipsAreUnique(replacementMemberships)) {
            throw new DuplicateClientProjectException();
        }
        internalList.setAll(replacementMemberships);
    }

    /**
     * Replaces list contents with memberships.
     */
    public void setClientProjects(List<ClientProject> clientProjects) {
        requireAllNonNull(clientProjects);
        if (!membershipsAreUnique(clientProjects)) {
            throw new DuplicateClientProjectException();
        }
        internalList.setAll(clientProjects);
    }

    /**
     * Returns an unmodifiable observable view backed by the internal membership list.
     */
    public ObservableList<ClientProject> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<ClientProject> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniqueClientProjectList otherUniqueClientProjectList)) {
            return false;
        }
        return internalList.equals(otherUniqueClientProjectList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean membershipsAreUnique(List<ClientProject> memberships) {
        for (int i = 0; i < memberships.size() - 1; i++) {
            for (int j = i + 1; j < memberships.size(); j++) {
                if (memberships.get(i).isSameClientProject(memberships.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
