package seedu.address.model;

import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces client book data with the data in {@code clientBook}.
     */
    void setClientBook(ReadOnlyClientBook clientBook);

    /** Returns the ClientBook */
    ReadOnlyClientBook getClientBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the client book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the client book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the client book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the client book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the client book.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Returns true if a project with the same name exists in the client book, ignoring case.
     */
    boolean hasProject(Project project);

    /**
     * Adds the given project.
     * {@code project} must not already exist in the client book.
     */
    void addProject(Project project);

    /**
     * Returns true if the client already belongs to the project in {@code clientProject}.
     */
    boolean hasClientProject(ClientProject clientProject);

    /**
     * Adds a client-project membership.
     * Both client and project must already exist, and the membership must not already exist.
     */
    void addClientProject(ClientProject clientProject);

    /**
     * Removes the given client-project membership while retaining the client and project.
     * The membership must already exist.
     */
    void removeClientProject(ClientProject clientProject);

    /** Updates role for an existing client-project membership. */
    void setClientProjectRole(ClientProject clientProject, String role);

    /**
     * Deletes the given project and every client-project membership under it, keeping the clients.
     * The project must exist in the client book.
     */
    void deleteProject(Project project);

    /** Shows every contact and records that the all-contacts view is active. */
    void showAllContacts();

    /** Shows clients belonging to {@code project} and records that project view as active. */
    void showProjectClients(Project project);

    /** Returns the current contact-list view. */
    ContactView getContactView();

    /** Returns an observable, read-only current contact-list view. */
    ReadOnlyObjectProperty<ContactView> contactViewProperty();

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
