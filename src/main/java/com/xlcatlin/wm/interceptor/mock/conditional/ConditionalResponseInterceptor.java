package com.xlcatlin.wm.interceptor.mock.conditional;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;
import com.xlcatlin.wm.aop.chainprocessor.InterceptResult;
import com.xlcatlin.wm.aop.chainprocessor.Interceptor;
import com.xlcatlin.wm.aop.matcher.MatchResult;
import com.xlcatlin.wm.aop.matcher.jexl.JexlIDataMatcher;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public class ConditionalResponseInterceptor implements Interceptor {

	private final JexlIDataMatcher evaluator;
	private final Map<String, IData> responses = new HashMap<String, IData>();
	private final boolean ignoreNoMatch;

	public ConditionalResponseInterceptor(InputStream responseXMLStream, boolean ignoreNoMatch) throws IOException, SAXException, ParserConfigurationException {
		this(new XMLResponseParser().parse(responseXMLStream), ignoreNoMatch);
	}

	public ConditionalResponseInterceptor(String responseXML, boolean ignoreNoMatch) throws IOException, SAXException, ParserConfigurationException {
		this(new XMLResponseParser().parse(responseXML), ignoreNoMatch);
	}

	public ConditionalResponseInterceptor(List<ConditionResponse> conditionResponses, boolean ignoreNoMatch) throws IOException {
		Map<String, String> exprs = new LinkedHashMap<String, String>();
		this.ignoreNoMatch = ignoreNoMatch;
		for (ConditionResponse cr : conditionResponses) {
			String sid = cr.getId();
			exprs.put(sid, cr.getExpression());
			responses.put(sid, new IDataXMLCoder().decodeFromBytes(cr.getResponse().getBytes()));
			System.out.println("]>]> Adding response id " + sid + " length " + cr.getResponse().length() + " for expression " + cr.getExpression());
		}
		evaluator = new JexlIDataMatcher(exprs);
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData idata) {
		MatchResult result = evaluator.match(idata);
		System.out.println("]>]> Evaluated " + result);
		if (result != null) {
			System.out.println("]>]> Merging response " + result.getId());
			IDataUtil.merge(responses.get(result.getId()), idata);
			return InterceptResult.TRUE;
		}
		if (ignoreNoMatch) {
			return InterceptResult.TRUE;
		}
		throw new RuntimeException("No conditions match pipeline state");
	}
}
