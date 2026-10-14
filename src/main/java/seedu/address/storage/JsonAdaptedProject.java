package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.project.Project;

/**
 * Jackson-friendly version of {@link Project}.
 */
class JsonAdaptedProject {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Project's %s field is missing!";

    private final String name;

    /**
     * Constructs a {@code JsonAdaptedProject} with the given name.
     */
    @JsonCreator
    public JsonAdaptedProject(@JsonProperty("name") String name) {
        this.name = name;
    }

    /**
     * Converts a given {@code Project} into this class for Jackson use.
     */
    public JsonAdaptedProject(Project source) {
        name = source.getName();
    }

    /**
     * Converts this Jackson-friendly adapted project into a {@code Project}.
     *
     * @throws IllegalValueException if project data violates model constraints.
     */
    public Project toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!Project.isValidProjectName(name)) {
            throw new IllegalValueException(Project.MESSAGE_CONSTRAINTS);
        }
        return new Project(name);
    }
}
