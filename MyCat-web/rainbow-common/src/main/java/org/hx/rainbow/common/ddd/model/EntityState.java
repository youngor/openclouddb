package org.hx.rainbow.common.ddd.model;

/**
 * 实体状态操作枚举类
 * @author huangxin
 *
 */
public enum EntityState implements IEntityState{
	/**
	 * 读
	 */
	READ,
	/**
	 * 新增
	 */
	ADD,
	/**
	 * 修改
	 */
	UPDATE,
	/**
	 * 修改(静态信息)
	 */
	MODIFY,
	/**
	 * 删除
	 */
	DELETE
}
