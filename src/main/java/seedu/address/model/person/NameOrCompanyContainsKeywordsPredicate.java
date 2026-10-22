package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} or {@code Company} contains any of the keywords given,
 * ignoring case and allowing partial matches.
 */
public class NameOrCompanyContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Creates a predicate that matches a contact whose name or company contains any of {@code keywords}.
     */
    public NameOrCompanyContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        String name = person.getName().fullName.toLowerCase(Locale.ROOT);
        // A contact without a company has an empty one, which no keyword is part of.
        String company = person.getCompany().value.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .map(keyword -> keyword.toLowerCase(Locale.ROOT))
                .anyMatch(keyword -> name.contains(keyword) || company.contains(keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameOrCompanyContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
