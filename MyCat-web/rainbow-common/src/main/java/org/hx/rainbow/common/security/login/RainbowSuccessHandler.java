package org.hx.rainbow.common.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hx.rainbow.common.web.session.RainbowSession;
import org.hx.rainbow.common.web.session.ThreadConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

public class RainbowSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	  protected final Log logger = LogFactory.getLog(getClass());

	   
	  private RequestCache requestCache = new HttpSessionRequestCache();
	  
	  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	    throws ServletException, IOException
	  {
		  	RainbowUser user = (RainbowUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_USER, user);
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_USERNAME, user.getSessionData().get("name"));
		    request.getSession().setAttribute(ThreadConstants.RAINBOW_LOGINID, user.getUsername());
		  	RainbowSession.web2Service(request);
		  	SavedRequest savedRequest = requestCache.getRequest(request, response);

	        if (savedRequest == null) {
	            super.onAuthenticationSuccess(request, response, authentication);

	            return;
	        }
	        String targetUrlParameter = getTargetUrlParameter();
	        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
	            requestCache.removeRequest(request, response);
	            super.onAuthenticationSuccess(request, response, authentication);

	            return;
	        }

	        clearAuthenticationAttributes(request);

	        // Use the DefaultSavedRequest URL
	        String targetUrl = savedRequest.getRedirectUrl();
	        logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
	        getRedirectStrategy().sendRedirect(request, response, targetUrl);
	  }
}
