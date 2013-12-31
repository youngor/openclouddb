package org.hx.rainbow.common.security.login;

import org.springframework.security.core.GrantedAuthority;

public class RainbowGrantedAuthority implements GrantedAuthority{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2028476867141688641L;

	@Override
	public String getAuthority() {
		return "ROLE_LOGIN";
	}
	
}
