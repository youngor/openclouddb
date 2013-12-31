package org.hx.rainbow.web.action.system;

import javax.servlet.http.HttpServletRequest;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.hx.rainbow.common.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.serializer.SerializerFeature;

@Controller
@RequestMapping("/orgAction")
public class OrgAction {
	
	@RequestMapping("/query")
	@ResponseBody
	public String query(RainbowContext context){
		context.setService("orgService");
		context.setMethod("query");
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	@RequestMapping("/queryTree")
	@ResponseBody
	public String queryTree(HttpServletRequest request){
		RainbowContext context = new RainbowContext();
		context.setService("orgService");
		context.setMethod("queryTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
	
	
	@RequestMapping("/queryComboxTree")
	@ResponseBody
	public String queryComboxTree(HttpServletRequest request){
		RainbowContext context = new RainbowContext();
		context.setService("orgService");
		context.setMethod("queryComboxTree");
		context.addAttr("id", request.getParameter("id"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
}
