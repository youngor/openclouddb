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
 * (created at 2011-6-17)
 */
package org.opencloudb.parser;

import java.sql.SQLSyntaxErrorException;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.SQLParserFeature;

/**
 * @author mycat
 */
public final class SQLParserDelegate {
	public static final String DEFAULT_CHARSET="utf-8";
	private static final ThreadLocal<SQLParser> sqlParser = new ThreadLocal<SQLParser>() {
		protected SQLParser initialValue() {
			SQLParser parser = new SQLParser();
			parser.getFeatures().add(SQLParserFeature.DOUBLE_QUOTED_STRING);
			parser.getFeatures().add(SQLParserFeature.MYSQL_HINTS);
			parser.getFeatures().add(SQLParserFeature.MYSQL_INTERVAL);
			return parser;
		}

	};

	public static QueryTreeNode parse(String stmt, String string)
			throws SQLSyntaxErrorException {
		try {
			return sqlParser.get().parseStatement(stmt);
		} catch (StandardException e) {
			throw new SQLSyntaxErrorException(e);
		}
	}
}