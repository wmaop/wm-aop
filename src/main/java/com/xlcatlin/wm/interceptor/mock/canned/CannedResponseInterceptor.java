package com.xlcatlin.wm.interceptor.mock.canned;

import java.io.IOException;
import java.io.InputStream;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class CannedResponseInterceptor implements Interceptor {

	private final IData cannedIdata;

	public CannedResponseInterceptor(String idataXml) throws IOException {
		this(new IDataXMLCoder().decodeFromBytes(idataXml.getBytes()));
	}

	public CannedResponseInterceptor(InputStream idataXmlStream) throws IOException  {
		this(new IDataXMLCoder().decode(idataXmlStream));
	}

	public CannedResponseInterceptor(IData idata) {
		cannedIdata = idata;
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData pipeline) {
		if (cannedIdata != null) {
			IDataUtil.merge(cannedIdata, pipeline);
		}
		return InterceptResult.TRUE;
	}

}
