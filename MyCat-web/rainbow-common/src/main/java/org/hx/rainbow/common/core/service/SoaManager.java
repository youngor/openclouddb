package org.hx.rainbow.common.core.service;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.exception.SysException;

public class SoaManager {

	private SoaInvoker soaInvoker;
	
	private volatile static SoaManager soaManager;
	
	private SoaManager(){}
	
	public static SoaManager getInstance(){
		if(soaManager == null){
			synchronized (SoaManager.class) {
				if(soaManager == null){
					soaManager = new SoaManager();
				}
			}
		}
		return soaManager;
	}
	

	public  RainbowContext invoke(RainbowContext context) {
		return callTx(context, 0);
	}

	public  RainbowContext call(RainbowContext context) {

		SoaInvoker SoaInvoker = (SoaInvoker) SpringApplicationContext
				.getBean("soaInvoker");
		return SoaInvoker.invoke(context);
	}

	public  RainbowContext invokeNoTx(RainbowContext context) {
		SoaInvoker SoaInvoker = (SoaInvoker) SpringApplicationContext
				.getBean("soaInvoker");
		return SoaInvoker.invoke(context);
	}

	public  RainbowContext callNoTx(RainbowContext context) {
		return callTx(context, 4);
	}

	public  RainbowContext callNewTx(RainbowContext context) {
		return callTx(context, 3);
	}

	private  RainbowContext callTx(RainbowContext context, int txType) {
		
		if (context == null) {
			throw new SysException("rainbowSoa: Service invoker is error");
		}
		if (context.getService() == null) {
			throw new SysException("rainbowSoa: Service is null !!");
		}
		if(context.getMethod() == null){
			throw new SysException("rainbowSoa: Service's method is null !!");
		}
		if(this.soaInvoker == null){
			this.soaInvoker = (SoaInvoker)SpringApplicationContext.getBean("soaInvoker");
		}
		
		RainbowContext info = new RainbowContext();
		try {
			context.addAttr("transactionType", Integer.valueOf(txType));
			info = this.soaInvoker.invoke(context);
		} catch (Exception e) {
			if ((e instanceof AppException)) {
				throw ((AppException) e);
			}else{
				throw new SysException("rainbowSoa: Service invoker is error!"+e.getMessage());
			}
		}

		return info;
	}

	public  SoaInvoker getSoaInvoker() {
		return soaInvoker;
	}

	public void setSoaInvoker(SoaInvoker soaInvoker) {
		this.soaInvoker = soaInvoker;
	}
}
