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
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;

public class NoteCommandTest {

    private static final Note NOTE = new Note("Based in London, prefers email");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalClientBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NoteCommand(null, NOTE));
        assertThrows(NullPointerException.class, () -> new NoteCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_newNote_addsNote() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = copyOf(model);
        expectedModel.setPerson(target, target.withNote(NOTE));

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, NOTE), model,
                String.format(NoteCommand.MESSAGE_ADD_SUCCESS, target.getName(), NOTE), expectedModel);
    }

    @Test
    public void execute_differentNote_replacesNote() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(target, target.withNote(new Note("Old note")));
        Person withOldNote = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = copyOf(model);
        expectedModel.setPerson(withOldNote, withOldNote.withNote(NOTE));

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, NOTE), model,
                String.format(NoteCommand.MESSAGE_ADD_SUCCESS, target.getName(), NOTE), expectedModel);
    }

    @Test
    public void execute_emptyNote_removesNote() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(target, target.withNote(NOTE));
        Person withNote = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = copyOf(model);
        expectedModel.setPerson(withNote, withNote.withNote(Note.EMPTY));

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY), model,
                String.format(NoteCommand.MESSAGE_REMOVE_SUCCESS, target.getName()), expectedModel);
    }

    @Test
    public void execute_sameNote_reportsNothingChanged() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(target, target.withNote(NOTE));

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, NOTE), model,
                String.format(NoteCommand.MESSAGE_UNCHANGED, target.getName()), copyOf(model));
    }

    @Test
    public void execute_removeWithoutNote_reportsNothingToRemove() {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY), model,
                String.format(NoteCommand.MESSAGE_NOTHING_TO_REMOVE, target.getName()), copyOf(model));
    }

    @Test
    public void execute_filteredList_addsNoteToDisplayedContact() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person target = model.getFilteredPersonList().get(0);
        Model expectedModel = copyOf(model);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(target, target.withNote(NOTE));

        assertCommandSuccess(new NoteCommand(INDEX_FIRST_PERSON, NOTE), model,
                String.format(NoteCommand.MESSAGE_ADD_SUCCESS, target.getName(), NOTE), expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new NoteCommand(outOfBounds, NOTE), model,
                String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, model.getFilteredPersonList().size()));
    }

    @Test
    public void equals() {
        NoteCommand command = new NoteCommand(INDEX_FIRST_PERSON, NOTE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new NoteCommand(INDEX_FIRST_PERSON, new Note(NOTE.value))));
        assertFalse(command.equals(new NoteCommand(INDEX_SECOND_PERSON, NOTE)));
        assertFalse(command.equals(new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }

    @Test
    public void toStringMethod() {
        NoteCommand command = new NoteCommand(INDEX_FIRST_PERSON, NOTE);
        String expected = NoteCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", note=" + NOTE + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Returns a model with a copy of {@code model}'s client book.
     */
    private static Model copyOf(Model model) {
        return new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
    }
}
