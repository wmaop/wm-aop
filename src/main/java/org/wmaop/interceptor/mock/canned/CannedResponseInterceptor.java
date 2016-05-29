package org.wmaop.interceptor.mock.canned;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.BaseInterceptor;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class CannedResponseInterceptor extends BaseInterceptor {

	private static final String CANNED_RESPONSE_PREFIX = "CannedResponse:";

	public enum ResponseSequence {
		SEQUENTIAL, RANDOM;
	}

	private final List<IData> cannedIdata;
	private final ResponseSequence sequence;
	private int seqCount = 0;
	private Random random = new Random();

	public CannedResponseInterceptor(String idataXml) throws IOException {
		this(new IDataXMLCoder().decodeFromBytes(idataXml.getBytes()));
	}

	public CannedResponseInterceptor(ResponseSequence seq, List<String> list) throws IOException {
		super(CANNED_RESPONSE_PREFIX);
		sequence = seq;
		cannedIdata = new ArrayList<>();
		for (String idataXml : list) {
			cannedIdata.add(new IDataXMLCoder().decodeFromBytes(idataXml.getBytes()));
		}
	}

	public CannedResponseInterceptor(InputStream idataXmlStream) throws IOException {
		this(new IDataXMLCoder().decode(idataXmlStream));
	}

	public CannedResponseInterceptor(IData idata) {
		super(CANNED_RESPONSE_PREFIX);
		cannedIdata = Arrays.asList(idata);
		sequence = ResponseSequence.SEQUENTIAL;
	}

	public CannedResponseInterceptor(ResponseSequence seq, IData... idata) {
		super(CANNED_RESPONSE_PREFIX);
		cannedIdata = Arrays.asList(idata);
		sequence = seq;
	}

	@Override
	public InterceptResult intercept(FlowPosition flowPosition, IData pipeline) {
		invokeCount++;
		if (cannedIdata != null) {
			IDataUtil.merge(getResponse(), pipeline);
		}
		return InterceptResult.TRUE;
	}

	protected IData getResponse() {
		IData response;
		if (sequence == ResponseSequence.RANDOM) {
			response = cannedIdata.get(random.nextInt(cannedIdata.size()));
		} else {
			response = cannedIdata.get(seqCount++ % cannedIdata.size());
		}
		return response;
	}

	@Override
	public String toString() {
		return "CannedResponseInterceptor";
	}

	@Override
	protected void addMap(Map<String, Object> am) {
		am.put("type", "CannedResponseInterceptor");
		am.put("responseSequence", sequence.toString());
		int i = 0;
		for (IData idata : cannedIdata) {
			am.put("response" + i++, idata);
		}
	}

}
