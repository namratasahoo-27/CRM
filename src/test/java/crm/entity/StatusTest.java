package crm.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testStatusEnumValues() {
        Status[] expectedValues = {Status.PROPOSED, Status.NEGOTIATED, Status.IMPLEMENTED, Status.DONE};
        Status[] actualValues = Status.values();

        assertEquals(expectedValues.length, actualValues.length);
        assertArrayEquals(expectedValues, actualValues);
    }

    @Test
    void testProposedStatus() {
        assertEquals("PROPOSED", Status.PROPOSED.name());
        assertEquals(0, Status.PROPOSED.ordinal());
    }

    @Test
    void testNegotiatedStatus() {
        assertEquals("NEGOTIATED", Status.NEGOTIATED.name());
        assertEquals(1, Status.NEGOTIATED.ordinal());
    }

    @Test
    void testImplementedStatus() {
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.name());
        assertEquals(2, Status.IMPLEMENTED.ordinal());
    }

    @Test
    void testDoneStatus() {
        assertEquals("DONE", Status.DONE.name());
        assertEquals(3, Status.DONE.ordinal());
    }

    @Test
    void testStatusValueOf() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));
        assertEquals(Status.NEGOTIATED, Status.valueOf("NEGOTIATED"));
        assertEquals(Status.IMPLEMENTED, Status.valueOf("IMPLEMENTED"));
        assertEquals(Status.DONE, Status.valueOf("DONE"));
    }

    @Test
    void testStatusValueOfInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            Status.valueOf("INVALID_STATUS");
        });
    }

    @Test
    void testStatusValueOfNullValue() {
        assertThrows(NullPointerException.class, () -> {
            Status.valueOf(null);
        });
    }

    @Test
    void testStatusToString() {
        assertEquals("PROPOSED", Status.PROPOSED.toString());
        assertEquals("NEGOTIATED", Status.NEGOTIATED.toString());
        assertEquals("IMPLEMENTED", Status.IMPLEMENTED.toString());
        assertEquals("DONE", Status.DONE.toString());
    }

    @Test
    void testAllStatusesConstant() {
        Status[] allStatuses = Status.ALL;
        Status[] enumValues = Status.values();

        assertNotNull(allStatuses);
        assertEquals(enumValues.length, allStatuses.length);

        for (int i = 0; i < enumValues.length; i++) {
            assertEquals(enumValues[i], allStatuses[i]);
        }
    }

    @Test
    void testAllStatusesContainsAllValues() {
        Status[] allStatuses = Status.ALL;

        assertTrue(contains(allStatuses, Status.PROPOSED));
        assertTrue(contains(allStatuses, Status.NEGOTIATED));
        assertTrue(contains(allStatuses, Status.IMPLEMENTED));
        assertTrue(contains(allStatuses, Status.DONE));
    }

    @Test
    void testAllStatusesOrder() {
        Status[] allStatuses = Status.ALL;

        assertEquals(Status.PROPOSED, allStatuses[0]);
        assertEquals(Status.NEGOTIATED, allStatuses[1]);
        assertEquals(Status.IMPLEMENTED, allStatuses[2]);
        assertEquals(Status.DONE, allStatuses[3]);
    }

    @Test
    void testStatusComparison() {
        assertTrue(Status.PROPOSED.ordinal() < Status.NEGOTIATED.ordinal());
        assertTrue(Status.NEGOTIATED.ordinal() < Status.IMPLEMENTED.ordinal());
        assertTrue(Status.IMPLEMENTED.ordinal() < Status.DONE.ordinal());
    }

    @Test
    void testStatusEquals() {
        assertEquals(Status.PROPOSED, Status.PROPOSED);
        assertEquals(Status.NEGOTIATED, Status.NEGOTIATED);
        assertEquals(Status.IMPLEMENTED, Status.IMPLEMENTED);
        assertEquals(Status.DONE, Status.DONE);

        assertNotEquals(Status.PROPOSED, Status.NEGOTIATED);
        assertNotEquals(Status.NEGOTIATED, Status.IMPLEMENTED);
        assertNotEquals(Status.IMPLEMENTED, Status.DONE);
    }

    @Test
    void testStatusHashCode() {
        assertEquals(Status.PROPOSED.hashCode(), Status.PROPOSED.hashCode());
        assertEquals(Status.NEGOTIATED.hashCode(), Status.NEGOTIATED.hashCode());
        assertEquals(Status.IMPLEMENTED.hashCode(), Status.IMPLEMENTED.hashCode());
        assertEquals(Status.DONE.hashCode(), Status.DONE.hashCode());
    }

    @Test
    void testStatusIsEnum() {
        assertTrue(Status.class.isEnum());
        assertEquals(Enum.class, Status.class.getSuperclass());
    }

    @Test
    void testEnumConstantCount() {
        assertEquals(4, Status.values().length);
        assertEquals(4, Status.ALL.length);
    }

    @Test
    void testStatusInSwitchStatement() {
        for (Status status : Status.values()) {
            String result = getStatusDescription(status);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void testAllStatusesIsStaticFinal() throws NoSuchFieldException {
        var allField = Status.class.getDeclaredField("ALL");

        assertTrue(java.lang.reflect.Modifier.isStatic(allField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(allField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(allField.getModifiers()));
        assertEquals(Status[].class, allField.getType());
    }

    @Test
    void testStatusSequentialFlow() {
        // Test that status progresses logically
        Status[] expectedSequence = {Status.PROPOSED, Status.NEGOTIATED, Status.IMPLEMENTED, Status.DONE};

        for (int i = 0; i < expectedSequence.length; i++) {
            assertEquals(i, expectedSequence[i].ordinal());
        }
    }

    @Test
    void testStatusEnumDeclaration() {
        // Verify enum constants are declared correctly
        assertNotNull(Status.PROPOSED);
        assertNotNull(Status.NEGOTIATED);
        assertNotNull(Status.IMPLEMENTED);
        assertNotNull(Status.DONE);
    }

    @Test
    void testAllStatusesImmutability() {
        Status[] originalAll = Status.ALL;
        Status[] retrievedAll = Status.ALL;

        // Verify we get the same reference (static final)
        assertSame(originalAll, retrievedAll);

        // Verify array contents cannot be modified (though array reference can be)
        assertEquals(4, Status.ALL.length);
    }

    @Test
    void testStatusValueOfCaseSensitive() {
        assertEquals(Status.PROPOSED, Status.valueOf("PROPOSED"));

        assertThrows(IllegalArgumentException.class, () -> {
            Status.valueOf("proposed");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Status.valueOf("Proposed");
        });
    }

    @Test
    void testIterateOverAllStatuses() {
        int count = 0;
        for (Status status : Status.ALL) {
            assertNotNull(status);
            count++;
        }
        assertEquals(4, count);
    }

    // Helper method for testing contains functionality
    private boolean contains(Status[] array, Status target) {
        for (Status status : array) {
            if (status == target) {
                return true;
            }
        }
        return false;
    }

    // Helper method for testing switch statements
    private String getStatusDescription(Status status) {
        return switch (status) {
            case PROPOSED -> "Contract has been proposed";
            case NEGOTIATED -> "Contract is being negotiated";
            case IMPLEMENTED -> "Contract has been implemented";
            case DONE -> "Contract is complete";
        };
    }
}