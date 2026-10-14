package seedu.address.model.project.exceptions;

/**
 * Signals that an operation would create a duplicate client-project membership.
 */
public class DuplicateClientProjectException extends RuntimeException {
    public DuplicateClientProjectException() {
        super("Operation would result in duplicate client-project memberships");
    }
}
