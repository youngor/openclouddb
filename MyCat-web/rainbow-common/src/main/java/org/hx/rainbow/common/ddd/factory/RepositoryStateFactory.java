package org.hx.rainbow.common.ddd.factory;

import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.base.RepositoryState;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.model.IEntityState;
import org.hx.rainbow.common.util.CglibUitl;

/**
 * 仓储状态工厂
 * @author huangxin
 *
 */
public class RepositoryStateFactory {
	
	private volatile static RepositoryStateFactory repositorystatefactory = null;

	private RepositoryStateFactory() {
	}

	public static RepositoryStateFactory getInstance() {
		if (repositorystatefactory == null) {
			synchronized (RepositoryStateFactory.class) {
				if (repositorystatefactory == null) {
					repositorystatefactory = new RepositoryStateFactory();
				}
			}
		}
		return repositorystatefactory;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends RepositoryState> T  getRepositoryState(Entity<?> e, IEntityState state){
		if(e == null || state == null ){
			return null;
		}
		T t =  null;
		Class clazz = e.getClass();
		if(CglibUitl.getInstance().isCglib(e.getClass())){
			clazz = e.getClass().getSuperclass();
		}
		if(clazz.isAnnotationPresent(Aggergation.class)){
			Aggergation aggergation = (Aggergation) clazz.getAnnotation(Aggergation.class);
			Class root = aggergation.root();
			if(root != null){
				clazz = root;
			}
		}
		String className = clazz.getSimpleName() + "State";
		
		try{
			t = (T) SpringApplicationContext.getBean(className);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return t;
	}
}
