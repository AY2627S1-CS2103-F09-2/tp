package seedu.address.storage;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedProjectTest {

    @Test
    public void toModelType_missingName_throwsIllegalValueException() {
        JsonAdaptedProject adaptedProject = new JsonAdaptedProject((String) null);

        assertThrows(IllegalValueException.class, adaptedProject::toModelType);
    }

    @Test
    public void toModelType_blankName_throwsIllegalValueException() {
        JsonAdaptedProject adaptedProject = new JsonAdaptedProject("   ");

        assertThrows(IllegalValueException.class, adaptedProject::toModelType);
    }
}
