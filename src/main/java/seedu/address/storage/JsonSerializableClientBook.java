package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ClientBook;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/**
 * A ClientBook that is serializable to JSON format.
 */
@JsonRootName(value = "clientbook")
class JsonSerializableClientBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_PROJECT = "Projects list contains duplicate project(s).";
    public static final String MESSAGE_DUPLICATE_CLIENT_PROJECT =
            "Client-project list contains duplicate membership(s).";
    public static final String MESSAGE_INVALID_CLIENT_PROJECT =
            "Client-project list contains an unknown client or project.";

    private List<JsonAdaptedPerson> persons = new ArrayList<>();
    private List<JsonAdaptedProject> projects = new ArrayList<>();
    private List<JsonAdaptedClientProject> clientProjects = new ArrayList<>();

    /**
     * Constructs an empty client book for Jackson. Omitted lists keep their empty defaults,
     * while explicit null values are retained for validation in {@link #toModelType()}.
     */
    @JsonCreator
    private JsonSerializableClientBook() {
    }

    /**
     * Constructs a {@code JsonSerializableClientBook} with the given persons, projects and memberships.
     */
    public JsonSerializableClientBook(List<JsonAdaptedPerson> persons, List<JsonAdaptedProject> projects,
            List<JsonAdaptedClientProject> clientProjects) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (projects != null) {
            this.projects.addAll(projects);
        }
        if (clientProjects != null) {
            this.clientProjects.addAll(clientProjects);
        }
    }

    /**
     * Converts a given {@code ReadOnlyClientBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableClientBook}.
     */
    public JsonSerializableClientBook(ReadOnlyClientBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        projects.addAll(source.getProjectList().stream().map(JsonAdaptedProject::new).collect(Collectors.toList()));
        clientProjects.addAll(source.getClientProjectList().stream()
                .map(JsonAdaptedClientProject::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this client book into the model's {@code ClientBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ClientBook toModelType() throws IllegalValueException {
        validateList(persons, "Persons");
        validateList(projects, "Projects");
        validateList(clientProjects, "Client-project");

        ClientBook clientBook = new ClientBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (clientBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            clientBook.addPerson(person);
        }

        for (JsonAdaptedProject jsonAdaptedProject : projects) {
            Project project = jsonAdaptedProject.toModelType();
            if (clientBook.hasProject(project)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PROJECT);
            }
            clientBook.addProject(project);
        }

        for (JsonAdaptedClientProject jsonAdaptedClientProject : clientProjects) {
            Person client = findPerson(clientBook, jsonAdaptedClientProject.getClientName());
            Project project = findProject(clientBook, jsonAdaptedClientProject.getProjectName());
            if (client == null || project == null) {
                throw new IllegalValueException(MESSAGE_INVALID_CLIENT_PROJECT);
            }

            ClientProject clientProject = new ClientProject(client, project,
                    jsonAdaptedClientProject.getRole() == null
                            ? ClientProject.DEFAULT_ROLE : jsonAdaptedClientProject.getRole());
            if (clientBook.hasClientProject(clientProject)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_CLIENT_PROJECT);
            }
            clientBook.addClientProject(clientProject);
        }
        return clientBook;
    }

    private static void validateList(List<?> entries, String name) throws IllegalValueException {
        if (entries == null || entries.contains(null)) {
            throw new IllegalValueException(name + " list must not be null or contain null entries.");
        }
    }

    private static Person findPerson(ClientBook clientBook, String name) {
        if (name == null) {
            return null;
        }
        return clientBook.getPersonList().stream()
                .filter(person -> person.getName().fullName.equalsIgnoreCase(name.trim().replaceAll(" +", " ")))
                .findFirst()
                .orElse(null);
    }

    private static Project findProject(ClientBook clientBook, String name) {
        if (name == null) {
            return null;
        }
        return clientBook.getProjectList().stream()
                .filter(project -> project.getName().equalsIgnoreCase(name.trim().replaceAll(" +", " ")))
                .findFirst()
                .orElse(null);
    }

}
