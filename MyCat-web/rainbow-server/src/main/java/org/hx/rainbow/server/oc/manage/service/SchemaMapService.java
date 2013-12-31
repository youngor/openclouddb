package org.hx.rainbow.server.oc.manage.service;

import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SchemaMapService extends BaseService {
	private static final String NAMESPACE = "OCSCHEMAMAP";
	private static final String INSERT = "insert";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		try{
			for (Map<String,Object> row : context.getRows()) {
				row.put("tableName", row.get("name"));
				row.put("schemaName", context.getAttr("schemaName"));
				row.put("guid", new ObjectId().toString());
				getDao().insert(NAMESPACE, INSERT ,  row);
				context.setMsg("添加成功!");
			}
		}catch (Exception e) {
			context.setMsg("系统异常");
			return context;
		}
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
