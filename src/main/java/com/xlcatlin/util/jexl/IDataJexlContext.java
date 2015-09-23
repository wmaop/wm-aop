package com.xlcatlin.util.jexl;

import org.apache.commons.jexl2.JexlContext;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class IDataJexlContext implements JexlContext {

	private final IData idata;

	public IDataJexlContext(IData idata) {
		this.idata = idata;
	}

	public Object get(String name) {
		IDataCursor cursor = idata.getCursor();
		Object o = IDataUtil.get(cursor, name.replace('_', ':')); // Use better
																	// escape
		cursor.destroy();
		Object ret = o;
		try {
			if (o instanceof IData[]) {
				IData[] idataArr = ((IData[]) o);
				IDataJexlContext[] arr = new IDataJexlContext[idataArr.length];
				for (int i = 0; i < idataArr.length; i++) {
					arr[i] = new IDataJexlContext(idataArr[i]);
				}
				ret = arr;
			} else if (o instanceof IData) {
				ret = new IDataJexlContext((IData) o);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
		return ret;
	}

	public void set(String name, Object value) {
		// NOOP
	}

	public boolean has(String name) {
		IDataCursor cursor = idata.getCursor();
		Object o = IDataUtil.get(cursor, name.replace('_', ':'));
		cursor.destroy();
		return o != null;
	}

}
