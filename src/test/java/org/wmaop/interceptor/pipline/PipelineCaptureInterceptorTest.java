package org.wmaop.interceptor.pipline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.wm.data.IDataFactory;

public class PipelineCaptureInterceptorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
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
	public void shouldUseDefaultExtension() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("target/foo"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("target/foo-1.xml")).thenReturn(baos);

		pci.intercept(null, IDataFactory.create());
		assertTrue(baos.toString().contains("IDataXMLCoder version="));
	}

	@Test
	public void shouldThrowException() throws Exception {
		thrown.expect(Exception.class); // actual error is non-visible junit ValidationError
		PipelineCaptureInterceptor pci = new PipelineCaptureInterceptor("z///zxzz:\foojashfjh");
		pci.intercept(null, IDataFactory.create());
	}
}
