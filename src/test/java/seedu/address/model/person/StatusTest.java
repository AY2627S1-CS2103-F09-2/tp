package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class StatusTest {

    @Test
    public void fromText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Status.fromText(null));
    }

    @Test
    public void fromText_exactLabels_returnsStatus() {
        assertEquals(Optional.of(Status.LEAD), Status.fromText("lead"));
        assertEquals(Optional.of(Status.ACTIVE), Status.fromText("active"));
        assertEquals(Optional.of(Status.ON_HOLD), Status.fromText("on hold"));
        assertEquals(Optional.of(Status.DONE), Status.fromText("done"));
        assertEquals(Optional.of(Status.NONE), Status.fromText("none"));
    }

    @Test
    public void fromText_differentCaseAndSeparators_returnsStatus() {
        assertEquals(Optional.of(Status.ACTIVE), Status.fromText("  ACTIVE "));
        assertEquals(Optional.of(Status.ON_HOLD), Status.fromText("On-Hold"));
        assertEquals(Optional.of(Status.ON_HOLD), Status.fromText("on_hold"));
        assertEquals(Optional.of(Status.ON_HOLD), Status.fromText("on   hold"));
        assertEquals(Optional.of(Status.ON_HOLD), Status.fromText("onhold"));
    }

    @Test
    public void fromText_notAStatus_returnsEmpty() {
        assertEquals(Optional.empty(), Status.fromText(""));
        assertEquals(Optional.empty(), Status.fromText("pending"));
        assertEquals(Optional.empty(), Status.fromText("on hold now"));
    }

    @Test
    public void isValidStatus() {
        assertTrue(Status.isValidStatus("Done"));
        assertFalse(Status.isValidStatus("finished"));
    }

    @Test
    public void isNone() {
        assertTrue(Status.NONE.isNone());
        assertFalse(Status.ACTIVE.isNone());
    }

    @Test
    public void toStringMethod_returnsLabel() {
        assertEquals("on hold", Status.ON_HOLD.toString());
    }
}
