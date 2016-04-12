package org.wmaop.interceptor.mock.canned;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.aop.interceptor.Interceptor;

import com.wm.data.IData;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class CannedResponseInterceptor implements Interceptor {

	public enum ResponseSequence{ 
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
		sequence = seq;
		cannedIdata = new ArrayList<>();
		for (String idataXml : list) {
			cannedIdata.add(new IDataXMLCoder().decodeFromBytes(idataXml.getBytes()));
		}
	}

	public CannedResponseInterceptor(InputStream idataXmlStream) throws IOException  {
		this(new IDataXMLCoder().decode(idataXmlStream));
	}

	public CannedResponseInterceptor(IData idata) {
		cannedIdata = Arrays.asList(idata);
		sequence = ResponseSequence.SEQUENTIAL;
	}

	public CannedResponseInterceptor(ResponseSequence seq, IData... idata) {
		cannedIdata = Arrays.asList(idata);
		sequence = seq;
	}

	public InterceptResult intercept(FlowPosition flowPosition, IData pipeline) {
		if (cannedIdata != null) {
			IDataUtil.merge(getResponse(), pipeline);
		}
		return InterceptResult.TRUE;
	}

	protected IData getResponse() {
		switch(sequence) {
			case RANDOM:
				return cannedIdata.get(random.nextInt(cannedIdata.size()));
			default:
				return cannedIdata.get(seqCount++%cannedIdata.size());
			
		}
	}
	@Override
	public String toString() {
		return "CannedResponseInterceptor";
	}

}
