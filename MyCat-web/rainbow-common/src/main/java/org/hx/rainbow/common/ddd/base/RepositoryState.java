package org.hx.rainbow.common.ddd.base;

import java.util.Map;

import org.hx.rainbow.common.ddd.model.Entity;

public interface RepositoryState<K extends Entity<?>,V extends Entity<?>> {
	public void retrieve(K entity,V aggregation,Map<String,Object> param) ;
    
	public void store(V entity);

}
