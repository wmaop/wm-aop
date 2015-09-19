package com.xlcatlin.wm.aop;

import com.wm.data.IData;
import com.xlcatlin.wm.aop.pipeline.FlowPosition;

public interface PointCut {

	boolean isApplicable(FlowPosition pipelinePosition, IData idata);
}
