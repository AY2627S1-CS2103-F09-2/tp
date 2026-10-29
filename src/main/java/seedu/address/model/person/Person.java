package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the client book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Company company;
    private final Status status;
    private final Note note;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a contact with no known address, e.g. a remote client, and no company, status or note.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Set<Tag> tags) {
        this(name, phone, email, Address.EMPTY, tags);
    }

    /**
     * Creates a contact with no known company, no status and no note.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, Company.EMPTY, Status.NONE, Note.EMPTY, tags);
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Company company, Status status,
            Note note, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, company, status, note, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.company = company;
        this.status = status;
        this.note = note;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Company getCompany() {
        return company;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns a copy of this contact with the given status, keeping every other detail.
     */
    public Person withStatus(Status newStatus) {
        return new Person(name, phone, email, address, company, newStatus, note, tags);
    }

    public Note getNote() {
        return note;
    }

    /**
     * Returns a copy of this contact with the given note, keeping every other detail.
     */
    public Person withNote(Note newNote) {
        return new Person(name, phone, email, address, company, status, newNote, tags);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name (ignoring case), phone and email.
     * Names are already trimmed and have repeated spaces collapsed by {@link Name}.
     * Other details do not affect a person's identity.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().fullName.equalsIgnoreCase(getName().fullName)
                && otherPerson.getPhone().equals(getPhone())
                && otherPerson.getEmail().equals(getEmail());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && company.equals(otherPerson.company)
                && status == otherPerson.status
                && note.equals(otherPerson.note)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, company, status, note, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("company", company)
                .add("status", status)
                .add("note", note)
                .add("tags", tags)
                .toString();
    }

}
