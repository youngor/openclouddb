package org.hx.rainbow.common.ddd.base;

import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;
import org.hx.rainbow.common.util.QueryInfo;

public interface Repository<T extends Entity<?>,V extends Entity<?>> {
  
	public T findById(String id);
	
	public T findByCode(String code);
	
	public void retrieve(T entity,V aggregation,Map<String,Object> param,IEntityState state);
	
	public void store(T entity);
	
	public List<T> find(QueryInfo<T> queryInfo);
	
}
