package org.hx.rainbow.web.action.oc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
@RequestMapping("/myCatAction")
public class MyCatAction {
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/createMyCat")
	public void createSchema(HttpServletRequest request,HttpServletResponse response){
		RainbowContext context = new RainbowContext();
		try{
			response.setContentType("application/x-msdownload;");
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("server.xml".getBytes("utf-8"), "ISO8859-1"));
			context.setService("mycatService");
			context.setMethod("queryMyCat");
			context = SoaManager.getInstance().invoke(context);
			Map<String,Object> params=(Map<String,Object>)context.getAttr("server");
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			createSchema(params,toClient);
			toClient.flush();
			toClient.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void createSchema(Map<String, Object> map,OutputStream toClient) {
		Writer out=null;
		try {
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath + "/template/server.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate), new Configuration());
			out = new OutputStreamWriter(toClient);
			tempState.process(map, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
