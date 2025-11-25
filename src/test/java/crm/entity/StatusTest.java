package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StatusTest {

    @Test
    public void testEnumValues() {
        Status[] statuses = Status.values();
        assertEquals(4, statuses.length);
        assertEquals(Status.PROPOSED, statuses[0]);
        assertEquals(Status.NEGOTIATED, statuses[1]);
        assertEquals(Status.IMPLEMENTED, statuses[2]);
        assertEquals(Status.DONE, statuses[3]);
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    public void testEnumValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Status.valueOf("INVALID"));
    }

    @Test
    public void testAllConstant() {
        assertNotNull(Status.ALL);
        assertEquals(4, Status.ALL.length);
        assertEquals(Status.PROPOSED, Status.ALL[0]);
        assertEquals(Status.NEGOTIATED, Status.ALL[1]);
        assertEquals(Status.IMPLEMENTED, Status.ALL[2]);
        assertEquals(Status.DONE, Status.ALL[3]);
    }

    @Test
    public void testEnumEquality() {
        Status status1 = Status.PROPOSED;
        Status status2 = Status.PROPOSED;
        assertEquals(status1, status2);
        assertSame(status1, status2);
    }

    @Test
    public void testEnumInequality() {
        Status status1 = Status.PROPOSED;
        Status status2 = Status.DONE;
        assertNotEquals(status1, status2);
    }

    @Test
    public void testEnumOrdinal() {
        assertEquals(0, Status.PROPOSED.ordinal());
        assertEquals(1, Status.NEGOTIATED.ordinal());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    public void testEnumName() {
        assertEquals("PROPOSED", Status.PROPOSED.name());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.name());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.name());
        assertEquals("DONE", Status.DONE.name());
    }
}
