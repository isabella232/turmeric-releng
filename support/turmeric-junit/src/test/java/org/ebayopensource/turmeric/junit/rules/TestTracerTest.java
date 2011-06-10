package org.ebayopensource.turmeric.junit.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class TestTracerTest {
	public static class LogCapturing extends Handler
	{
		private List<String> messages = new ArrayList<String>();
		
		public void clearCaptured() {
			messages.clear();
		}
		
		@Override
		public void publish(LogRecord record) {
			messages.add(record.getMessage());
		}

		@Override
		public void flush() {
			/* do nothing */
		}

		@Override
		public void close() throws SecurityException {
			/* do nothing */
		}
	}
	
	private static LogCapturing capturing;
	private static Logger log; 
	
	@BeforeClass
	public static void setupLogCapture()
	{
		Logger testlog = Logger.getLogger(TestTracer.LOG_NAME);
		
		for(Handler handler: testlog.getHandlers()) {
			if(handler instanceof LogCapturing) {
				testlog.removeHandler(handler);
			}
		}
		
		capturing = new LogCapturing();
		testlog.addHandler(capturing);
		
		log = Logger.getLogger(TestTracerTest.class.getName());
		log.addHandler(capturing);
	}
	
	@AfterClass
	public static void validatePrintStream()
	{
		String captured = StringUtils.join(capturing.messages.iterator(),
				SystemUtils.LINE_SEPARATOR);
		
		System.out.println(captured); // Show what we captured.
		
		// Build expected output
		StringBuilder expected = new StringBuilder();
		String className = TestTracerTest.class.getName();
		String ln = SystemUtils.LINE_SEPARATOR;
		String id = String.format("### %s / testSomething ::: ", className);
		expected.append(id).append("Starting").append(ln);
		expected.append("Testing something").append(ln);
		expected.append("Tracked requests for the following resources:").append(ln);
		expected.append(id).append("Completed").append(ln);
		id = String.format("### %s / testSomethingElse ::: ", className);
		expected.append(id).append("Starting").append(ln);
		expected.append("Testing something else").append(ln);
		expected.append("Tracked requests for the following resources:").append(ln);
		expected.append(id).append("Completed");
		
		// Validate output
		Assert.assertEquals(expected.toString(), captured);
	}

	@Rule
	public TestTracer tracer = new TestTracer().setTrackResourceUsage(true);

	@Test
	public void testSomething() {
		log.info("Testing something");
	}

	@Test
	public void testSomethingElse() {
		log.info("Testing something else");
	}
}
