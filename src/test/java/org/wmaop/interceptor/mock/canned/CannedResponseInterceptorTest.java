package org.wmaop.interceptor.mock.canned;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;
import org.mockito.Mockito;
import org.wmaop.aop.chainprocessor.InterceptResult;
import org.wmaop.aop.pipeline.FlowPosition;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.data.ISMemDataImpl;

public class CannedResponseInterceptorTest {

	private String A_IDATA = "<IDataXMLCoder version=\"1.0\"><record javaclass=\"com.wm.data.ISMemDataImpl\"><value name=\"akey\">avalue</value></record></IDataXMLCoder>";
	
	@Test
	public void shouldLoadFromStream() throws IOException {
		IData pipeline = getPipelineIData();
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		ByteArrayInputStream bais = new ByteArrayInputStream(A_IDATA.getBytes());
		InterceptResult ir = new CannedResponseInterceptor(bais).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
	}
	
	@Test
	public void shouldLoadFromString() throws IOException {
		IData pipeline = getPipelineIData();
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor(A_IDATA).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
	}

	@Test
	public void shouldLoadFromIData() {
		IData pipeline = getPipelineIData();
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor(getAIData()).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
	}
	private IData getPipelineIData() {
		IData pipeline = new ISMemDataImpl();
		IDataCursor idc = pipeline.getCursor();
		IDataUtil.put(idc, "bkey", "bvalue");
		idc.destroy();
		return pipeline;
	}
	private IData getAIData() {
		IData idata = new ISMemDataImpl();
		IDataCursor idc = idata.getCursor();
		IDataUtil.put(idc, "akey", "avalue");
		idc.destroy();
		return idata;
	}
	private void verifyMerged(IData pipeline, InterceptResult ir) {
		assertTrue(ir.hasIntercepted());
		IDataCursor pc = pipeline.getCursor();
		assertEquals(2, IDataUtil.size(pc));
		assertEquals("avalue", IDataUtil.getString(pc, "akey"));
		assertEquals("bvalue", IDataUtil.getString(pc, "bkey"));
	}

	@Test
	public void shouldNotChange() throws IOException {
		IData pipeline = getPipelineIData();
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor((IData)null).intercept(flowPosition , pipeline);
		assertTrue(ir.hasIntercepted());
		IDataCursor pc = pipeline.getCursor();
		assertEquals(1, IDataUtil.size(pc));
		pc.destroy();
		
	}
}
