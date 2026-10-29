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
import seedu.address.model.person.Person;
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
                  {"name": "  jOhN   dOe  ", "phone": "91234567", "email": "john@example.com", "tags": []}
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
                                new PersonBuilder(ALICE).withCompany("Acme").build(),
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

    @Test
    public void toModelType_nullRole_usesDefaultRole() throws Exception {
        JsonSerializableClientBook data = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                java.util.List.of(new JsonAdaptedClientProject("Alice Pauline", "Hackathon", null)));

        ClientBook clientBook = data.toModelType();

        assertEquals("", clientBook.getClientProjectList().get(0).getRole());
    }

    @Test
    public void toModelType_legacyNameOnlyMembership_resolvesUniqueClient() throws Exception {
        String json = """
                {"persons": [
                  {"name": "Alice Pauline", "phone": "94351253", "email": "alice@example.com", "tags": []}
                ], "projects": [{"name": "Hackathon"}], "clientProjects": [
                  {"clientName": "  aLiCe   pAuLiNe  ", "projectName": "Hackathon", "role": "developer"}
                ]}
                """;
        ClientBook clientBook = JsonUtil.fromJsonString(json, JsonSerializableClientBook.class).toModelType();

        assertEquals(clientBook.getPersonList().get(0), clientBook.getClientProjectList().get(0).getClient());
        assertEquals("developer", clientBook.getClientProjectList().get(0).getRole());
    }

    @Test
    public void toModelType_ambiguousLegacyClientName_throwsIllegalValueException() {
        Person otherAlice = new PersonBuilder(ALICE).withPhone("91234567").build();
        JsonSerializableClientBook data = new JsonSerializableClientBook(
                java.util.List.of(new JsonAdaptedPerson(ALICE), new JsonAdaptedPerson(otherAlice)),
                java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                java.util.List.of(new JsonAdaptedClientProject("Alice Pauline", "Hackathon")));

        assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_AMBIGUOUS_CLIENT_PROJECT,
                data::toModelType);
    }

    @Test
    public void toModelType_incompleteOrUnknownClientIdentity_throwsIllegalValueException() {
        for (JsonAdaptedClientProject membership : java.util.List.of(
                new JsonAdaptedClientProject("Alice Pauline", null, "alice@example.com", "Hackathon", ""),
                new JsonAdaptedClientProject("Alice Pauline", "94351253", null, "Hackathon", ""),
                new JsonAdaptedClientProject("Alice Pauline", "91234567", "alice@example.com", "Hackathon", ""),
                new JsonAdaptedClientProject("Alice Pauline", "94351253", "other@example.com", "Hackathon", ""))) {
            JsonSerializableClientBook data = new JsonSerializableClientBook(
                    java.util.List.of(new JsonAdaptedPerson(ALICE)),
                    java.util.List.of(new JsonAdaptedProject(new Project("Hackathon"))),
                    java.util.List.of(membership));

            assertThrows(IllegalValueException.class, JsonSerializableClientBook.MESSAGE_INVALID_CLIENT_PROJECT,
                    data::toModelType);
        }
    }

}
