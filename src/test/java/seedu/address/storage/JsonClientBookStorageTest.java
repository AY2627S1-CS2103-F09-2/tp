package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClientBook;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.person.Person;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.testutil.PersonBuilder;

public class JsonClientBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonClientBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void saveAndRead_sameNameClients_preservesSeparateProjectMemberships() throws Exception {
        Person firstJohn = new PersonBuilder().withName("John Tan").withPhone("91234567")
                .withEmail("john@test1.com").withCompany("test1").build();
        Person secondJohn = new PersonBuilder().withName("John Tan").withPhone("98765432")
                .withEmail("john@test2.com").withCompany("test2").build();
        Project project = new Project("Client Portal");
        ClientBook clientBook = new ClientBook();
        clientBook.addPerson(firstJohn);
        clientBook.addPerson(secondJohn);
        clientBook.addProject(project);
        clientBook.addClientProject(new ClientProject(secondJohn, project, "developer"));
        clientBook.addClientProject(new ClientProject(firstJohn, project, "tester"));
        Path filePath = testFolder.resolve("sameNameClients.json");
        JsonClientBookStorage storage = new JsonClientBookStorage(filePath);

        storage.saveClientBook(clientBook);
        ReadOnlyClientBook loaded = storage.readClientBook().orElseThrow();

        assertEquals(clientBook, loaded);
        assertEquals(secondJohn, loaded.getClientProjectList().get(0).getClient());
        assertEquals(firstJohn, loaded.getClientProjectList().get(1).getClient());
    }

    @Test
    public void readClientBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readClientBook(null));
    }

    private java.util.Optional<ReadOnlyClientBook> readClientBook(String filePath) throws Exception {
        return new JsonClientBookStorage(Paths.get(filePath)).readClientBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readClientBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readClientBook("notJsonFormatClientBook.json"));
    }

    @Test
    public void readClientBook_invalidPersonClientBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readClientBook("invalidPersonClientBook.json"));
    }

    @Test
    public void readClientBook_invalidAndValidPersonClientBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readClientBook("invalidAndValidPersonClientBook.json"));
    }

    @Test
    public void readClientBook_emptyOrNullFile_throwsDataLoadingException() throws Exception {
        for (String json : new String[]{"", "   ", "null"}) {
            assertInvalidClientBook(json);
        }
    }

    @Test
    public void readClientBook_nullLists_throwsDataLoadingException() throws Exception {
        for (String field : new String[]{"persons", "projects", "clientProjects"}) {
            ObjectNode json = getValidClientBookJson();
            json.putNull(field);
            assertInvalidClientBook(json.toString());
        }
    }

    @Test
    public void readClientBook_nullEntries_throwsDataLoadingException() throws Exception {
        for (String field : new String[]{"persons", "projects", "clientProjects"}) {
            ObjectNode json = getValidClientBookJson();
            ArrayNode entries = (ArrayNode) json.get(field);
            entries.addNull();
            assertInvalidClientBook(json.toString());
        }
    }

    @Test
    public void readClientBook_nullTag_throwsDataLoadingException() throws Exception {
        ObjectNode json = getValidClientBookJson();
        ArrayNode tags = (ArrayNode) json.get("persons").get(0).get("tags");
        tags.addNull();
        assertInvalidClientBook(json.toString());
    }

    @Test
    public void readClientBook_omittedLists_returnsEmptyClientBook() throws Exception {
        Path filePath = testFolder.resolve("ClientBook.json");
        Files.writeString(filePath, "{}");
        assertEquals(new ClientBook(), new JsonClientBookStorage(filePath).readClientBook().get());
    }

    private ObjectNode getValidClientBookJson() throws Exception {
        ClientBook clientBook = new ClientBook();
        Project project = new Project("Hackathon");
        clientBook.addPerson(ALICE);
        clientBook.addProject(project);
        clientBook.addClientProject(new ClientProject(ALICE, project));
        return JsonUtil.fromJsonString(JsonUtil.toJsonString(new JsonSerializableClientBook(clientBook)),
                ObjectNode.class);
    }

    private void assertInvalidClientBook(String json) throws Exception {
        Path filePath = testFolder.resolve("InvalidClientBook.json");
        Files.writeString(filePath, json);
        assertThrows(DataLoadingException.class, () -> new JsonClientBookStorage(filePath).readClientBook());
        assertEquals(json, Files.readString(filePath));
    }

    @Test
    public void readAndSaveClientBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempClientBook.json");
        ClientBook original = getTypicalClientBook();
        JsonClientBookStorage jsonClientBookStorage = new JsonClientBookStorage(filePath);

        // Save in new file and read back
        jsonClientBookStorage.saveClientBook(original, filePath);
        ReadOnlyClientBook readBack = jsonClientBookStorage.readClientBook(filePath).get();
        assertEquals(original, new ClientBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonClientBookStorage.saveClientBook(original, filePath);
        readBack = jsonClientBookStorage.readClientBook(filePath).get();
        assertEquals(original, new ClientBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonClientBookStorage.saveClientBook(original); // file path not specified
        readBack = jsonClientBookStorage.readClientBook().get(); // file path not specified
        assertEquals(original, new ClientBook(readBack));

    }

    @Test
    public void saveClientBook_nullClientBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveClientBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code clientBook} at the specified {@code filePath}.
     */
    private void saveClientBook(ReadOnlyClientBook clientBook, String filePath) {
        try {
            new JsonClientBookStorage(Paths.get(filePath))
                    .saveClientBook(clientBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveClientBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveClientBook(new ClientBook(), null));
    }
}
