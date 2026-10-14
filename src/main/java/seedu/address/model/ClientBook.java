package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.model.project.UniqueClientProjectList;
import seedu.address.model.project.UniqueProjectList;
import seedu.address.model.project.exceptions.ProjectNotFoundException;

/**
 * Wraps all data at the client-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class ClientBook implements ReadOnlyClientBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueProjectList projects = new UniqueProjectList();
    private final UniqueClientProjectList clientProjects = new UniqueClientProjectList();

    public ClientBook() {}

    /**
     * Creates an ClientBook using the Persons in the {@code toBeCopied}
     */
    public ClientBook(ReadOnlyClientBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces project data with {@code projects}.
     */
    public void setProjects(List<Project> projects) {
        this.projects.setProjects(projects);
    }

    /**
     * Replaces client-project membership data with {@code clientProjects}.
     * Every membership must refer to an existing client and project.
     */
    public void setClientProjects(List<ClientProject> clientProjects) {
        requireNonNull(clientProjects);
        for (ClientProject clientProject : clientProjects) {
            requireNonNull(clientProject);
            if (!hasPerson(clientProject.getClient())) {
                throw new PersonNotFoundException();
            }
            if (!hasProject(clientProject.getProject())) {
                throw new ProjectNotFoundException();
            }
        }
        this.clientProjects.setClientProjects(clientProjects);
    }

    /**
     * Resets the existing data of this {@code ClientBook} with {@code newData}.
     */
    public void resetData(ReadOnlyClientBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setProjects(newData.getProjectList());
        setClientProjects(newData.getClientProjectList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the client book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the client book.
     * The person must not already exist in the client book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the client book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the client book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        clientProjects.replaceClient(target, editedPerson);
    }

    //// project-level operations

    /**
     * Returns true if a project with the same name exists in the client book.
     */
    public boolean hasProject(Project project) {
        requireNonNull(project);
        return projects.contains(project);
    }

    /**
     * Adds a project to the client book.
     */
    public void addProject(Project project) {
        projects.add(project);
    }

    /**
     * Removes a project and all client memberships under it.
     */
    public void removeProject(Project project) {
        projects.remove(project);
        clientProjects.removeForProject(project);
    }

    /**
     * Returns true if a client belongs to a project.
     */
    public boolean hasClientProject(ClientProject clientProject) {
        requireNonNull(clientProject);
        return clientProjects.contains(clientProject);
    }

    /**
     * Adds a client-project membership.
     * Both client and project must already exist in the client book.
     */
    public void addClientProject(ClientProject clientProject) {
        requireNonNull(clientProject);
        if (!hasPerson(clientProject.getClient())) {
            throw new PersonNotFoundException();
        }
        if (!hasProject(clientProject.getProject())) {
            throw new ProjectNotFoundException();
        }
        clientProjects.add(clientProject);
    }

    /**
     * Removes a client-project membership.
     */
    public void removeClientProject(ClientProject clientProject) {
        clientProjects.remove(clientProject);
    }

    /**
     * Returns projects assigned to a client in insertion order.
     */
    public List<Project> getProjectsForPerson(Person person) {
        requireNonNull(person);
        List<Project> result = new ArrayList<>();
        for (ClientProject clientProject : clientProjects) {
            if (clientProject.getClient().isSamePerson(person)) {
                result.add(clientProject.getProject());
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Removes {@code key} from this {@code ClientBook}.
     * {@code key} must exist in the client book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        clientProjects.removeForClient(key);
    }

    //// util methods

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("persons", persons);
        if (!projects.asUnmodifiableObservableList().isEmpty()
                || !clientProjects.asUnmodifiableObservableList().isEmpty()) {
            builder.add("projects", projects)
                    .add("clientProjects", clientProjects);
        }
        return builder.toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Project> getProjectList() {
        return projects.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<ClientProject> getClientProjectList() {
        return clientProjects.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClientBook otherClientBook)) {
            return false;
        }

        return persons.equals(otherClientBook.persons)
                && projects.equals(otherClientBook.projects)
                && clientProjects.equals(otherClientBook.clientProjects);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(persons, projects, clientProjects);
    }
}
