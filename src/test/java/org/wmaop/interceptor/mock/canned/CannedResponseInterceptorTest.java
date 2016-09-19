package org.wmaop.interceptor.mock.canned;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;
import org.wmaop.aop.interceptor.FlowPosition;
import org.wmaop.aop.interceptor.InterceptResult;
import org.wmaop.interceptor.mock.canned.CannedResponseInterceptor.ResponseSequence;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.data.ISMemDataImpl;

public class CannedResponseInterceptorTest {

	private String A_IDATA = "<IDataXMLCoder version=\"1.0\"><record javaclass=\"com.wm.data.ISMemDataImpl\"><value name=\"akey\">avalue</value></record></IDataXMLCoder>";
	
	@Test
	public void shouldLoadFromStream() throws IOException {
		IData pipeline = getIData(new String[][]{{"bkey", "bvalue"}});
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		ByteArrayInputStream bais = new ByteArrayInputStream(A_IDATA.getBytes());
		InterceptResult ir = new CannedResponseInterceptor(bais).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
	}
	
	@Test
	public void shouldLoadFromString() throws IOException {
		IData pipeline = getIData(new String[][]{{"bkey", "bvalue"}});
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor(A_IDATA).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
	}

	@Test
	public void shouldLoadFromIData() {
		IData pipeline = getIData(new String[][]{{"bkey", "bvalue"}});
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor(getIData(new String[][]{{"akey", "avalue"}})).intercept(flowPosition , pipeline);
		verifyMerged(pipeline, ir);
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
		IData pipeline = getIData(new String[][]{{"bkey", "bvalue"}});
		FlowPosition flowPosition = Mockito.mock(FlowPosition.class);
		InterceptResult ir = new CannedResponseInterceptor((IData)null).intercept(flowPosition , pipeline);
		assertTrue(ir.hasIntercepted());
		IDataCursor pc = pipeline.getCursor();
		assertEquals(1, IDataUtil.size(pc));
		pc.destroy();
	}
	
	IData getIData(String[][] data) {
		IData idata = new ISMemDataImpl();
		IDataCursor idc = idata.getCursor();
		for (String[] kv : data) {
			IDataUtil.put(idc, kv[0], kv[1]);
		}
		idc.destroy();
		return idata;
	}
	
	@Test
	public void shouldReturnSequential() {
		CannedResponseInterceptor cri = new CannedResponseInterceptor(ResponseSequence.SEQUENTIAL, getIData(new String[][]{{"akey", "avalue"}}), getIData(new String[][]{{"bkey", "bvalue"}}), getIData(new String[][]{{"ckey", "cvalue"}}));
		assertEquals("avalue", IDataUtil.get(cri.getResponse().getCursor(), "akey"));
		assertEquals("bvalue", IDataUtil.get(cri.getResponse().getCursor(), "bkey"));
		assertEquals("cvalue", IDataUtil.get(cri.getResponse().getCursor(), "ckey"));
		assertEquals("avalue", IDataUtil.get(cri.getResponse().getCursor(), "akey"));
		assertEquals("bvalue", IDataUtil.get(cri.getResponse().getCursor(), "bkey"));
		assertEquals("cvalue", IDataUtil.get(cri.getResponse().getCursor(), "ckey"));
	}

	@Test
	public void shouldReturnSequentialFromIDataString() throws IOException {
		List<String> lst = Arrays.asList("<IDataXMLCoder version=\"1.0\"><record javaclass=\"com.wm.data.ISMemDataImpl\"><value name=\"option\">a</value></record></IDataXMLCoder>","<IDataXMLCoder version=\"1.0\"><record javaclass=\"com.wm.data.ISMemDataImpl\"><value name=\"option\">b</value></record></IDataXMLCoder>");
		CannedResponseInterceptor cri = new CannedResponseInterceptor(ResponseSequence.SEQUENTIAL, lst);
		assertEquals("a", IDataUtil.get(cri.getResponse().getCursor(), "option"));
		assertEquals("b", IDataUtil.get(cri.getResponse().getCursor(), "option"));
		assertEquals("a", IDataUtil.get(cri.getResponse().getCursor(), "option"));
	}
	
	@Test
	public void shouldReturnRandom() {
		CannedResponseInterceptor cri = new CannedResponseInterceptor(ResponseSequence.RANDOM, getIData(new String[][]{{"akey", "avalue"}}), getIData(new String[][]{{"bkey", "bvalue"}}), getIData(new String[][]{{"ckey", "cvalue"}}));
		
		Set<String> keys = new HashSet<String>();
		for (int i = 0; i < 20; i++) {
			IDataCursor cursor = cri.getResponse().getCursor();
			cursor.next();
			keys.add(cursor.getKey());
		}
		assertTrue(keys.contains("akey"));
		assertTrue(keys.contains("bkey"));
		assertTrue(keys.contains("ckey"));
	}
}

