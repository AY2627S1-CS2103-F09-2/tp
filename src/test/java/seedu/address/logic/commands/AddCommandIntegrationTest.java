package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
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
