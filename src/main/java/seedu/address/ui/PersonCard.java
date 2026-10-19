package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.project.Project;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label note;
    @FXML
    private Label projects;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person}, index to display, and the projects the
     * client belongs to, in the order they joined them.
     */
    public PersonCard(Person person, int displayedIndex, List<Project> clientProjects) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        // An unknown address is left out of the card, rather than shown as an empty line.
        address.setVisible(!person.getAddress().isEmpty());
        address.setManaged(!person.getAddress().isEmpty());
        email.setText(person.getEmail().value);
        note.setText("Note: " + person.getNote());
        // A contact without a note gets no note line, rather than an empty one.
        note.setVisible(!person.getNote().isEmpty());
        note.setManaged(!person.getNote().isEmpty());
        projects.setText(formatProjects(clientProjects));
        // A client on no project gets no projects line, rather than an empty one.
        projects.setVisible(!clientProjects.isEmpty());
        projects.setManaged(!clientProjects.isEmpty());
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Returns the projects line shown on a client's card, e.g. "Projects: Client Portal, Website Redesign".
     */
    static String formatProjects(List<Project> clientProjects) {
        return clientProjects.stream()
                .map(Project::getName)
                .collect(Collectors.joining(", ", "Projects: ", ""));
    }
}
