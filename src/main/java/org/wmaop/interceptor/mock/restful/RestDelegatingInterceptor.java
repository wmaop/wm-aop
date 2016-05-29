package org.wmaop.interceptor.mock.restful;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class RestDelegatingInterceptor extends BaseInterceptor {

	public static final String APPLICATION_XML = "application/xml";

	private final String destinationUrl;
	private final String serviceName;

	public RestDelegatingInterceptor(String serviceName, String destinationUrl) {
		super("Restful:"+serviceName);
		this.serviceName = serviceName;
		this.destinationUrl = destinationUrl;
	}

	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		sendPost(idata);
		return InterceptResult.TRUE;
	}

	@Override
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

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put("type", "RestDelegatingInterceptor");
		am.put("destinationUrl", destinationUrl);
	}
}
