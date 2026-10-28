package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ContactView;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

/** Sidebar for switching between all contacts and project member lists. */
public class ProjectSidebar extends UiPart<Region> {
    private static final String FXML = "ProjectSidebar.fxml";
    private static final String SELECTED_STYLE_CLASS = "sidebar-selected";

    private final Logger logger = LogsCenter.getLogger(getClass());
    private final ReadOnlyClientBook clientBook;
    private final ReadOnlyObjectProperty<ContactView> contactView;
    private final CommandBox.CommandExecutor commandExecutor;
    private final Runnable returnFocusToCommandBox;

    @FXML
    private Button contactsButton;

    @FXML
    private VBox projectList;

    /**
     * Creates a project navigation sidebar backed by the client book's live project list.
     */
    public ProjectSidebar(ReadOnlyClientBook clientBook, ReadOnlyObjectProperty<ContactView> contactView,
            CommandBox.CommandExecutor commandExecutor, Runnable returnFocusToCommandBox) {
        super(FXML);
        this.clientBook = clientBook;
        this.contactView = contactView;
        this.commandExecutor = commandExecutor;
        this.returnFocusToCommandBox = returnFocusToCommandBox;

        refreshContactsLabel();
        refreshProjectButtons();
        refreshSelectionStyles();

        clientBook.getPersonList().addListener((ListChangeListener<Person>) change -> refreshContactsLabel());
        clientBook.getProjectList().addListener((ListChangeListener<Project>) change -> refreshProjectButtons());
        clientBook.getClientProjectList().addListener(
                (ListChangeListener<ClientProject>) change -> refreshProjectButtons());
        contactView.addListener((observable, oldView, newView) -> refreshSelectionStyles());
    }

    @FXML
    private void handleContactsSelected() {
        executeNavigationCommand("list");
    }

    private void handleProjectSelected(Project project) {
        executeNavigationCommand("project view " + project.getName());
    }

    private void executeNavigationCommand(String command) {
        try {
            commandExecutor.execute(command);
        } catch (CommandException | ParseException e) {
            logger.warning("Sidebar navigation failed: " + e.getMessage());
        } finally {
            returnFocusToCommandBox.run();
        }
    }

    private void refreshContactsLabel() {
        contactsButton.setText("Contacts (" + clientBook.getPersonList().size() + ")");
    }

    private void refreshProjectButtons() {
        projectList.getChildren().clear();
        for (Project project : clientBook.getProjectList()) {
            Button projectButton = new Button(formatProjectLabel(project));
            projectButton.setMaxWidth(Double.MAX_VALUE);
            projectButton.setAlignment(Pos.CENTER_LEFT);
            projectButton.getStyleClass().add("sidebar-project-button");
            projectButton.setOnAction(event -> handleProjectSelected(project));
            projectButton.getProperties().put(Project.class, project);
            projectList.getChildren().add(projectButton);
        }
        refreshSelectionStyles();
    }

    private String formatProjectLabel(Project project) {
        long clientCount = clientBook.getClientProjectList().stream()
                .filter(clientProject -> clientProject.getProject().isSameProject(project))
                .count();
        return project.getName() + " (" + clientCount + ")";
    }

    private void refreshSelectionStyles() {
        Project selectedProject = contactView.get().getProject().orElse(null);
        setSelected(contactsButton, selectedProject == null);
        for (Node node : projectList.getChildren()) {
            Button projectButton = (Button) node;
            Project project = (Project) projectButton.getProperties().get(Project.class);
            setSelected(projectButton, selectedProject != null && selectedProject.isSameProject(project));
        }
    }

    private static void setSelected(Labeled control, boolean selected) {
        if (selected && !control.getStyleClass().contains(SELECTED_STYLE_CLASS)) {
            control.getStyleClass().add(SELECTED_STYLE_CLASS);
        } else if (!selected) {
            control.getStyleClass().remove(SELECTED_STYLE_CLASS);
        }
    }
}
