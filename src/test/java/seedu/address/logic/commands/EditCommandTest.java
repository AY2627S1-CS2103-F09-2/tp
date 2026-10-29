package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.ClientBookParser;
import seedu.address.model.ClientBook;
import seedu.address.model.ContactView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalClientBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sharedEmail_successWithEmailWarning() {
        Person editedPerson = new PersonBuilder(BENSON).withEmail(ALICE.getEmail().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(ALICE.getEmail().value).build();

        assertEditWithWarning(descriptor, editedPerson,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, editedPerson.getEmail()));
    }

    @Test
    public void execute_sharedPhone_successWithPhoneWarning() {
        Person editedPerson = new PersonBuilder(BENSON).withPhone(ALICE.getPhone().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(ALICE.getPhone().value).build();

        assertEditWithWarning(descriptor, editedPerson,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, editedPerson.getPhone()));
    }

    @Test
    public void execute_sampleDataSharedEmailAndPhone_successWithBothWarnings() throws Exception {
        model = new ModelManager(SampleDataUtil.getSampleClientBook(), new UserPrefs());
        Person personToEdit = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withPhone("87438807")
                .withEmail("alexyeoh@example.com").build();
        Command editCommand = new ClientBookParser().parseCommand("edit 2 p/87438807 e/alexyeoh@example.com");

        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\nWarning: Email alexyeoh@example.com already belongs to another contact."
                + "\nWarning: Phone 87438807 already belongs to another contact.";

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emailAndPhoneBelongToDifferentContacts_successWithBothWarnings() {
        Person editedPerson = new PersonBuilder(BENSON).withEmail(ALICE.getEmail().value)
                .withPhone(CARL.getPhone().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(ALICE.getEmail().value)
                .withPhone(CARL.getPhone().value).build();

        assertEditWithWarning(descriptor, editedPerson,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, editedPerson.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, editedPerson.getPhone()));
    }

    @Test
    public void execute_multipleContactsShareDetails_warnsOncePerField() {
        model.addPerson(new PersonBuilder(ALICE).withName("Other Owner").build());
        Person editedPerson = new PersonBuilder(BENSON).withEmail(ALICE.getEmail().value)
                .withPhone(ALICE.getPhone().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(ALICE.getEmail().value)
                .withPhone(ALICE.getPhone().value).build();

        assertEditWithWarning(descriptor, editedPerson,
                "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, editedPerson.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, editedPerson.getPhone()));
    }

    @Test
    public void execute_renamedContactWithOwnDetails_successWithoutWarning() {
        Person editedPerson = new PersonBuilder(BENSON).withName(VALID_NAME_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();

        assertEditWithWarning(descriptor, editedPerson, "");
    }

    @Test
    public void execute_sharedDetailsHiddenByFilter_successWithBothWarnings() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person editedPerson = new PersonBuilder(BENSON).withEmail(ALICE.getEmail().value)
                .withPhone(ALICE.getPhone().value).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withEmail(ALICE.getEmail().value)
                .withPhone(ALICE.getPhone().value).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(BENSON, editedPerson);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_EMAIL_WARNING, editedPerson.getEmail())
                + "\n" + String.format(AddCommand.MESSAGE_DUPLICATE_PHONE_WARNING, editedPerson.getPhone());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        assertEquals(ContactView.filtered(), model.getContactView());
    }

    /**
     * Verifies feedback and the updated model when editing the second contact.
     */
    private void assertEditWithWarning(EditPersonDescriptor descriptor, Person editedPerson, String warning) {
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);
        Model expectedModel = new ModelManager(model.getClientBook(), new UserPrefs());
        expectedModel.setPerson(BENSON, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)) + warning,
                expectedModel);
    }

    @Test
    public void execute_company_setsAndRemovesCompany() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withCompany = new PersonBuilder(firstPerson).withCompany("Acme Pte Ltd").build();
        EditCommand setCompany = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withCompany("Acme Pte Ltd").build());

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, withCompany);
        assertCommandSuccess(setCompany, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(withCompany)), expectedModel);

        // editing another field keeps the company
        Person renamed = new PersonBuilder(withCompany).withName(VALID_NAME_BOB).build();
        EditCommand rename = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());
        expectedModel.setPerson(withCompany, renamed);
        assertCommandSuccess(rename, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(renamed)), expectedModel);

        // "c/" alone removes it
        EditCommand removeCompany = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withoutCompany().build());
        expectedModel.setPerson(renamed, new PersonBuilder(firstPerson).withName(VALID_NAME_BOB).build());
        assertCommandSuccess(removeCompany, model, String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                Messages.format(new PersonBuilder(firstPerson).withName(VALID_NAME_BOB).build())), expectedModel);
    }

    @Test
    public void execute_contactWithNote_keepsNote() {
        Person target = model.getFilteredPersonList().get(0);
        model.setPerson(target, target.withNote(new Note("Prefers email")));
        Person withNote = model.getFilteredPersonList().get(0);
        Person editedPerson = new PersonBuilder(withNote).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(withNote, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
        assertEquals(new Note("Prefers email"), model.getFilteredPersonList().get(0).getNote());
    }

    @Test
    public void execute_contactWithStatus_keepsStatus() {
        Person target = model.getFilteredPersonList().get(0);
        model.setPerson(target, target.withStatus(Status.ACTIVE));
        Person withStatus = model.getFilteredPersonList().get(0);
        Person editedPerson = new PersonBuilder(withStatus).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(withStatus, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
        assertEquals(Status.ACTIVE, model.getFilteredPersonList().get(0).getStatus());
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_preservesFilteredView() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        assertEquals(ContactView.filtered(), model.getContactView());
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in client book
        Person personInList = model.getClientBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_nameDiffersInCaseAndSpacing_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("  aLiCe   pAuLiNe  ").build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicateNameDiffersInCaseAndSpacingFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("  bEnSoN   mEiEr  ").build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_ownNameDiffersInCaseAndSpacing_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withName("  aLiCe   pAuLiNe  ").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ClientBook(model.getClientBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX,
                model.getFilteredPersonList().size()));
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of client book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of client book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getClientBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        // the message gives the range of the filtered list, not the whole client book
        assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_INVALID_CONTACT_DISPLAYED_INDEX, 1));
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
