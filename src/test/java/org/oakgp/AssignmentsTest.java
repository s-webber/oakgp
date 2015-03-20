package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.oakgp.Assignments.createAssignments;

import org.junit.Test;

public class AssignmentsTest {
	@Test
	public void test() {
		int x = 9;
		int y = 7;
		Object[] values = { x, y };
		Assignments assignments = createAssignments(values);
		assertEquals(x, assignments.get(0));
		assertEquals(y, assignments.get(1));

		// assert Assignments remains unchanged by subsequent changes to values
		values[0] = 10;
		assertEquals(x, assignments.get(0));
		assertEquals(y, assignments.get(1));

		assertArrayIndexOutOfBoundsException(assignments, -1);
		assertArrayIndexOutOfBoundsException(assignments, 2);
	}

	@Test
	public void testEqualsAndHashCode() {
		Assignments a1 = createAssignments("hello", true, 42);
		Assignments a2 = createAssignments("hello", true, 42);
		assertEquals(a1, a1);
		assertEquals(a1.hashCode(), a2.hashCode());
		assertEquals(a1, a2);
	}

	@Test
	public void testNotEquals() {
		Assignments a = createAssignments("hello", true, 42);

		// same arguments, different order
		assertNotEquals(a, createAssignments(42, true, "hello"));

		// different arguments
		assertNotEquals(a, createAssignments("hello", true, 43));

		// one fewer argument
		assertNotEquals(a, createAssignments("hello", true));

		// one extra argument
		assertNotEquals(a, createAssignments("hello", true, 42, 42));
	}

	@Test
	public void testToString() {
		Assignments assignments = createAssignments("hello", true, 42);
		assertEquals("[hello, true, 42]", assignments.toString());
	}

	private void assertArrayIndexOutOfBoundsException(Assignments assignments, int index) {
		try {
			assignments.get(index);
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
			// expected
		}
	}
}
