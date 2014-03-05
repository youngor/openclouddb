package org.opencloudb.route.function;

import org.opencloudb.config.model.rule.RuleAlgorithm;

/**
 * 路由分片函数抽象类
 * 为了实现一个默认的支持范围分片的函数 calcualteRange
 * 重写它以实现自己的范围路由规则
 * @author lxy
 *
 */
public abstract class AbstractPartionAlgorithm implements RuleAlgorithm {

	@Override
	public void init() {
	}

	/**
	 * 返回所有被路由到的节点的编号
	 * 返回长度为0的数组表示所有节点都被路由（默认）
	 * 返回null表示没有节点被路由到
	 */
	@Override
	public Integer[] calculateRange(String beginValue, String endValue) {
		return new Integer[0];
	}
	
}
