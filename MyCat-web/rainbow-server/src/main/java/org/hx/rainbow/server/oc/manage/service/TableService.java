package org.hx.rainbow.server.oc.manage.service;

import java.util.Date;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class TableService extends BaseService {
	private static final String NAMESPACE = "OCTABLE";
	private static final String QUERY_TREE = "queryTree";
	private static final String QUERY_COMBOXTREE = "queryComboxTree";
	private static final String QUERYFORSCHEMAMAP = "queryForSchemaMap";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	public RainbowContext queryTree(RainbowContext context) {
		super.query(context, NAMESPACE,QUERY_TREE);
		return context;
	}
	public RainbowContext queryComboxTree(RainbowContext context) {
		super.query(context, NAMESPACE,QUERY_COMBOXTREE);
		return context;
	}
	public RainbowContext queryForSchemaMap(RainbowContext context) {
		String schemaName = (String) context.getAttr("schemaName");
		if(schemaName == null || schemaName.trim().isEmpty()){
			context.setMsg("请选择逻辑库");
			return context;
		}
		super.query(context, NAMESPACE,QUERYFORSCHEMAMAP);
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
