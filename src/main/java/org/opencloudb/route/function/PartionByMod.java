package org.opencloudb.route.function;

import org.opencloudb.config.model.rule.RuleAlgorithm;

/**
 * number column partion by Mod operator
 * if count is 10 then 0 to 0,21 to 1 (21 % 10 =1)
 * @author wuzhih
 *
 */
public class PartionByMod  implements RuleAlgorithm  {

	private int count;
	@Override
	public void init() {
	
		
	}



	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public Integer calculate(String columnValue) {
	 return (int) (Long.parseLong(columnValue) % count);
	}

}
