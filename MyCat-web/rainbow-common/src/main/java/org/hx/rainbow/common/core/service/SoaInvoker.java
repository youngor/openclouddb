package org.hx.rainbow.common.core.service;

import org.hx.rainbow.common.context.RainbowContext;

public abstract interface SoaInvoker {
	public abstract RainbowContext invoke(RainbowContext context);
}
