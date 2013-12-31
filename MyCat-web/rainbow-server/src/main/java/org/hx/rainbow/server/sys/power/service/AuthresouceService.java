package org.hx.rainbow.server.sys.power.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AuthresouceService extends BaseService {
	private static final String NAMESPACE = "SYSAUTHRESOUCE";
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
			String roleGuid = (String)context.getAttr("roleGuid");
			List<Map<String,Object>> list = getDao().query(NAMESPACE, "query", context.getAttr());
			List<String> resourceCodes = listMap2Str(list);
			String roleCode = (String)context.getAttr("roleCode");
			for(Map<String,Object> data : context.getRows()){
				String resourceCode = (String)data.get("id");
				if(!resourceCodes.contains(resourceCode)){
					data.put("roleGuid", roleGuid);
					data.put("roleCode", roleCode);
					data.put("resourceGuid", data.get("text"));
					data.put("resourceCode", data.get("id"));
					data.put("guid", new ObjectId().toString());
					data.put("createTime", new Date());
					data.put("createUser", RainbowSession.getUserName());
					getDao().insert(NAMESPACE, INSERT,  data);
				}
			}
			context.setMsg("资源授权成功!");
		}catch (Exception e) {
			context.setMsg("资源授权失败!,系统异常!");
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
	
	private List<String> listMap2Str(List<Map<String,Object>> listMap){
		List<String> listStr = new ArrayList<String>();
		for(Map<String,Object> map : listMap){
			listStr.add((String)map.get("resourceCode"));
		}
		return listStr;
	}
}
