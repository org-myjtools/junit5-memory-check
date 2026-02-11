package org.myjtools.junit5.memorycheck;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.*;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;

class MemoryExtensionTest {

	static class ShouldPass {
		@RegisterExtension
		MemoryExtension memoryCheck = new MemoryExtension(100_000_000);

		@Test
		void lightOperation() {
			int x = 42;
		}
	}

	static class ShouldFail {
		@RegisterExtension
		MemoryExtension memoryCheck = new MemoryExtension(100);

		// stored as field so GC cannot collect it before afterTestExecution
		@SuppressWarnings("unused")
		byte[] retained;

		@Test
		void heavyAllocation() {
			retained = new byte[10_000_000];
		}
	}

	// Simulates a realistic scenario: processing data that builds up collections in memory
	static class RealisticUsage {
		@RegisterExtension
		MemoryExtension memoryCheck = new MemoryExtension(5_000_000); // 5MB limit

		@SuppressWarnings("unused")
		List<Map<String, String>> retained;

		@Test
		void processUserRecords() {
			retained = new ArrayList<>();
			for (int i = 0; i < 50_000; i++) {
				Map<String, String> record = new HashMap<>();
				record.put("id", String.valueOf(i));
				record.put("name", "User " + i);
				record.put("email", "user" + i + "@example.com");
				retained.add(record);
			}
		}
	}

	@Test
	void shouldPassWhenMemoryWithinLimit() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectClass(ShouldPass.class))
			.execute()
			.testEvents()
			.assertThatEvents()
			.haveExactly(1, event(finishedSuccessfully()));
	}

	@Test
	void shouldFailWhenRealisticUsageExceedsLimit() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectClass(RealisticUsage.class))
			.execute()
			.testEvents()
			.assertThatEvents()
			.haveExactly(1, event(finishedWithFailure(
				instanceOf(AssertionError.class),
				message(m -> m.contains("Memory exceeded"))
			)));
	}

	@Test
	void shouldFailWhenMemoryExceeded() {
		EngineTestKit.engine("junit-jupiter")
			.selectors(selectClass(ShouldFail.class))
			.execute()
			.testEvents()
			.assertThatEvents()
			.haveExactly(1, event(finishedWithFailure(
				instanceOf(AssertionError.class),
				message(m -> m.contains("Memory exceeded"))
			)));
	}
}