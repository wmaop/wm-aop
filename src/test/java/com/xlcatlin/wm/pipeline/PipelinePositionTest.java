package com.xlcatlin.wm.pipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

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
