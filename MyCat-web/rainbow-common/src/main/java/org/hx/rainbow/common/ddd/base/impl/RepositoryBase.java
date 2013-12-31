package org.hx.rainbow.common.ddd.base.impl;

import java.util.Map;
import java.util.Set;

import org.hx.rainbow.common.dao.Dao;
import org.hx.rainbow.common.ddd.factory.RepositoryStateFactory;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;

@SuppressWarnings("rawtypes")
public abstract class RepositoryBase<K extends Entity,V extends Entity>{

	public Dao dao;
	
	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public void retrieve(K entity, V aggregation,Map<String,Object> param,IEntityState state) {	
		System.out.println("entity.class==" +entity.getClass().getName()+"aggregation.class==="+aggregation.getClass().getName());
        RepositoryStateFactory.getInstance().getRepositoryState(aggregation,state).retrieve(entity,aggregation,param);
	}

	@SuppressWarnings("unchecked")
	public void store(V entity) {
		 Set<IEntityState> bstateList = entity.getBstate();
		 for(IEntityState state : bstateList){
			 RepositoryStateFactory.getInstance().getRepositoryState(entity,state).store(entity);
		 }
	}
	
	
}
