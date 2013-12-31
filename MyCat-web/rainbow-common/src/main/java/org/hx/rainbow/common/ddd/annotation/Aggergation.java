package org.hx.rainbow.common.ddd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 聚合体注解
 * @author huangxin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aggergation {
	/**
	 * 聚合根类
	 * @return
	 */
	public abstract Class<?> root();
	/**
	 * 是否子类
	 * @return
	 */
	public boolean isSubclass () default false;
}
