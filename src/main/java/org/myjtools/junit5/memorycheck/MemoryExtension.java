package org.myjtools.junit5.memorycheck;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MemoryExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private final long maxBytes;
	private long before;

	public MemoryExtension(long maxBytes) {
		this.maxBytes = maxBytes;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		sleep();
		before = used(runtime);
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		sleep();

		long after = used(runtime);
		long used = Math.max(0,after - before);

		String formatted = format(used);
		System.out.println("[memory-check] " + context.getDisplayName() + ": " + formatted);
		context.publishReportEntry("memoryUsed", formatted);

		if (used > maxBytes) {
			throw new AssertionError(
					"Memory exceeded: " + format(used) + " > " + format(maxBytes) + " (" + format(used - maxBytes) + " bytes over limit)"
			);
		}



	}

	private long used(Runtime r) {
		return r.totalMemory() - r.freeMemory();
	}

	private void sleep() {
		try { Thread.sleep(100); } catch (InterruptedException ignored) {}
	}

	private static String format(long bytes) {
		if (bytes < 1_000) return bytes + " B";
		if (bytes < 1_000_000) return String.format("%.2f KB", bytes / 1_000.0);
		if (bytes < 1_000_000_000) return String.format("%.2f MB", bytes / 1_000_000.0);
		return String.format("%.2f GB", bytes / 1_000_000_000.0);
	}
}
