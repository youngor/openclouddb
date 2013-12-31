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
@RequestMapping("/sysCodeAction")
public class SysCodeAction {
	
	@RequestMapping("/getCode")
	@ResponseBody
	public String query(HttpServletRequest request){
		RainbowContext context = new RainbowContext();
		context.setService("codeService");
		context.setMethod("getCode");
		context.addAttr("code", request.getParameter("code"));
		context = SoaManager.getInstance().invoke(context);
		return JsonUtil.getInstance().object2JSON(context.getRows(), SerializerFeature.WriteDateUseDateFormat);
	}
}
