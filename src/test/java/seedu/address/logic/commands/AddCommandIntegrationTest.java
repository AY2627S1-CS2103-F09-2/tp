package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddCommandParser;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_twoJohnTansWithDifferentDetails_successWithNameWarning() throws Exception {
        model = new ModelManager(new ClientBook(), new UserPrefs());
        AddCommandParser parser = new AddCommandParser();
        parser.parse(" n/John Tan p/91234567 e/john@test1.com c/test1").execute(model);
        Person secondJohn = new PersonBuilder().withName("John Tan").withPhone("98765432")
                .withEmail("john@test2.com").withCompany("test2").withoutAddress().withTags().build();
        AddCommand secondCommand = parser.parse(" n/John Tan p/98765432 e/john@test2.com c/test2");
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addPerson(secondJohn);

        assertCommandSuccess(secondCommand, model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(secondJohn))
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_NAME_WARNING, secondJohn.getName()), expectedModel);
        assertCommandFailure(secondCommand, model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePhoneWithDifferentSpacing_failure() throws Exception {
        AddCommandParser parser = new AddCommandParser();
        for (String phone : new String[]{"91234567", "9123 4567", "91 23 45 67", "9123   4567"}) {
            model = new ModelManager(new ClientBook(), new UserPrefs());
            parser.parse(" n/John Tan p/" + phone + " e/john@test1.com c/test1").execute(model);
            model.updateFilteredPersonList(person -> false);

            assertCommandFailure(parser.parse(" n/John Tan p/9123 4567 e/john@test1.com c/test1"),
                    model, AddCommand.MESSAGE_DUPLICATE_PERSON);
            assertCommandFailure(parser.parse(" n/John Tan p/91234567 e/john@test1.com c/test1"),
                    model, AddCommand.MESSAGE_DUPLICATE_PERSON);
        }
    }

    @Test
    public void execute_sharedPhoneWithDifferentSpacing_successWithPhoneWarning() {
        Person person = new PersonBuilder().withPhone("9435 1253").build();
        assertAddWithWarning(person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    @Test
    public void execute_sameNameHiddenByFilter_successWithNameWarning() {
        model.updateFilteredPersonList(person -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());
        Person person = new PersonBuilder().withName(ALICE.getName().fullName).build();

        assertAddWithWarning(person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_NAME_WARNING, person.getName()));
    }

    @Test
    public void execute_sharedEmail_successWithWarning() {
        Person person = new PersonBuilder().withEmail(ALICE.getEmail().value).build();
        assertAddWithWarning(person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail()));
    }

    @Test
    public void execute_sharedPhone_successWithWarning() {
        Person person = new PersonBuilder().withPhone(ALICE.getPhone().value).build();
        assertAddWithWarning(person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    @Test
    public void execute_contactWithSharedDetailsHiddenByFilter_successWithBothWarnings() {
        model.updateFilteredPersonList(person -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());
        Person person = new PersonBuilder(ALICE).withName("New Contact").build();

        assertAddWithWarning(person,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, person.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, person.getPhone()));
    }

    /**
     * Verifies that warnings accompany a successful addition and that the model shows all contacts afterwards.
     */
    private void assertAddWithWarning(Person person, String warning) {
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.addPerson(person);

        assertCommandSuccess(new AddCommand(person), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(person)) + warning, expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getClientBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
