package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.model.project.ClientProject;

/**
 * Jackson-friendly version of {@link ClientProject}.
 */
class JsonAdaptedClientProject {

    private final String clientName;
    private final String projectName;

    /**
     * Constructs a {@code JsonAdaptedClientProject} with client and project names.
     */
    @JsonCreator
    public JsonAdaptedClientProject(@JsonProperty("clientName") String clientName,
            @JsonProperty("projectName") String projectName) {
        this.clientName = clientName;
        this.projectName = projectName;
    }

    /**
     * Converts a given {@code ClientProject} into this class for Jackson use.
     */
    public JsonAdaptedClientProject(ClientProject source) {
        clientName = source.getClient().getName().fullName;
        projectName = source.getProject().getName();
    }

    public String getClientName() {
        return clientName;
    }

    public String getProjectName() {
        return projectName;
    }
}
