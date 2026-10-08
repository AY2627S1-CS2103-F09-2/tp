package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyClientBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_distinctContactDetails_addSuccessfulWithoutWarning() throws Exception {
        assertAddWithWarning(List.of(ALICE), new PersonBuilder().build(), "");
    }

    @Test
    public void execute_sharedEmail_addSuccessfulWithEmailWarning() throws Exception {
        Person person = new PersonBuilder().withEmail(ALICE.getEmail().value).build();
        assertAddWithWarning(List.of(ALICE), person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail()));
    }

    @Test
    public void execute_sharedPhone_addSuccessfulWithPhoneWarning() throws Exception {
        Person person = new PersonBuilder().withPhone(ALICE.getPhone().value).build();
        assertAddWithWarning(List.of(ALICE), person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    @Test
    public void execute_sharedEmailAndPhone_addSuccessfulWithBothWarnings() throws Exception {
        Person person = new PersonBuilder(ALICE).withName("New Contact").build();
        assertAddWithWarning(List.of(ALICE), person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    @Test
    public void execute_emailAndPhoneBelongToDifferentContacts_addSuccessfulWithBothWarnings() throws Exception {
        Person emailOwner = new PersonBuilder().withName("Email Owner").withPhone("11111111").build();
        Person phoneOwner = new PersonBuilder().withName("Phone Owner").withEmail("other@example.com").build();
        Person person = new PersonBuilder().build();
        assertAddWithWarning(List.of(emailOwner, phoneOwner), person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    @Test
    public void execute_multipleContactsShareDetails_warnsOncePerField() throws Exception {
        Person otherOwner = new PersonBuilder(ALICE).withName("Other Owner").build();
        Person person = new PersonBuilder(ALICE).withName("New Contact").build();
        assertAddWithWarning(List.of(ALICE, otherOwner), person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    /**
     * Verifies both feedback and insertion when existing contacts may share details with the new person.
     */
    private void assertAddWithWarning(List<Person> existingPersons, Person person, String warning) throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        modelStub.personsAdded.addAll(existingPersons);

        CommandResult commandResult = new AddCommand(person).execute(modelStub);

        assertEquals(new CommandResult(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(person)) + warning),
                commandResult);
        List<Person> expectedPersons = new ArrayList<>(existingPersons);
        expectedPersons.add(person);
        assertEquals(expectedPersons, modelStub.personsAdded);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_throwsCommandException() {
        Model model = new ModelManager(new ClientBook(), new UserPrefs());
        model.addPerson(ALICE);
        for (String name : List.of("alice pauline", "Alice   Pauline", "  aLiCe   pAuLiNe  ")) {
            Person duplicateAlice = new PersonBuilder().withName(name).build();
            assertCommandFailure(new AddCommand(duplicateAlice), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setClientBook(ReadOnlyClientBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyClientBook getClientBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }
    }

    /**
     * A Model stub that always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyClientBook getClientBook() {
            ClientBook clientBook = new ClientBook();
            for (Person person : personsAdded) {
                clientBook.addPerson(person);
            }
            return clientBook;
        }
    }

}
