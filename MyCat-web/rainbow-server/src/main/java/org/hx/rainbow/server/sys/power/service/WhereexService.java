package org.hx.rainbow.server.sys.power.service;
import java.util.Date;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class WhereexService extends BaseService {
	private static final String NAMESPACE = "SYSWHEREEX";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		for (Map<String, Object> row : context.getRows()) {
			context.setAttr(row);
			if(row.get("valueType") == null){
				row.put("valueType", "text");
			}
			context.addAttr("guid", new ObjectId().toString());
			context.addAttr("createTime", new Date());
			context.addAttr("createUser", RainbowSession.getUserName());
			super.insert(context, NAMESPACE);
			context.getAttr().clear();
		}
		return context;
	}
	
	public RainbowContext insertBatch(RainbowContext context) {
		for (Map<String, Object> row : context.getRows()) {
			context.setAttr(row);
			context.addAttr("guid", new ObjectId().toString());
			context.addAttr("createTime", new Date());
			context.addAttr("createUser", RainbowSession.getUserName());
			context.addAttr("whereName", context.getAttr("whereName"));
			context.addAttr("whereCode", context.getAttr("whereCode"));
			super.insert(context, NAMESPACE);
			context.getAttr().clear();
		}
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		try{
			for (Map<String, Object> row : context.getRows()) {
				context.setAttr(row);
				super.update(context, NAMESPACE);
				context.getAttr().clear();
			}
			context.setMsg("成功修改" + context.getRows().size() + "条记录!");
		}catch (Exception e) {
			context.setSuccess(false);
			context.setMsg("修改失败,系统异常!");
		}
		return context;
	}
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
}
