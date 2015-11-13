package org.wmaop.interceptor.mock.restful;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.wmaop.aop.chainprocessor.InterceptResult;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class RestDelegatingInterceptor implements org.wmaop.aop.chainprocessor.Interceptor {

	public static final String APPLICATION_XML = "application/xml";

	private final String destinationUrl;
	private final String serviceName;

	public RestDelegatingInterceptor(String serviceName, String destinationUrl) {
		this.serviceName = serviceName;
		this.destinationUrl = destinationUrl;
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		sendPost(idata);
		return InterceptResult.TRUE;
	}

	public String getName() {
		return serviceName;
	}
	
	private void sendPost(IData idata) {
		try {
			URL url = new URL(destinationUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", APPLICATION_XML);

			IDataXMLCoder idxc = new IDataXMLCoder();

			OutputStream os = conn.getOutputStream();
			idxc.encode(os, idata);
			os.flush();

			IDataUtil.merge(idxc.decode(conn.getInputStream()), idata);

			conn.disconnect();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
