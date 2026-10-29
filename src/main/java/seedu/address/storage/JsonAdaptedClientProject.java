package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.model.project.ClientProject;

/**
 * Jackson-friendly version of {@link ClientProject}.
 */
class JsonAdaptedClientProject {

    private final String clientName;
    private final String clientPhone;
    private final String clientEmail;
    private final String projectName;
    private final String role;

    /**
     * Constructs a {@code JsonAdaptedClientProject} with client identity and project name.
     * Older files may omit both client phone and email when the name uniquely identifies a client.
     */
    @JsonCreator
    public JsonAdaptedClientProject(@JsonProperty("clientName") String clientName,
            @JsonProperty("clientPhone") String clientPhone,
            @JsonProperty("clientEmail") String clientEmail,
            @JsonProperty("projectName") String projectName,
            @JsonProperty("role") String role) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.projectName = projectName;
        this.role = role;
    }

    /** Constructs an adapted membership using the legacy name-only client reference. */
    public JsonAdaptedClientProject(String clientName, String projectName, String role) {
        this(clientName, null, null, projectName, role);
    }

    /** Constructs an adapted membership without a role. */
    public JsonAdaptedClientProject(String clientName, String projectName) {
        this(clientName, projectName, ClientProject.DEFAULT_ROLE);
    }

    /**
     * Converts a given {@code ClientProject} into this class for Jackson use.
     */
    public JsonAdaptedClientProject(ClientProject source) {
        clientName = source.getClient().getName().fullName;
        clientPhone = source.getClient().getPhone().value;
        clientEmail = source.getClient().getEmail().value;
        projectName = source.getProject().getName();
        role = source.getRole();
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getRole() {
        return role;
    }
}
