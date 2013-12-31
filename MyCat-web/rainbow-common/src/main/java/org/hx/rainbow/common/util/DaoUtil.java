package org.hx.rainbow.common.util;

import org.apache.ibatis.mapping.MappedStatement;
import org.mybatis.spring.SqlSessionTemplate;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class DaoUtil {
	private volatile static DaoUtil dataUtil = null;
	private static JdbcTemplate jdbcTmeplate = null;
	private static SqlSessionTemplate sqlSessionTemplate = null;
	
	private DaoUtil(){}
	
	public static DaoUtil getInstance(){
		if(dataUtil  == null ){
			synchronized (DaoUtil.class) {
				if(dataUtil == null){
					dataUtil = new DaoUtil();
				}
			}
		}
		return dataUtil;
	}
	
	
	public  JdbcTemplate getJdbcTemplate(){
		if(jdbcTmeplate == null){
			jdbcTmeplate = (JdbcTemplate)SpringApplicationContext.getBean("jdbcTemplate");
		}
		return jdbcTmeplate;
	}
	
	
	public  SqlSessionTemplate getSqlSessionTemplate(){
		if(sqlSessionTemplate == null){
			sqlSessionTemplate = (SqlSessionTemplate)SpringApplicationContext.getBean("sqlSessionTemplate");
		}
		return sqlSessionTemplate;
	}
	
	public static String getSql(String mothodMame,Object paramObject){
		MappedStatement mappedStatement = sqlSessionTemplate.getConfiguration().getMappedStatement(mothodMame);
		return mappedStatement.getBoundSql(paramObject).getSql();
	}
	
	
}
