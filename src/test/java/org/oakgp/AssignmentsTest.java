package org.oakgp;

import static org.junit.Assert.assertEquals;
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

	private void assertArrayIndexOutOfBoundsException(Assignments assignments, int index) {
		try {
			assignments.get(index);
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
			// expected
		}
	}
}
