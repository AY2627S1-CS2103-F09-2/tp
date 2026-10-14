package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class ReadOnlyClientBookTest {

    @Test
    public void defaultProjectViews_empty() {
        ReadOnlyClientBook view = () -> FXCollections.observableArrayList();

        assertTrue(view.getProjectList().isEmpty());
        assertTrue(view.getClientProjectList().isEmpty());
        assertTrue(view.getProjectsForPerson(ALICE).isEmpty());
        assertThrows(NullPointerException.class, () -> view.getProjectsForPerson(null));
    }

    @Test
    public void getProjectsForPerson_returnsMatchingProjects() {
        Project hackathon = new Project("Hackathon");
        ClientProject membership = new ClientProject(ALICE, hackathon);
        ReadOnlyClientBook view = new ReadOnlyClientBook() {
            @Override
            public ObservableList<Person> getPersonList() {
                return FXCollections.observableArrayList(ALICE);
            }

            @Override
            public ObservableList<ClientProject> getClientProjectList() {
                return FXCollections.observableArrayList(membership);
            }
        };

        assertEquals(List.of(hackathon), view.getProjectsForPerson(ALICE));
    }
}
