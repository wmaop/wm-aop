package com.xlcatlin.wm.interceptor.pipline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.wm.data.IDataFactory;

public class PipelineCaptureInterceptorTest {

	@Test
	public void test() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("foo.xml"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
	}

	@Test
	public void testNoExrtension() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("foo"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
	}

	@Test
	public void testExeption() throws Exception {
		PipelineCaptureInterceptor pci = new PipelineCaptureInterceptor("z///zxzz:\foojashfjh");
		try {
			pci.intercept(null, IDataFactory.create());
			fail();
		} catch (RuntimeException e) {
		}
	}
}