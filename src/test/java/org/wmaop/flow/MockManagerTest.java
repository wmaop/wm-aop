package org.wmaop.flow;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.wmaop.flow.MockManager.ADVICE_ID;
import static org.wmaop.flow.MockManager.INTERCEPT_POINT;
import static org.wmaop.flow.MockManager.RESPONSE;
import static org.wmaop.flow.MockManager.SERVICE_NAME;

import org.junit.Test;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;
import com.wm.util.coder.IDataXMLCoder;

public class MockManagerTest {

	private static final String[][] PARAMS = new String[][] { { ADVICE_ID, "advid" }, { INTERCEPT_POINT, "before" }, { SERVICE_NAME, "foo:bar" } };

	@Test
	public void shouldVerifyMissingParameters() throws Exception {
		final MockManager mm = new MockManager();
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.registerFixedResponseMock(idata);
			}
		}, new String[][] { { ADVICE_ID, "advid" }, { INTERCEPT_POINT, "before" }, { RESPONSE, "invalidResponse" },
				{ SERVICE_NAME, "foo:bar" } });
	}

	@Test
	public void shouldCheckInvalidParameters() throws Exception {
		IData idata = IDataFactory.create();
		IDataCursor cursor = idata.getCursor();
		IDataUtil.put(cursor, ADVICE_ID, "id");
		IDataUtil.put(cursor, INTERCEPT_POINT, "before");
		IDataUtil.put(cursor, SERVICE_NAME, "foo:bar");

		IDataUtil.put(cursor, RESPONSE, "invalid data");
		try {
			new MockManager().registerFixedResponseMock(idata);
			fail();
		} catch (Exception e) {
			assertTrue(e.toString().contains("Unable to parse"));
		}
		IDataUtil.put(cursor, RESPONSE, new String(new IDataXMLCoder().encodeToBytes(IDataFactory.create())));

		IDataUtil.put(cursor, INTERCEPT_POINT, "Nothing");
		try {
			new MockManager().registerFixedResponseMock(idata);
			fail();
		} catch (Exception e) {
			assertTrue(e.toString().contains("interceptPoint NOTHING"));
		}
	}

	@Test
	public void shouldVerifyAssertingMissingParameters() throws Exception {
		final MockManager mm = new MockManager();
		testForMissingManadatory(new Callback() {
			public void action(IData idata) throws ServiceException {
				mm.registerFixedResponseMock(idata);
			}
		}, PARAMS);
	}

	private void testForMissingManadatory(Callback callback, String[][] params) throws Exception {
		for (int i = 0; i < params.length; i++) {
			IData idata = IDataFactory.create();
			addParams(idata, params, i);  // Miss out one at a time;
			try {
				callback.action(idata);
				fail("Missed mandatory " + params[i][0]);
			} catch (ServiceException e) {
				assertTrue(e.toString().contains(params[i][0] + " must exist"));
			}
		}
	}

	private void addParams(IData idata, String[][] params, int skipEntry) {
		IDataCursor idc = idata.getCursor();
		for (int j = 0; j < params.length; j++) {
			if (skipEntry == j)
				continue;
			IDataUtil.put(idc, params[j][0], params[j][1]);
		}
		idc.destroy();
	}

	interface Callback {
		void action(IData idata) throws ServiceException;
	}
	
}
