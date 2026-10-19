package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_statusFilter_showsOnlyMatchingContacts() {
        setStatus(model, INDEX_FIRST_PERSON, Status.ACTIVE);
        setStatus(model, INDEX_THIRD_PERSON, Status.ACTIVE);
        setStatus(model, INDEX_SECOND_PERSON, Status.DONE);
        expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.getStatus() == Status.ACTIVE);

        assertCommandSuccess(new ListCommand(Status.ACTIVE), model,
                String.format(ListCommand.MESSAGE_FILTERED_SUCCESS, 2, "contacts", Status.ACTIVE), expectedModel);
    }

    @Test
    public void execute_statusFilterOneMatch_usesSingular() {
        setStatus(model, INDEX_SECOND_PERSON, Status.ON_HOLD);
        expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.getStatus() == Status.ON_HOLD);

        assertCommandSuccess(new ListCommand(Status.ON_HOLD), model,
                String.format(ListCommand.MESSAGE_FILTERED_SUCCESS, 1, "contact", Status.ON_HOLD), expectedModel);
    }

    @Test
    public void execute_statusFilterNoMatch_showsHelpfulMessage() {
        expectedModel.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new ListCommand(Status.LEAD), model,
                String.format(ListCommand.MESSAGE_FILTERED_EMPTY, Status.LEAD), expectedModel);
    }

    @Test
    public void equals() {
        ListCommand listActive = new ListCommand(Status.ACTIVE);
        assertTrue(listActive.equals(listActive));
        assertTrue(new ListCommand().equals(new ListCommand()));
        assertTrue(new ListCommand(Status.DONE).equals(new ListCommand(Status.DONE)));
        assertFalse(new ListCommand().equals(new ListCommand(Status.DONE)));
        assertFalse(new ListCommand(Status.DONE).equals(new ListCommand(Status.LEAD)));
        assertFalse(new ListCommand().equals(null));
    }

    /**
     * Gives the contact at {@code index} the given status in {@code model}.
     */
    private static void setStatus(Model model, Index index, Status status) {
        Person target = model.getFilteredPersonList().get(index.getZeroBased());
        model.setPerson(target, target.withStatus(status));
    }

    @Test
    public void execute_emptyList_showsHelpfulMessage() {
        Model emptyModel = new ModelManager(new ClientBook(), new UserPrefs());
        Model expectedEmptyModel = new ModelManager(new ClientBook(), new UserPrefs());
        assertCommandSuccess(new ListCommand(), emptyModel, ListCommand.MESSAGE_EMPTY, expectedEmptyModel);
    }
}
