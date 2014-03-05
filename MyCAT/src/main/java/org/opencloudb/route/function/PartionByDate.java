package org.opencloudb.route.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.opencloudb.config.model.rule.RuleAlgorithm;
import org.opencloudb.route.ServerRouterUtil;

/**
 * 例子 按日期列分区  格式 between操作解析的范例
 * 
 * @author lxy
 * 
 */
public class PartionByDate extends AbstractPartionAlgorithm implements RuleAlgorithm {
	private static final Logger LOGGER = Logger
			.getLogger(ServerRouterUtil.class);

	private String sBeginDate;
	private String sPartionDay;
	private String dateFormat;

	private long beginDate;
	private long partionTime;
	
	private static final long oneDay = 86400000;

	@Override
	public void init() {
		try {
			beginDate = new SimpleDateFormat(dateFormat).parse(sBeginDate)
					.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		partionTime = Integer.parseInt(sPartionDay) * oneDay;
	}

	@Override
	public Integer calculate(String columnValue) {
		try {
			long targetTime = new SimpleDateFormat(dateFormat).parse(
					columnValue).getTime();
			int targetPartition = (int) ((targetTime - beginDate) / partionTime);
			return targetPartition;

		} catch (ParseException e) {
			LOGGER.error("date partion rule wrong", e);
		}
		return null;
	}

	@Override
	public Integer[] calculateRange(String beginValue, String endValue) {
		int begin = 0, end = 0;
		begin = calculate(beginValue);
		end = calculate(endValue);

		if (end >= begin) {
			int len = end-begin+1;
			Integer [] re = new Integer[len];
			
			for(int i =0;i<len;i++){
				re[i]=begin+i;
			}
			
			return re;
		}else{
			return null;
		}
	}

	public void setsBeginDate(String sBeginDate) {
		this.sBeginDate = sBeginDate;
	}

	public void setsPartionDay(String sPartionDay) {
		this.sPartionDay = sPartionDay;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
