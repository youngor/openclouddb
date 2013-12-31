package org.opencloudb.sequence.handler;

import java.util.Map;

/**
 * zookeeper 实现递增序列号
 * 
 * @author <a href="http://www.micmiu.com">Michael</a>
 * @time Create on 2013-12-29 下午11:04:47
 * @version 1.0
 */
public class IncrSequenceZKHandler extends IncrSequenceHandler {

	@Override
	public Map<String, String> getParaValMap(String prefixName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean fetchNextPeriod(String prefixName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateCURIDVal(String prefixName, Long val) {
		// TODO Auto-generated method stub
		return null;
	}

}
