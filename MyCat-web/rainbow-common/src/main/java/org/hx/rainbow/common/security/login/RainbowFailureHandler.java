package org.hx.rainbow.common.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

public class RainbowFailureHandler implements AuthenticationFailureHandler {
	protected final Log logger = LogFactory.getLog(getClass());
	private String defaultFailureUrl;
	private boolean forwardToDestination = false;
	
	public boolean isForwardToDestination() {
		return forwardToDestination;
	}

	public void setForwardToDestination(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}

	public String getDefaultFailureUrl() {
		return defaultFailureUrl;
	}

	private boolean allowSessionCreation = true;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public RainbowFailureHandler() {
	}

	public RainbowFailureHandler(String defaultFailureUrl) {
		setDefaultFailureUrl(defaultFailureUrl);
	}

	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		if (this.defaultFailureUrl == null) {
			this.logger.debug("No failure URL set, sending 401 Unauthorized error");

			response.sendError(401,"Authentication Failed: " + exception.getMessage());
		} else {
			saveException(request, exception);

			if (this.forwardToDestination) {
				this.logger.debug("Forwarding to " + this.defaultFailureUrl);
				request.getRequestDispatcher(this.defaultFailureUrl+exception.getMessage()).forward(request, response);
			} else {
				this.logger.debug("Redirecting to " + this.defaultFailureUrl);
				this.redirectStrategy.sendRedirect(request, response,this.defaultFailureUrl+exception.getMessage());
			}
		}
	}

	protected final void saveException(HttpServletRequest request,
			AuthenticationException exception) {
		if (this.forwardToDestination) {
			request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
		} else {
			HttpSession session = request.getSession(false);

			if ((session != null) || (this.allowSessionCreation))
				request.getSession().setAttribute(
						"SPRING_SECURITY_LAST_EXCEPTION", exception);
		}
	}

	public void setDefaultFailureUrl(String defaultFailureUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl), "'"
				+ defaultFailureUrl + "' is not a valid redirect URL");

		this.defaultFailureUrl = defaultFailureUrl;
	}

	

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return this.redirectStrategy;
	}

	protected boolean isAllowSessionCreation() {
		return this.allowSessionCreation;
	}

	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}

}
