package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ClientBook;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class JsonProjectStorageTest {

    @TempDir
    public Path testFolder;

    @Test
    public void readAndSave_projectsAndMemberships_success() throws Exception {
        ClientBook original = new ClientBook();
        Project hackathon = new Project("Hackathon");
        original.addPerson(ALICE);
        original.addProject(hackathon);
        original.addClientProject(new ClientProject(ALICE, hackathon, "developer"));

        Path filePath = testFolder.resolve("ClientBook.json");
        JsonClientBookStorage storage = new JsonClientBookStorage(filePath);
        storage.saveClientBook(original);

        assertEquals(original, new ClientBook(storage.readClientBook().get()));
    }
}
