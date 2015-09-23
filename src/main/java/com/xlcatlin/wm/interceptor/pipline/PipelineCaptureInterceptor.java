package com.xlcatlin.wm.interceptor.pipline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.wm.data.IData;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class PipelineCaptureInterceptor implements Interceptor {

	private final String prefix;
	private final String suffix;
	private int fileCount;

	public PipelineCaptureInterceptor(String fileName) {
		int dotPos = fileName.lastIndexOf('.');
		if (dotPos == -1) {
			prefix = fileName;
			suffix = ".xml";
		} else {
			prefix = fileName.substring(0, dotPos);
			suffix = fileName.substring(dotPos);
		}
	}

	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		String fname = prefix + '-' + ++fileCount + suffix;
		try (OutputStream fos = getFileOutputStream(fname)) {
			new IDataXMLCoder().encode(fos, idata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return InterceptResult.TRUE;
	}

	OutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}
}
