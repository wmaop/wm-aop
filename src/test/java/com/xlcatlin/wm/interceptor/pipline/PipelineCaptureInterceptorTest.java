package com.xlcatlin.wm.interceptor.pipline;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class PipelineCaptureInterceptorTest {

	@Test
	public void test() throws Exception {
		PipelineCaptureInterceptor pci = spy(new PipelineCaptureInterceptor("foo.xml"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		when(pci.getFileOutputStream("foo-1.xml")).thenReturn(baos);

		pci.intercept(null, idata);
	}

}
