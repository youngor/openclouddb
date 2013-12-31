package org.hx.rainbow.web.action.system;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.cas.ServiceProperties;
import org.hx.rainbow.common.context.RainbowProperties;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logoutAction")
public class LogoutAction {
	private static final String CAS_LOGOUT = "cas.service.logout";
	private static final String CLIENT_SERVICE = "client.service";
	
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException{
		 HttpSession session = request.getSession(false);
         if (session != null) {
             session.invalidate();
         }
		String loginUrl = (String)RainbowProperties.getProperties(CAS_LOGOUT);
		String serviceUrl = (String)RainbowProperties.getProperties(CLIENT_SERVICE) + "/login";
		if(serviceUrl == null || serviceUrl.length() == 0){
			serviceUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/login";
		}
		ServiceProperties sp = (ServiceProperties)SpringApplicationContext.getBean("serviceProperties");
		String redirectUrl = CommonUtils.constructRedirectUrl(loginUrl, sp.getServiceParameter(), serviceUrl, false, false);
		response.sendRedirect(redirectUrl);
	}
}
