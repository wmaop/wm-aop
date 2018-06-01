package org.wmaop.interceptor.delegating;

import java.util.Map;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.InterceptionException;
import org.wmaop.interceptor.BaseInterceptor;
import org.wmaop.util.pipeline.NameParser;

import com.webmethods.util.Pair;
import com.wm.app.b2b.server.Service;
import com.wm.data.IData;
import com.wm.data.IDataUtil;

public class FlowServiceDelegatingInterceptor extends BaseInterceptor {

	private String packageName;
	private String serviceName;

	public FlowServiceDelegatingInterceptor(String fqServiceName) {
		super("DelegatedFlowService:"+fqServiceName);
		Pair<String, String> parsedName = NameParser.parseFQServiceName(fqServiceName);
		this.packageName = parsedName.getFirst();
		this.serviceName = parsedName.getSecond();
	}

	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		invokeCount++;
		invokeDelegatedService(idata);
		return InterceptResult.TRUE;
	}
	
	private void invokeDelegatedService(IData idata) {
		try {
			IData output = Service.doInvoke(packageName, serviceName, idata);
			IDataUtil.merge(output, idata);
		} catch (Exception e) {
			throw new InterceptionException("Error while executing flow service " + packageName + ":" + serviceName, e);
		}
	}

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put(MAP_TYPE, "FlowServiceDelegatingInterceptor");
		am.put(MAP_NAME, super.name);
	}
}
