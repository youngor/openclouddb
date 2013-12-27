/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2012-6-12)
 */
package org.opencloudb.config.model.rule;


/**
 * 分片规则，column是用于分片的数据库物理字段
 * @author mycat
 */
public class RuleConfig {
	private final String column;
	private final String functionName;
	private RuleAlgorithm ruleAlgorithm;

	public RuleConfig(String column, String functionName) {
		if (functionName == null) {
			throw new IllegalArgumentException("functionName is null");
		}
		this.functionName = functionName;
		if (column == null || column.length() <= 0) {
			throw new IllegalArgumentException("no rule column is found");
		}
		this.column = column;
	}

	public RuleAlgorithm getRuleAlgorithm() {
		return ruleAlgorithm;
	}

	public void setRuleAlgorithm(RuleAlgorithm ruleAlgorithm) {
		this.ruleAlgorithm = ruleAlgorithm;
	}

	/**
	 * @return unmodifiable, upper-case
	 */
	public String getColumn() {
		return column;
	}

	public String getFunctionName() {
		return functionName;
	}

	

}