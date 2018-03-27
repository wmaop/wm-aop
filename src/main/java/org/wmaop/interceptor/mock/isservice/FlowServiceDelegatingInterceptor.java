package org.wmaop.interceptor.mock.isservice;

import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.InterceptionException;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.app.b2b.server.Service;

public class FlowServiceDelegatingInterceptor extends BaseInterceptor {

	public static final String MAP_INTERFACE_NAME = "ifcname";

	private final String ifcname;
	private final String serviceName;

	public FlowServiceDelegatingInterceptor(String ifcname, String serviceName) {
		super("IntegrationServerService:"+ifcname+":"+serviceName);
		this.serviceName = serviceName;
		this.ifcname = ifcname;
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
			IData output = Service.doInvoke(ifcname, serviceName, idata);
			IDataUtil.merge(output, idata);
		} catch (Exception e) {
			throw new InterceptionException("Error while executing flow service " +
					ifcname + ":" + serviceName, e);
		}
	}

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put(MAP_TYPE, "FlowServiceDelegatingInterceptor");
		am.put(MAP_INTERFACE_NAME, ifcname);
	}
}
