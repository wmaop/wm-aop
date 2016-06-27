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
	public void shouldCaptureToFile() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("target/foo.xml"));
		assertEquals("No file captured", pci.toMap().get("currentFile")); // Ready for second capture
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("target/foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
		assertEquals("target/foo-1.xml", pci.toMap().get("currentFile"));
	}

	@Test
	public void ShouldUseDefaultExtension() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("target/foo"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("target/foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
	}

	@Test
	public void shouldThrowException() throws Exception {
		PipelineCaptureInterceptor pci = new PipelineCaptureInterceptor("z///zxzz:\foojashfjh");
		try {
			pci.intercept(null, IDataFactory.create());
			fail();
		} catch (RuntimeException e) {
			// Pass
		}
	}
}
