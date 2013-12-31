package org.hx.rainbow.server.oc.manage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class MycatService extends BaseService {
	private static final String NAMESPACE = "OCMYCAT";
	private static final String OCUSER = "OCUSER";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryMyCat(RainbowContext context) {
		super.query(context, NAMESPACE);
		Map<String,Object> server = new HashMap<String,Object>();
		server.put("paramList", context.getRows());
		super.query(context, OCUSER);
		server.put("userList", context.getRows());
		context.addAttr("server", server);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
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
