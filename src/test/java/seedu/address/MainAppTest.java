package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonClientBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    private Path clientBookPath;
    private Storage storage;
    private ClientBook validClientBook;
    private String validJson;

    @BeforeEach
    public void setUp() throws Exception {
        clientBookPath = testFolder.resolve("clientbook.json");
        storage = new StorageManager(new JsonClientBookStorage(clientBookPath),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        validClientBook = new ClientBook();
        Project project = new Project("Hackathon");
        validClientBook.addPerson(ALICE);
        validClientBook.addProject(project);
        validClientBook.addClientProject(new ClientProject(ALICE, project));
        storage.saveClientBook(validClientBook);
        validJson = Files.readString(clientBookPath);
    }

    @Test
    public void initModelManager_invalidJson_returnsEmptyClientBook() throws Exception {
        for (String json : new String[]{"", "   ", "null", "{", "[]"}) {
            assertStartsWithEmptyClientBook(json);
        }
    }

    @Test
    public void initModelManager_nullLists_returnsEmptyClientBook() throws Exception {
        for (String field : new String[]{"persons", "projects", "clientProjects"}) {
            ObjectNode json = JsonUtil.fromJsonString(validJson, ObjectNode.class);
            json.putNull(field);
            assertStartsWithEmptyClientBook(json.toString());
        }
    }

    @Test
    public void initModelManager_nullEntries_returnsEmptyClientBook() throws Exception {
        for (String field : new String[]{"persons", "projects", "clientProjects"}) {
            ObjectNode json = JsonUtil.fromJsonString(validJson, ObjectNode.class);
            ArrayNode entries = (ArrayNode) json.get(field);
            entries.addNull();
            assertStartsWithEmptyClientBook(json.toString());
        }
    }

    @Test
    public void initModelManager_invalidListValues_returnsEmptyClientBook() throws Exception {
        for (String field : new String[]{"persons", "projects", "clientProjects"}) {
            ObjectNode json = JsonUtil.fromJsonString(validJson, ObjectNode.class);
            json.put(field, "invalid");
            assertStartsWithEmptyClientBook(json.toString());
        }
    }

    @Test
    public void initModelManager_validFile_returnsSavedClientBook() {
        Model model = new MainApp().initModelManager(storage, new UserPrefs());
        assertEquals(validClientBook, model.getClientBook());
    }

    @Test
    public void initModelManager_missingFile_returnsSampleClientBook() throws Exception {
        Files.delete(clientBookPath);
        Model model = new MainApp().initModelManager(storage, new UserPrefs());
        assertEquals(SampleDataUtil.getSampleClientBook(), model.getClientBook());
    }

    private void assertStartsWithEmptyClientBook(String json) throws Exception {
        Files.writeString(clientBookPath, json);
        Model model = new MainApp().initModelManager(storage, new UserPrefs());
        assertEquals(new ClientBook(), model.getClientBook());
        assertEquals(json, Files.readString(clientBookPath));
    }
}
