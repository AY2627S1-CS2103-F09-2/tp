package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameOrCompanyContainsKeywordsPredicateTest {

    private static final Person ALICE_AT_ACME = new PersonBuilder().withName("Alice Tan")
            .withCompany("Acme Pte Ltd").build();
    private static final Person BOB_FREELANCER = new PersonBuilder().withName("Bob Lim").build();

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NameOrCompanyContainsKeywordsPredicate(null));
    }

    @Test
    public void constructor_keywordsChangedAfterwards_keepsOriginalKeywords() {
        List<String> keywords = new ArrayList<>(List.of("acme"));
        NameOrCompanyContainsKeywordsPredicate predicate = new NameOrCompanyContainsKeywordsPredicate(keywords);
        keywords.set(0, "globex");
        assertTrue(predicate.test(ALICE_AT_ACME));
    }

    @Test
    public void test_nameContainsKeyword_returnsTrue() {
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("ali")).test(ALICE_AT_ACME));
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("LIM")).test(BOB_FREELANCER));

        // middle and end of a word
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("LiC")).test(ALICE_AT_ACME));
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("ice")).test(ALICE_AT_ACME));

        // accented letters, ignoring case
        Person jose = new PersonBuilder().withName("Jos\u00e9").build();
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("OS\u00c9")).test(jose));
    }

    @Test
    public void test_companyContainsKeyword_returnsTrue() {
        // full word, ignoring case
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("ACME")).test(ALICE_AT_ACME));

        // part of a later word
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("pte")).test(ALICE_AT_ACME));

        // only one keyword matches, and only the company
        assertTrue(new NameOrCompanyContainsKeywordsPredicate(List.of("globex", "acm")).test(ALICE_AT_ACME));
    }

    @Test
    public void test_neitherNameNorCompanyContainsKeyword_returnsFalse() {
        // zero keywords
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of()).test(ALICE_AT_ACME));

        // no match in the name or the company
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of("globex", "carol")).test(ALICE_AT_ACME));

        // a contact without a company only matches on the name
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of("acme")).test(BOB_FREELANCER));

        // the keyword is spread across the name and the company, so neither contains it
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of("tanacme")).test(ALICE_AT_ACME));

        // the keyword is longer than the name
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of("Bob Limited")).test(BOB_FREELANCER));

        // keywords are literal text, not regular expressions
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of(".", "A.*")).test(ALICE_AT_ACME));

        // keywords that match other fields only
        Person withOtherFields = new PersonBuilder(ALICE_AT_ACME).withPhone("12345").withEmail("alice@globex.com")
                .withAddress("Main Street").build();
        assertFalse(new NameOrCompanyContainsKeywordsPredicate(List.of("12345", "globex", "Main"))
                .test(withOtherFields));
    }

    @Test
    public void equals() {
        NameOrCompanyContainsKeywordsPredicate first = new NameOrCompanyContainsKeywordsPredicate(List.of("acme"));
        NameOrCompanyContainsKeywordsPredicate firstCopy =
                new NameOrCompanyContainsKeywordsPredicate(List.of("acme"));

        // same object -> returns true
        assertTrue(first.equals(first));

        // same values -> returns true
        assertTrue(first.equals(firstCopy));
        assertEquals(first.hashCode(), firstCopy.hashCode());

        // different types -> returns false
        assertFalse(first.equals(1));

        // null -> returns false
        assertFalse(first.equals(null));

        // different keywords -> returns false
        assertFalse(first.equals(new NameOrCompanyContainsKeywordsPredicate(List.of("globex"))));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("acme", "ali");
        NameOrCompanyContainsKeywordsPredicate predicate = new NameOrCompanyContainsKeywordsPredicate(keywords);
        String expected = NameOrCompanyContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
