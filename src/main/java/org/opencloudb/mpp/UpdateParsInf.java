package org.opencloudb.mpp;

import java.util.Map;

public class UpdateParsInf extends SelectParseInf {
	/**
	 * update table's name
	 */
	public String tableName;
	/**
	 * colum's and values
	 */
	public Map<String, String> columnPairMap;

}
