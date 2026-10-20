package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ListCommand} object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses {@code args} into a {@code ListCommand}: one listing every contact, or, given
     * {@code s/STATUS}, one listing only the contacts with that status.
     * Other text after {@code list} is ignored, as it was before statuses existed.
     *
     * @throws ParseException if the status is invalid or given more than once.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_STATUS);
        if (argMultimap.getValue(PREFIX_STATUS).isEmpty()) {
            return new ListCommand();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STATUS);
        return new ListCommand(ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS).get()));
    }
}
