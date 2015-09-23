package com.xlcatlin.wm.interceptor.mock.conditional;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class ConditionalResponseInterceptor implements Interceptor {

	private static final Logger logger = Logger.getLogger(ConditionalResponseInterceptor.class);

	private final JexlIDataMatcher evaluator;
	private final Map<String, IData> responses = new HashMap<String, IData>();
	private final IData defaultResponse;
	private final String defaultId;
	private final boolean ignoreNoMatch;

	public ConditionalResponseInterceptor(List<ConditionResponse> conditionResponses, ConditionResponse dr, boolean ignoreNoMatch) throws IOException {
		Map<String, String> exprs = new LinkedHashMap<String, String>();
		this.ignoreNoMatch = ignoreNoMatch;
		for (ConditionResponse cr : conditionResponses) {
			String sid = cr.getId();
			exprs.put(sid, cr.getExpression());
			responses.put(sid, new IDataXMLCoder().decodeFromBytes(cr.getResponse().getBytes()));
			logger.info("]>]> Adding response id " + sid + " length " + cr.getResponse().length() + " for expression " + cr.getExpression());
		}
		if (dr != null && dr.getResponse() != null) {
			defaultResponse = new IDataXMLCoder().decodeFromBytes(dr.getResponse().getBytes());
			defaultId = dr.getId();
		} else {
			defaultId = null;
			defaultResponse = null;
		}
		evaluator = new JexlIDataMatcher(exprs);
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		MatchResult result = evaluator.match(idata);
		logger.info("]>]> Evaluated " + result);
		if (result != null) {
			logger.info("]>]> Merging response " + result.getId());
			IDataUtil.merge(responses.get(result.getId()), idata);
			return InterceptResult.TRUE;
		} else if (defaultResponse != null) {
			logger.info("]>]> Merging default response " + defaultId);
			IDataUtil.merge(defaultResponse, idata);
			return InterceptResult.TRUE;
		}
		if (ignoreNoMatch) {
			return InterceptResult.TRUE;
		}
		throw new RuntimeException("No conditions match pipeline state");
	}
}
