package seedu.address.model.project.exceptions;

/**
 * Signals that an operation would create a duplicate project.
 */
public class DuplicateProjectException extends RuntimeException {
    public DuplicateProjectException() {
        super("Operation would result in duplicate projects");
    }
}
