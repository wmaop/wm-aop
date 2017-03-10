package org.wmaop.pipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;

public class PipelinePositionTest {

	@Test
	public void shouldExtractPackageName() {
		assertEquals("pub", new FlowPosition(InterceptPoint.INVOKE, "pub:foo").packageName);
		assertEquals("", new FlowPosition(InterceptPoint.INVOKE, "").packageName);
	}

	@Test
	public void shouldExtractServiceName() {
		assertEquals("foo", new FlowPosition(InterceptPoint.INVOKE, "pub:foo").serviceName);
		assertEquals("", new FlowPosition(InterceptPoint.INVOKE, "").serviceName);
		assertEquals("foo", new FlowPosition(InterceptPoint.INVOKE, "foo").serviceName);
	}
}
