package org.hx.rainbow.common.web.listener;

import javax.servlet.http.HttpSessionEvent;

import org.springframework.security.web.session.HttpSessionEventPublisher;

public class RainbowSessionListener extends HttpSessionEventPublisher {


	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		super.sessionDestroyed(event);
		//new RainbowSidHttpSession(event.getSession().getId()).invalidate();
	}


}
