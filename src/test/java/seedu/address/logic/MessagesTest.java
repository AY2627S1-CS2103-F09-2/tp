package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void getInvalidContactDisplayedIndexMessage_emptyList_returnsNoContactsMessage() {
        assertEquals("The displayed contact list is empty. Use list to show all contacts.",
                Messages.getInvalidContactDisplayedIndexMessage(0));
    }

    @Test
    public void getInvalidContactDisplayedIndexMessage_nonEmptyList_returnsValidRange() {
        assertEquals("The contact index provided is invalid. Pick a number from 1 to 3.",
                Messages.getInvalidContactDisplayedIndexMessage(3));
    }

    @Test
    public void format_noTags_omitsTags() {
        Person person = new PersonBuilder().withName("Alex").withPhone("12345678")
                .withEmail("alex@example.com").withAddress("123 Main Street").withCompany("Acme")
                .withTags().build();

        assertEquals("Alex; Phone: 12345678; Email: alex@example.com; Address: 123 Main Street; Company: Acme",
                Messages.format(person));
    }

    @Test
    public void format_withTags_includesTags() {
        Person person = new PersonBuilder().withName("Alex").withPhone("12345678")
                .withEmail("alex@example.com").withoutAddress().withTags("friends").build();

        assertEquals("Alex; Phone: 12345678; Email: alex@example.com; Tags: [friends]", Messages.format(person));
    }
}
