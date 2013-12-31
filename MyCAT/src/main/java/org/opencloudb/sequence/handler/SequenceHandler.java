package org.opencloudb.sequence.handler;

/**
 * 
 * @author <a href="http://www.micmiu.com">Michael</a>
 * @time Create on 2013-12-20 下午3:35:53
 * @version 1.0
 */
public interface SequenceHandler {

	public long nextId(String prefixName);

}
