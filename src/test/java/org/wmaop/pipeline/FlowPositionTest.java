package org.wmaop.pipeline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptPoint;

public class FlowPositionTest {

	@Test
	public void shouldParseServiceName() {
		FlowPosition fp1 = new FlowPosition(InterceptPoint.BEFORE, "foo");
		assertEquals("foo", fp1.getServiceName());
		assertEquals("", fp1.getPackageName());
		assertEquals("foo", fp1.getFqname());
		assertEquals("foo", fp1.toString());
	}		

	@Test
	public void shouldReportInterceptPoint() {
		FlowPosition fp1 = new FlowPosition(InterceptPoint.BEFORE, "foo");
		assertEquals(InterceptPoint.BEFORE, fp1.getInterceptPoint());
		fp1.setInterceptPoint(InterceptPoint.INVOKE);
		assertEquals(InterceptPoint.INVOKE, fp1.getInterceptPoint());
	}
	
	@Test
	public void shouldParsePackageAndService() {
		FlowPosition fp2 = new FlowPosition(InterceptPoint.BEFORE, "foo:bar");
		assertEquals("bar", fp2.getServiceName());
		assertEquals("foo", fp2.getPackageName());
		assertEquals("foo:bar", fp2.getFqname());
	}

	@Test
	public void shouldHandleBlankServiceName() {
		FlowPosition fp3 = new FlowPosition(InterceptPoint.BEFORE, "");
		assertEquals("", fp3.getServiceName());
		assertEquals("", fp3.getPackageName());
		assertEquals("", fp3.getFqname());
	}

	@Test
	public void shouldHandleNullServiceName() {
		FlowPosition fp4 = new FlowPosition(InterceptPoint.BEFORE, null);
		assertEquals("", fp4.getServiceName());
		assertEquals("", fp4.getPackageName());
		assertEquals("", fp4.getFqname());
	}
}
