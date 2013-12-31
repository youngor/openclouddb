package org.hx.rainbow.web.action.sa;

import java.io.BufferedOutputStream;
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
@RequestMapping("/licensesAction")
public class LicensesAction {

	@RequestMapping("/download")
	public void download(HttpServletRequest request,
			HttpServletResponse response) {
		RainbowContext context = new RainbowContext();
		try {
			response.setContentType("application/x-msdownload;");
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String("licenses123.xml".getBytes("utf-8"), "ISO8859-1"));
			context.setService("licensesService");
			context.setMethod("query");
			context.addAttr("guid", request.getParameter("guid"));
			context = SoaManager.getInstance().invoke(context);
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			if(context.getRows().size() > 0){
				Map<String,Object> map = context.getRows().get(0);
				map.put("signature",map.get("signature").toString().replace("\n", ""));
				createLicenses(context.getRows().get(0),toClient);
			}
			toClient.flush();
			toClient.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createLicenses(Map<String, Object> map,OutputStream toClient) {
		try {
			String packageName = super.getClass().getPackage().getName();
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = this.getClass().getClassLoader();
			InputStream inputstate = classLoader
					.getResourceAsStream(packagePath + "/template/license.ftl");
			Template tempState = new Template("", new InputStreamReader(
					inputstate), new Configuration());
			Writer out = new OutputStreamWriter(toClient);
			tempState.process(map, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
