package org.hx.rainbow.server.oc.manage.service;

import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class DatahostmapService extends BaseService {
	private static final String NAMESPACE = "OCDATAHOSTMAP";
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
			String name = (String)context.getAttr("name");
			for(Map<String,Object> data : context.getRows()){
				String host = (String)data.get("host");
				data.put("dataHost", name);
				data.put("host", host);
				data.put("guid", new ObjectId().toString());
				getDao().insert(NAMESPACE, INSERT,  data);
				context.setMsg("物理机绑定成功!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			context.setMsg("物理机绑定失败!,系统异常!");
			context.setSuccess(false);
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
