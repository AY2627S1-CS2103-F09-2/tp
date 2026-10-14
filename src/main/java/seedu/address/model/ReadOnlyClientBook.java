package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * Unmodifiable view of an client book
 */
public interface ReadOnlyClientBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the projects list.
     */
    default ObservableList<Project> getProjectList() {
        return javafx.collections.FXCollections.unmodifiableObservableList(
                javafx.collections.FXCollections.observableArrayList());
    }

    /**
     * Returns an unmodifiable view of the client-project membership list.
     */
    default ObservableList<ClientProject> getClientProjectList() {
        return javafx.collections.FXCollections.unmodifiableObservableList(
                javafx.collections.FXCollections.observableArrayList());
    }

    /**
     * Returns projects assigned to a client in insertion order.
     */
    default List<Project> getProjectsForPerson(Person person) {
        requireNonNull(person);
        return getClientProjectList().stream()
                .filter(clientProject -> clientProject.getClient().isSamePerson(person))
                .map(ClientProject::getProject)
                .toList();
    }

}
