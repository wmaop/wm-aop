package org.wmaop.interceptor.pipline;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;
import com.wm.util.coder.IDataXMLCoder;

public class PipelineCaptureInterceptor extends BaseInterceptor {

	private final String prefix;
	private final String suffix;
	private int fileCount;

	public PipelineCaptureInterceptor(String fileName) {
		super("PipelineCapture-"+fileName);
		int dotPos = fileName.lastIndexOf('.');
		if (dotPos == -1) {
			prefix = fileName;
			suffix = ".xml";
		} else {
			prefix = fileName.substring(0, dotPos);
			suffix = fileName.substring(dotPos);
		}
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		try (OutputStream fos = getFileOutputStream(getFileName())) {
			new IDataXMLCoder().encode(fos, idata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return InterceptResult.TRUE;
	}

	private String getFileName() {
		return prefix + '-' + ++fileCount + suffix;
	}

	OutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put("type", "PipelineCaptureInterceptor");
		am.put("currentFile", getFileName());
	}
}
