package org.wmaop.pipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wmaop.aop.InterceptPoint;
import org.wmaop.aop.pipeline.FlowPosition;

public class PipelinePositionTest {

	@Test
	public void test() {
		assertEquals("pub", new FlowPosition(InterceptPoint.INVOKE, "pub:foo").packageName);
		assertEquals("foo", new FlowPosition(InterceptPoint.INVOKE, "pub:foo").serviceName);
		assertEquals("", new FlowPosition(InterceptPoint.INVOKE, "").packageName);
		assertEquals("", new FlowPosition(InterceptPoint.INVOKE, "").serviceName);
		assertEquals("foo", new FlowPosition(InterceptPoint.INVOKE, "foo").serviceName);
	}

}
