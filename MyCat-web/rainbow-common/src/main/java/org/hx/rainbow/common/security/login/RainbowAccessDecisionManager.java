package org.hx.rainbow.common.security.login;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

public class RainbowAccessDecisionManager implements AccessDecisionManager
{

	@Override
	public void decide(Authentication arg0, Object arg1,
			Collection<ConfigAttribute> arg2) throws AccessDeniedException,
			InsufficientAuthenticationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean supports(ConfigAttribute arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	 
}
