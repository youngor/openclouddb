package org.opencloudb.sequence.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 递增序列号处理器
 * 
 * @author <a href="http://www.micmiu.com">Michael</a>
 * @time Create on 2013-12-29 下午10:42:39
 * @version 1.0
 */
public abstract class IncrSequenceHandler implements SequenceHandler {

	public static final Logger logger = LoggerFactory
			.getLogger(IncrSequenceHandler.class);

	public static final String FILE_NAME = "sequence_conf.properties";

	public static final String KEY_HIS_NAME = ".HISIDS";// 1-10000,50001-60000
	public static final String KEY_MIN_NAME = ".MINID";// 1
	public static final String KEY_MAX_NAME = ".MAXID";// 10000
	public static final String KEY_CUR_NAME = ".CURID";// 888

	public abstract Map<String, String> getParaValMap(String prefixName);

	public abstract Boolean updateCURIDVal(String prefixName, Long val);

	public abstract Boolean fetchNextPeriod(String prefixName);

	@Override
	public long nextId(String prefixName) {
		Map<String, String> paraMap = this.getParaValMap(prefixName);
		if (null == paraMap) {
			throw new RuntimeException("fetch Param Values error.");
		}
		Long nextId = Long.parseLong(paraMap.get(prefixName + KEY_CUR_NAME)) + 1;
		Long maxId = Long.parseLong(paraMap.get(prefixName + KEY_MAX_NAME));
		if (nextId > maxId) {
			fetchNextPeriod(prefixName);
			return nextId(prefixName);
		}
		updateCURIDVal(prefixName, nextId);
		return nextId.longValue();

	}
}
