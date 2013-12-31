package org.hx.rainbow.common.ddd.factory;

import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.ddd.proxy.EntityProxyMethod;

/**
 * 仓储实体实例工厂
 * @author huangxin
 * 
 */
public class EntityFactory {
	
	private volatile static EntityFactory entityfactory = null;

	private EntityFactory() {
	}

	public static EntityFactory getInstance() {
		if (entityfactory == null) {
			synchronized (EntityFactory.class) {
				if (entityfactory == null) {
					entityfactory = new EntityFactory();
				}
			}
		}
		return entityfactory;
	}

	/**
	 * 获取传入clazz的继承EntityBase的对象
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity<?>> T getEntity(Class<T> clazz) {
		return (T)EntityProxyMethod.getInstance().getInstance(clazz);
	}
}
