package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
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
    private final CommandBox.CommandExecutor commandExecutor;

    private boolean showingAllContacts = true;
    private Project selectedProject;

    @FXML
    private Button contactsButton;

    @FXML
    private Button projectsButton;

    @FXML
    private VBox projectList;

    /**
     * Creates a project navigation sidebar backed by the client book's live project list.
     */
    public ProjectSidebar(ReadOnlyClientBook clientBook, CommandBox.CommandExecutor commandExecutor) {
        super(FXML);
        this.clientBook = clientBook;
        this.commandExecutor = commandExecutor;

        refreshContactsLabel();
        refreshProjectButtons();
        selectContacts();

        clientBook.getPersonList().addListener((ListChangeListener<Person>) change -> refreshContactsLabel());
        clientBook.getProjectList().addListener((ListChangeListener<Project>) change -> refreshProjectButtons());
        clientBook.getClientProjectList().addListener(
                (ListChangeListener<ClientProject>) change -> refreshProjectButtons());
    }

    @FXML
    private void handleContactsSelected() {
        if (executeNavigationCommand("list")) {
            selectContacts();
        }
    }

    @FXML
    private void handleProjectsSelected() {
        boolean showProjects = !projectList.isVisible();
        projectList.setVisible(showProjects);
        projectList.setManaged(showProjects);
        showingAllContacts = false;
        refreshSelectionStyles();
    }

    private void handleProjectSelected(Project project) {
        if (executeNavigationCommand("project view " + project.getName())) {
            showingAllContacts = false;
            selectedProject = project;
            refreshSelectionStyles();
        }
    }

    private boolean executeNavigationCommand(String command) {
        try {
            commandExecutor.execute(command);
            return true;
        } catch (CommandException | ParseException e) {
            logger.warning("Sidebar navigation failed: " + e.getMessage());
            return false;
        }
    }

    private void selectContacts() {
        showingAllContacts = true;
        selectedProject = null;
        refreshSelectionStyles();
    }

    private void refreshContactsLabel() {
        contactsButton.setText("Contacts (" + clientBook.getPersonList().size() + ")");
    }

    private void refreshProjectButtons() {
        if (selectedProject != null && clientBook.getProjectList().stream()
                .noneMatch(selectedProject::isSameProject)) {
            selectedProject = null;
        }

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
        setSelected(contactsButton, showingAllContacts);
        setSelected(projectsButton, !showingAllContacts);
        for (javafx.scene.Node node : projectList.getChildren()) {
            Button projectButton = (Button) node;
            Project project = (Project) projectButton.getProperties().get(Project.class);
            setSelected(projectButton, selectedProject != null && selectedProject.isSameProject(project));
        }
    }

    private static void setSelected(Button button, boolean selected) {
        if (selected && !button.getStyleClass().contains(SELECTED_STYLE_CLASS)) {
            button.getStyleClass().add(SELECTED_STYLE_CLASS);
        } else if (!selected) {
            button.getStyleClass().remove(SELECTED_STYLE_CLASS);
        }
    }
}
