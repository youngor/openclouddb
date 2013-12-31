package org.hx.rainbow.server.sys.power.service;

import java.util.Date;
import java.util.HashMap;
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
public class OrgService extends BaseService {
	private static final String NAMESPACE = "SYSORG";
	private static final String QUERYBYPARENT = "queryByParent";
	private static final String QUERYCOMBOXTREE = "queryComboxTree";
	private static final String QUERYTREE = "queryTree";
	private static final String QUERY_GRIDTREE = "queryGridTree";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByParent(RainbowContext context) {
		super.query(context, NAMESPACE, QUERYBYPARENT);
		return context;
	}
	
	public RainbowContext queryComboxTree(RainbowContext context) {
		super.query(context, NAMESPACE, QUERYCOMBOXTREE);
		return context;
	}
	
	public RainbowContext queryTree(RainbowContext context) {
		super.query(context, NAMESPACE, QUERYTREE);
		return context;
	}
	
	public RainbowContext queryGridTree(RainbowContext context) {
		super.query(context, NAMESPACE, QUERY_GRIDTREE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		changeParent(context);
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createUser", RainbowSession.getUserName());
		context.addAttr("createTime", new Date());
		super.insert(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	public RainbowContext update(RainbowContext context) {
		changeParent(context);
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext delete(RainbowContext context) {
		List<Map<String,Object>> list = context.getRows();
		if(list.size() > 0){
			String code = (String)list.get(0).get("orgCode");
			queryChilds(NAMESPACE,QUERY_GRIDTREE,code,context.getRows());
		    super.delete(context, NAMESPACE);
		}
		context.getRows().clear();
		return context;
	}
	
	private void changeParent(RainbowContext context){
		String parentCode = (String)context.getAttr("parentCode");
		if(parentCode != null && parentCode.length() > 0){
			Map<String,Object> paramData = new HashMap<String,Object>();
			paramData.put("orgCode", parentCode);
			paramData.put("state", "closed");
			super.getDao().update(NAMESPACE, "updateState", paramData);
		}
	}
	
	public void queryChilds(String namespace,String statement,String code,List<Map<String,Object>> list){
		if(code == null || code.isEmpty()){
			return;
		}
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("id", code);
		List<Map<String,Object>> dataList = getDao().query(namespace, statement,  paramData);
		if(dataList.size() > 0){
			list.addAll(dataList);
			for(Map<String,Object> map : dataList){
				String parentCode = (String)map.get("orgCode");
				queryChilds(namespace,statement,parentCode,list);
			}
		}
	}
}
