package org.hx.rainbow.common.ddd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hx.rainbow.common.ddd.model.IEntityState;

/**
 * 懒加载注解
 * @author huangxin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface  Lazyload {
	
	/**
	 * 操作状态
	 * @return
	 */
	public abstract String state();
	/**
	 * 设置业务枚举类型
	 * @return
	 */
	public abstract Class<? extends IEntityState> enmuClass();
	

	
	
}
