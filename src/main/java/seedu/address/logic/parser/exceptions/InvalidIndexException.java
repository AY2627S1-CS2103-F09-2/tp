package seedu.address.logic.parser.exceptions;

/**
 * Signals that a contact index is a whole number, but not one that can refer to a contact, e.g. 0 or -1.
 * Parsers let it through as it is, rather than replacing it with their usage message, so the user is told
 * what is wrong with the index instead of seeing a general format error.
 */
public class InvalidIndexException extends ParseException {

    public InvalidIndexException(String message) {
        super(message);
    }
}
