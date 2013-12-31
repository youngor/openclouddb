package org.hx.rainbow.common.security.login;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class RainbowUser implements UserDetails{
	private Map<String,Object> sessionData;
	private String password;
	private String username;
	public RainbowUser(String password,String username,Map<String,Object> sessionData){
		this.password = password;
		this.username = username;
		this.sessionData = sessionData;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7417311584808031585L;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("ROLE_USER");
	}

	@Override
	public String getPassword() {
		
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean equals(Object object) {
		if (object instanceof RainbowUser) {
			if (this.username.equals(((RainbowUser) object).getUsername()))
				return true;
		}
		return false;
	}
	
    public int hashCode(){     
        return this.username.hashCode();     
    }

	public Map<String, Object> getSessionData() {
		return sessionData;
	}

	public void setSessionData(Map<String, Object> sessionData) {
		this.sessionData = sessionData;
	}
    

}
