package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClientBook;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableClientBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableClientBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsClientBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonClientBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonClientBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableClientBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableClientBook.class).get();
        ClientBook clientBookFromFile = dataFromFile.toModelType();
        ClientBook typicalPersonsClientBook = TypicalPersons.getTypicalClientBook();
        assertEquals(clientBookFromFile, typicalPersonsClientBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableClientBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableClientBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableClientBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableClientBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_namesDifferInCaseAndSpacing_throwsIllegalValueException() throws Exception {
        String json = """
                {"persons": [
                  {"name": "John Doe", "phone": "91234567", "email": "john@example.com", "tags": []},
                  {"name": "  jOhN   dOe  ", "phone": "98765432", "email": "other@example.com", "tags": []}
                ]}
                """;
        JsonSerializableClientBook data = JsonUtil.fromJsonString(json, JsonSerializableClientBook.class);
        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_DUPLICATE_PERSON,
                data::toModelType);
    }

    @Test
    public void toModelType_duplicateProjects_throwsIllegalValueException() throws Exception {
        JsonSerializableClientBook data = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon")),
                        new JsonAdaptedProject(new Project("hackathon"))),
                java.util.List.of());

        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_DUPLICATE_PROJECT,
                data::toModelType);
    }

    @Test
    public void toModelType_invalidOrDuplicateClientProjects_throwsIllegalValueException() throws Exception {
        JsonSerializableClientBook invalidData = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                java.util.List.of(new JsonAdaptedClientProject("Unknown", "Hackathon")));
        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_INVALID_CLIENT_PROJECT,
                invalidData::toModelType);

        JsonSerializableClientBook duplicateData = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                java.util.List.of(new JsonAdaptedClientProject(new ClientProject(ALICE, new Project("Hackathon"))),
                        new JsonAdaptedClientProject(new ClientProject(
                                new PersonBuilder(ALICE).withPhone("91234567").build(),
                                new Project("hackathon")))));
        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_DUPLICATE_CLIENT_PROJECT,
                duplicateData::toModelType);
    }

    @Test
    public void toModelType_nullProjectNames_throwsIllegalValueException() throws Exception {
        JsonSerializableClientBook data = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                java.util.List.of(new JsonAdaptedClientProject(null, null)));

        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_INVALID_CLIENT_PROJECT,
                data::toModelType);
    }

}
