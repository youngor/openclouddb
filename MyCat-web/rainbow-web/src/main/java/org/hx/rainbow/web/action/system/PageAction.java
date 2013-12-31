package org.hx.rainbow.web.action.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hx.rainbow.web.model.DataGridParamData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pageAction")
public class PageAction {
	
	@RequestMapping(value = "/query",method = RequestMethod.GET)
	@ResponseBody
	public DataGridParamData query(ModelMap map){
		return null;
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{         
		return null;
	}
}
