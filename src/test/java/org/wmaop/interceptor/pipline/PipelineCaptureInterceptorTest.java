package org.wmaop.interceptor.pipline;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.wmaop.interceptor.pipline.PipelineCaptureInterceptor;

import com.wm.data.IDataFactory;

public class PipelineCaptureInterceptorTest {

	@Test
	public void test() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("target/foo.xml"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("target/foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
	}

	@Test
	public void testNoExrtension() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("target/foo"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("target/foo-1.xml")).thenReturn(baos);

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
