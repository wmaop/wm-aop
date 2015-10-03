package org.wmaop.flow;

import java.text.MessageFormat;

import org.apache.commons.lang.ArrayUtils;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public abstract class FlowManager {

	public void mandatory(IData pipeline, String message, String... params) throws ServiceException {
		IDataCursor pipelineCursor = pipeline.getCursor();
		try {
			for (String p : params) {
				Object o = IDataUtil.get(pipelineCursor, p);
				if (o == null || "".equals(o)) {
					MessageFormat mf = new MessageFormat(message);
					throw new ServiceException(mf.format(ArrayUtils.addAll(new Object[]{p}, params)));
				}
			}
		} finally {
			pipelineCursor.destroy();
		}
	
	}

	public  <T> void oneof(String message, T input, T... values) throws ServiceException {
		for (T v : values) {
			if (v.equals(input)) {
				return;
			}
		}
		MessageFormat mf = new MessageFormat(message);
		throw new ServiceException(mf.format(ArrayUtils.addAll(new Object[]{input}, values)));
	}
}
