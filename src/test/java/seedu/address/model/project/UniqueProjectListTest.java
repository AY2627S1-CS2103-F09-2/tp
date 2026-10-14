package seedu.address.model.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.project.exceptions.DuplicateProjectException;
import seedu.address.model.project.exceptions.ProjectNotFoundException;

public class UniqueProjectListTest {

    @Test
    public void projectListOperations_success() {
        Project hackathon = new Project("Hackathon");
        UniqueProjectList projects = new UniqueProjectList();
        projects.add(hackathon);

        assertTrue(projects.contains(new Project("hackathon")));
        assertEquals(List.of(hackathon), projects.asUnmodifiableObservableList());
        assertEquals(List.of(hackathon), List.copyOf(projects.asUnmodifiableObservableList()));
        assertEquals("[Hackathon]", projects.toString());

        UniqueProjectList copy = new UniqueProjectList();
        copy.setProjects(List.of(hackathon));
        assertEquals(projects, copy);
        assertEquals(projects.hashCode(), copy.hashCode());
        assertEquals(projects, projects);
        assertFalse(projects.equals(null));
        assertFalse(projects.equals("Hackathon"));
    }

    @Test
    public void remove_missingProject_throws() {
        UniqueProjectList projects = new UniqueProjectList();

        assertThrows(ProjectNotFoundException.class, () -> projects.remove(new Project("Hackathon")));
    }

    @Test
    public void addOrSet_duplicateProject_throws() {
        UniqueProjectList projects = new UniqueProjectList();
        projects.add(new Project("Hackathon"));

        assertThrows(DuplicateProjectException.class, () -> projects.add(new Project("hackathon")));
        assertThrows(DuplicateProjectException.class, () ->
                projects.setProjects(List.of(new Project("Hackathon"), new Project("HACKATHON"))));
    }
}
