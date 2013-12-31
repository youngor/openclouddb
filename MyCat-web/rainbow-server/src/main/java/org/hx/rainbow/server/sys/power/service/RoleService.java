package org.hx.rainbow.server.sys.power.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class RoleService extends BaseService {
	private static final String NAMESPACE = "SYSROLE";
	private static final String[] AUTHS = {"deleteAuthButton","deleteAuthresouce","deleteAuthservice","deleteAuthuser"};

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
			List<Map<String,Object>> roleTypeList = context.getRows();
			String orgId = (String)context.getAttr("orgId");
			String orgName = (String)context.getAttr("orgName");
			for(Map<String,Object> map : roleTypeList){
				map.put("roleCode",orgId+"_" + map.get("roleTypeCode"));
				map.put("roleName",orgName+"_" + map.get("roleTypeName"));
				map.put("guid", new ObjectId().toString());
				map.put("createTime", new Date());
				map.put("orgCode", orgId);
				context.addAttr("createUser", RainbowSession.getUserName());
				getDao().insert(NAMESPACE, "insert", map);
			}
			context.clearRows();
			context.clearAttr();
			context.setMsg("新增成功!");
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("新增失败!");
		}
		
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		try{
			List<Map<String,Object>> roleNameList = context.getRows();
			for(Map<String,Object> map : roleNameList){
				map.put("roleName",map.get("roleName"));
				map.put("guid", map.get("guid"));
				getDao().update(NAMESPACE, "update", map);
			}
			context.setMsg("成功修改,"+roleNameList.size()+"条记录!");
			context.clearRows();
			context.clearAttr();
		}catch (Exception e) {
			e.printStackTrace();
			throw new AppException("修改失败!");
		}
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		super.delete(context, NAMESPACE);
		for(Map<String,Object> map : context.getRows()){				
			for(String auth : AUTHS){
				getDao().delete(NAMESPACE, auth, map);
			}
		}
		return context;
	}
}
