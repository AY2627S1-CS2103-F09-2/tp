package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.ClientBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;

public class StatusCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StatusCommand(null, Status.ACTIVE));
        assertThrows(NullPointerException.class, () -> new StatusCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_newStatus_setsStatus() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(target, target.withStatus(Status.ON_HOLD));

        assertCommandSuccess(new StatusCommand(INDEX_FIRST_PERSON, Status.ON_HOLD), model,
                String.format(StatusCommand.MESSAGE_SET_SUCCESS, target.getName(), Status.ON_HOLD), expectedModel);
    }

    @Test
    public void execute_none_clearsStatus() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(target, target.withStatus(Status.ACTIVE));
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        Person active = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        expectedModel.setPerson(active, active.withStatus(Status.NONE));

        assertCommandSuccess(new StatusCommand(INDEX_FIRST_PERSON, Status.NONE), model,
                String.format(StatusCommand.MESSAGE_CLEAR_SUCCESS, target.getName()), expectedModel);
    }

    @Test
    public void execute_sameStatus_reportsNothingChanged() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(target, target.withStatus(Status.DONE));
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());

        assertCommandSuccess(new StatusCommand(INDEX_FIRST_PERSON, Status.DONE), model,
                String.format(StatusCommand.MESSAGE_UNCHANGED, target.getName(), Status.DONE), expectedModel);
    }

    @Test
    public void execute_clearWithoutStatus_reportsNothingToClear() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());

        assertCommandSuccess(new StatusCommand(INDEX_FIRST_PERSON, Status.NONE), model,
                String.format(StatusCommand.MESSAGE_NOTHING_TO_CLEAR, target.getName()), expectedModel);
    }

    @Test
    public void execute_filteredList_setsStatusOfDisplayedContact() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person target = model.getFilteredPersonList().get(0);
        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(target, target.withStatus(Status.LEAD));

        assertCommandSuccess(new StatusCommand(INDEX_FIRST_PERSON, Status.LEAD), model,
                String.format(StatusCommand.MESSAGE_SET_SUCCESS, target.getName(), Status.LEAD), expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new StatusCommand(outOfBounds, Status.ACTIVE), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_contactInProject_keepsMembershipWithUpdatedContact() throws Exception {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Project project = new Project("Client Portal");
        model.addProject(project);
        model.addClientProject(new ClientProject(target, project));

        new StatusCommand(INDEX_FIRST_PERSON, Status.ACTIVE).execute(model);

        assertEquals(1, model.getClientBook().getClientProjectList().size());
        assertEquals(Status.ACTIVE, model.getClientBook().getClientProjectList().get(0).getClient().getStatus());
    }

    @Test
    public void equals() {
        StatusCommand command = new StatusCommand(INDEX_FIRST_PERSON, Status.ACTIVE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new StatusCommand(INDEX_FIRST_PERSON, Status.ACTIVE)));
        assertFalse(command.equals(new StatusCommand(INDEX_SECOND_PERSON, Status.ACTIVE)));
        assertFalse(command.equals(new StatusCommand(INDEX_FIRST_PERSON, Status.DONE)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        StatusCommand command = new StatusCommand(INDEX_FIRST_PERSON, Status.ON_HOLD);
        String expected = StatusCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", status=on hold}";
        assertEquals(expected, command.toString());
    }
}
