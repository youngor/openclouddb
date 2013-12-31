package org.hx.rainbow.common.dao.handler;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

@SuppressWarnings("rawtypes")
public class NumberHandler implements TypeHandler {
	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			JdbcType jdbcType) throws SQLException {
		if(parameter != null && !"".equals(parameter)){
			if(parameter instanceof String){
				String number =  (String)parameter;
				ps.setBigDecimal(i,new BigDecimal(number));
			}else if(parameter instanceof BigDecimal){
				ps.setBigDecimal(i, (BigDecimal)parameter);
			}else if(parameter instanceof Integer){
				ps.setInt(i, (Integer)parameter);
			}else if(parameter instanceof Double){
				ps.setDouble(i, (Double)parameter);
			}else if(parameter instanceof Float){
				ps.setFloat(i, (Float)parameter);
			}else if(parameter instanceof Long){
				ps.setLong(i, (Long)parameter);
			}
		}else{
			ps.setBigDecimal(i,null);
		}

	}

	@Override
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		return rs.getObject(columnName);
	}

	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getObject(columnIndex);
	}
	
}
