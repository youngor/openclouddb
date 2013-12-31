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
public class RoletypeService extends BaseService {
	private static final String NAMESPACE = "SYSROLETYPE";
	private static final String QUERY_NOTINORG = "queryNotInOrg";
	private static final String COUNT_NOTINORG = "countNotInOrg";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryNotInOrg(RainbowContext context) {
		if(context.getAttr("orgCode") != null){
			super.queryByPage(context, NAMESPACE,QUERY_NOTINORG,COUNT_NOTINORG);
		}
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
		context.addAttr("createUser", RainbowSession.getUserName());
		String userId = RainbowSession.getLoginId();
		context.addAttr("createUser", userId);
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
