package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.InvalidIndexException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Company;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.project.ClientProject;
import seedu.address.model.project.Project;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65"; // fewer than 3 digits
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsInvalidIndexException() {
        // whole numbers that can't be a position in the list: the message is about the index, not the format
        for (String index : List.of("0", "-1", " -12 ", Long.toString(Integer.MAX_VALUE + 1L),
                "99999999999999999999")) {
            assertThrows(InvalidIndexException.class, Messages.MESSAGE_INDEX_NOT_IN_LIST, ()
                -> ParserUtil.parseIndex(index));
        }
    }

    @Test
    public void parseIndex_notAWholeNumber_throwsParseException() {
        for (String index : List.of("+1", "1.5", "abc", "", "1 2")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndex(index));
        }
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseIndexes_validInput_deduplicatesIndexes() throws Exception {
        assertEquals(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                ParserUtil.parseIndexes(" 1, 2,1 "));
    }

    @Test
    public void parseIndexes_invalidInput_throwsParseException() {
        for (String indexes : List.of("", "1,", ",1", "1,,2")) {
            assertThrows(ParseException.class, () -> ParserUtil.parseIndexes(indexes));
        }
    }

    @Test
    public void parseIndexes_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIndexes(null));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_repeatedSpaces_returnsNormalizedName() throws Exception {
        Name name = ParserUtil.parseName("  Rachel   Walker  ");
        assertEquals("Rachel Walker", name.fullName);
        assertThrows(ParseException.class, () -> ParserUtil.parseName("   "));
        assertThrows(ParseException.class, () -> ParserUtil.parseName("Rachel\tWalker"));
    }

    @Test
    public void parseProject_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseProject(null));
    }

    @Test
    public void parseProject_blankName_throwsParseException() {
        for (String name : List.of("", "   ", WHITESPACE)) {
            assertThrows(ParseException.class, Project.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseProject(name));
        }
    }

    @Test
    public void parseProject_validName_returnsProject() throws Exception {
        Project project = ParserUtil.parseProject("Client Portal");
        assertEquals(new Project("Client Portal"), project);
        assertEquals("Client Portal", project.getName());
    }

    @Test
    public void parseProject_extraWhitespace_returnsNormalizedProject() throws Exception {
        Project project = ParserUtil.parseProject(WHITESPACE + "Client   Portal" + WHITESPACE);
        assertEquals("Client Portal", project.getName());
    }

    @Test
    public void parseRole_validInput_returnsNormalizedRole() throws Exception {
        assertEquals("senior developer", ParserUtil.parseRole(" senior   developer "));
    }

    @Test
    public void parseRole_nullOrBlank_throws() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRole(null));
        assertThrows(ParseException.class, ClientProject.MESSAGE_ROLE_CONSTRAINTS, () ->
                ParserUtil.parseRole(" \t "));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseCompany_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseCompany(null));
    }

    @Test
    public void parseCompany_blank_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseCompany(WHITESPACE));
    }

    @Test
    public void parseCompany_validValueWithWhitespace_returnsTrimmedCompany() throws Exception {
        assertEquals(new Company("Acme Pte Ltd"), ParserUtil.parseCompany(WHITESPACE + "Acme Pte Ltd" + WHITESPACE));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_valueWithSpace_throwsSpaceMessage() {
        // text after an unknown prefix, or a typo in a prefix, ends up after the email
        for (String email : new String[] {"x@example.com x/what", "x@example.com adr/Blk 5", "x@example.com extra",
            "john doe@example.com", "x@example.com\tx/what"}) {
            assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_EMAIL_HAS_SPACE, email), ()
                    -> ParserUtil.parseEmail(WHITESPACE + email + WHITESPACE));
        }
    }

    @Test
    public void parseEmail_overlongValue_throwsParseException() {
        String overlongEmail = "a".repeat(64) + "@" + "b".repeat(63) + "."
                + "c".repeat(63) + "." + "d".repeat(62); // 255 characters
        assertThrows(ParseException.class, Email.MESSAGE_LENGTH_CONSTRAINTS, ()
                -> ParserUtil.parseEmail(overlongEmail));
        assertThrows(ParseException.class, Email.MESSAGE_LENGTH_CONSTRAINTS, ()
                -> ParserUtil.parseEmail(WHITESPACE + overlongEmail + WHITESPACE));
        assertThrows(ParseException.class, Email.MESSAGE_LENGTH_CONSTRAINTS, ()
                -> ParserUtil.parseEmail("a+b_c.d-".repeat(10_000) + "a@example.com"));
    }

    @Test
    public void parseEmail_overlongInvalidFormat_throwsFullConstraintsMessage() {
        String invalidEmail = "a".repeat(Email.MAX_LENGTH) + "@exam_ple.com";
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseEmail(invalidEmail));
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseEmail("a+b_c.d-".repeat(10_000) + "a..b@example.com"));
    }

    @Test
    public void parseEmail_maxLengthValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String email = "a".repeat(64) + "@" + "b".repeat(63) + "."
                + "c".repeat(63) + "." + "d".repeat(61); // 254 characters
        assertEquals(new Email(email), ParserUtil.parseEmail(WHITESPACE + email + WHITESPACE));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_tagsDifferingOnlyInCase_returnsOneTag() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of("Friends", "friends", "FRIENDS"));
        assertEquals(Set.of(new Tag("Friends")), actualTagSet);
        assertEquals(1, actualTagSet.size());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
