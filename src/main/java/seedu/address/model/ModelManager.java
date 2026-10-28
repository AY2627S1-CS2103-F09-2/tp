package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * Represents the in-memory model of the client book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ClientBook clientBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final ReadOnlyObjectWrapper<ContactView> contactView =
            new ReadOnlyObjectWrapper<>(ContactView.allContacts());
    private Predicate<Person> personFilter = PREDICATE_SHOW_ALL_PERSONS;

    /**
     * Initializes a ModelManager with the given clientBook and userPrefs.
     */
    public ModelManager(ReadOnlyClientBook clientBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(clientBook, userPrefs);

        logger.fine("Initializing with client book: " + clientBook + " and user prefs " + userPrefs);

        this.clientBook = new ClientBook(clientBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.clientBook.getPersonList());
        // Project memberships can change without a corresponding change to the person list.
        this.clientBook.getClientProjectList().addListener((ListChangeListener<ClientProject>) change ->
                refreshPersonFilter());
    }

    public ModelManager() {
        this(new ClientBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== ClientBook ================================================================================

    @Override
    public void setClientBook(ReadOnlyClientBook clientBook) {
        this.clientBook.resetData(clientBook);
    }

    @Override
    public ReadOnlyClientBook getClientBook() {
        return clientBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return clientBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        clientBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        clientBook.addPerson(person);
        showAllContacts();
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        clientBook.setPerson(target, editedPerson);
    }

    //=========== Projects ==================================================================================

    @Override
    public boolean hasProject(Project project) {
        requireNonNull(project);
        return clientBook.hasProject(project);
    }

    @Override
    public void addProject(Project project) {
        clientBook.addProject(project);
    }

    @Override
    public boolean hasClientProject(ClientProject clientProject) {
        requireNonNull(clientProject);
        return clientBook.hasClientProject(clientProject);
    }

    @Override
    public void addClientProject(ClientProject clientProject) {
        clientBook.addClientProject(clientProject);
    }

    @Override
    public void removeClientProject(ClientProject clientProject) {
        clientBook.removeClientProject(clientProject);
    }

    @Override
    public void setClientProjectRole(ClientProject clientProject, String role) {
        clientBook.setClientProjectRole(clientProject, role);
    }

    @Override
    public void deleteProject(Project project) {
        requireNonNull(project);
        boolean deletingActiveProject = contactView.get().isProject(project);
        clientBook.removeProject(project);
        if (deletingActiveProject) {
            showAllContacts();
        }
    }

    //=========== Filtered Person List Accessors =============================================================

    @Override
    public void showAllContacts() {
        applyPersonFilter(PREDICATE_SHOW_ALL_PERSONS, ContactView.allContacts());
    }

    @Override
    public void showProjectClients(Project project) {
        requireNonNull(project);
        applyPersonFilter(person -> hasClientProject(new ClientProject(person, project)), ContactView.project(project));
    }

    @Override
    public ContactView getContactView() {
        return contactView.get();
    }

    @Override
    public ReadOnlyObjectProperty<ContactView> contactViewProperty() {
        return contactView.getReadOnlyProperty();
    }

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code clientBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        applyPersonFilter(predicate, ContactView.filtered());
    }

    private void applyPersonFilter(Predicate<Person> predicate, ContactView newContactView) {
        requireNonNull(predicate);
        requireNonNull(newContactView);
        personFilter = predicate;
        refreshPersonFilter();
        contactView.set(newContactView);
    }

    private void refreshPersonFilter() {
        // A fresh predicate makes JavaFX re-evaluate the filter even when only memberships changed.
        filteredPersons.setPredicate(personFilter::test);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return clientBook.equals(otherModelManager.clientBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
