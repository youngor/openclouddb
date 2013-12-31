package org.hx.rainbow.server.sys.power.service;

import java.util.Date;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.hx.rainbow.common.util.ObjectId;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class FavoritesService extends BaseService {
	private static final String NAMESPACE = "SYSTEMFAVORITES";
	private static final String COUNT = "count";
	private static final String CLEAR = "clear";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}
	
	public RainbowContext insert(RainbowContext context) {
		context.addAttr("loginId", RainbowSession.getLoginId());
		int count = this.getDao().count(NAMESPACE, COUNT,context.getAttr());
		if(count > 0){
			context.setSuccess(false);
			context.setMsg("该菜单已经被收藏！");
			return context;
		}
		context.addAttr("guid", new ObjectId().toString());
		context.addAttr("createTime", new Date());
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
		String pageCode = (String) context.getRow(0).get("pageCode");
		if(pageCode==null||pageCode.trim().isEmpty()){
			context.setMsg("移除失败！请指定要移除的菜单");
			context.setSuccess(false);
			return context;
		}
		super.delete(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}
	public RainbowContext clear(RainbowContext context) {
		try{
		context.addAttr("loginId", RainbowSession.getLoginId());
		this.getDao().delete(NAMESPACE, CLEAR, context.getAttr());
		}catch(Exception e){
			e.printStackTrace();
			context.setSuccess(false);
			throw new AppException("系统异常，清空失败!");
		}
		context.getAttr().clear();
		return context;
	}
}
