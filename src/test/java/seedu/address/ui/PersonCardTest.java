package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.project.Project;

public class PersonCardTest {

    @Test
    public void formatProjects_oneProject_showsItsName() {
        assertEquals("Projects: Client Portal", PersonCard.formatProjects(List.of(new Project("Client Portal"))));
    }

    @Test
    public void formatProjects_severalProjects_keepsTheirOrder() {
        List<Project> projects = List.of(new Project("Website Redesign"), new Project("Client Portal"));
        assertEquals("Projects: Website Redesign, Client Portal", PersonCard.formatProjects(projects));
    }
}
