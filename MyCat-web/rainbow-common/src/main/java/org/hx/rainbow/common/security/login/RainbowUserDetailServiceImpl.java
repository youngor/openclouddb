package org.hx.rainbow.common.security.login;

import java.util.HashMap;
import java.util.Map;

import org.hx.rainbow.common.dao.Dao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class RainbowUserDetailServiceImpl implements UserDetailsService{
	private static final String NAMESPACE = "SYSUSER";
	private static final String STATEMENT = "query";
	
	private Dao dao;
	
	
	public Dao getDao() {
		return dao;
	}


	public void setDao(Dao dao) {
		this.dao = dao;
	}


	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		Map<String,Object> paramData = new HashMap<String,Object>();
		paramData.put("loginId", userName);
		Map<String, Object> dataMap =  dao.get(NAMESPACE, STATEMENT,paramData);
		RainbowUser user = new RainbowUser((String)dataMap.get("password"), userName,dataMap);
	
		return user;
	}

}
