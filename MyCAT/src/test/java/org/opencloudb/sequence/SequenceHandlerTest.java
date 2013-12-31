package org.opencloudb.sequence;

import junit.framework.Assert;

import org.junit.Test;
import org.opencloudb.sequence.handler.IncrSequencePropHandler;
import org.opencloudb.sequence.handler.SequenceHandler;

/**
 * 全局序列号单元测试
 * 
 * @author <a href="http://www.micmiu.com">Michael</a>
 * @time Create on 2013-12-30 上午12:07:51
 * @version 1.0
 */
public class SequenceHandlerTest {

	//@Test
	public void testPropSequence() {
		SequenceHandler hander = IncrSequencePropHandler.getInstance();
		Assert.assertEquals(hander.nextId("DEF") - hander.nextId("DEF"), -1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SequenceHandler hander = IncrSequencePropHandler.getInstance();
		System.out.println(hander.nextId("DEF"));
	}

}
