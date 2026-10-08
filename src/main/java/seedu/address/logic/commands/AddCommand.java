package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the client book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the client book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the client book.";
    public static final String MESSAGE_DUPLICATE_EMAIL_WARNING =
            "Warning: Email %1$s already belongs to another contact.";
    public static final String MESSAGE_DUPLICATE_PHONE_WARNING =
            "Warning: Phone %1$s already belongs to another contact.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        String warning = getContactDetailsWarning(model);
        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)) + warning);
    }

    /**
     * Returns warnings for shared email and phone values across the entire client book.
     * Must be called before adding {@code toAdd} so it does not match itself.
     */
    private String getContactDetailsWarning(Model model) {
        boolean hasDuplicateEmail = false;
        boolean hasDuplicatePhone = false;
        for (Person person : model.getClientBook().getPersonList()) {
            hasDuplicateEmail |= person.getEmail().equals(toAdd.getEmail());
            hasDuplicatePhone |= person.getPhone().equals(toAdd.getPhone());
        }

        StringBuilder warning = new StringBuilder();
        if (hasDuplicateEmail) {
            warning.append("\n").append(String.format(MESSAGE_DUPLICATE_EMAIL_WARNING, toAdd.getEmail()));
        }
        if (hasDuplicatePhone) {
            warning.append("\n").append(String.format(MESSAGE_DUPLICATE_PHONE_WARNING, toAdd.getPhone()));
        }
        return warning.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
