package org.hx.rainbow.server.sys.power.service;

import java.util.Date;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class CodeService extends BaseService {
	private static final String NAMESPACE = "SYSCODE";
	private static final String GETCODE = "getCode";
	private static final String QUERY_COMBOX = "queryCombox";
	private static final String COUNT_COMBOX = "countCombox";
	public RainbowContext getCode(RainbowContext context) {		
		try{
			super.query(context, NAMESPACE, GETCODE);
		}catch (Exception e) {
			context.setSuccess(false);
		}
		return context;
	}
	
	
	public RainbowContext queryCombox(RainbowContext context) {		
		try{
			super.queryCombox(context, NAMESPACE,QUERY_COMBOX,COUNT_COMBOX);
		}catch (Exception e) {
			context.setSuccess(false);
		}
		return context;
	}

	public RainbowContext query(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}

	public RainbowContext getPage(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}

	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		context.addAttr("createUser", RainbowSession.getUserName());
		super.insert(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}

	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}

	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
}
