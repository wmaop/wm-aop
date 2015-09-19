package com.xlcatlin.wm.pipeline;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xlcatlin.wm.aop.InterceptPoint;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class FlowPositionTest {

	@Test
	public void shouldParseServiceNames() {
		FlowPosition fp1 = new FlowPosition(InterceptPoint.BEFORE, "foo");
		assertEquals("foo", fp1.getServiceName());
		assertEquals("", fp1.getPackageName());
		assertEquals("foo", fp1.getFqname());
		assertEquals(InterceptPoint.BEFORE, fp1.getInterceptPoint());
		
		FlowPosition fp2 = new FlowPosition(InterceptPoint.BEFORE, "foo:bar");
		assertEquals("bar", fp2.getServiceName());
		assertEquals("foo", fp2.getPackageName());
		assertEquals("foo:bar", fp2.getFqname());
		
		FlowPosition fp3 = new FlowPosition(InterceptPoint.BEFORE, "");
		assertEquals("", fp3.getServiceName());
		assertEquals("", fp3.getPackageName());
		assertEquals("", fp3.getFqname());
	}

}
