package org.opencloudb.route;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.opencloudb.MycatServer;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.parser.ExtNodeToString4SEQ;
import org.opencloudb.parser.SQLParserDelegate;

import com.foundationdb.sql.parser.QueryTreeNode;
import com.foundationdb.sql.unparser.NodeToString;

public class MyCATSequnceProcessor {
	private static final Logger LOGGER = Logger
			.getLogger(MyCATSequnceProcessor.class);
	private ConcurrentLinkedQueue<SessionSQLPair> seqSQLQueue = new ConcurrentLinkedQueue<SessionSQLPair>();

	public MyCATSequnceProcessor() {
		new ExecuteThread().start();
	}

	public void addNewSql(SessionSQLPair pair) {
		seqSQLQueue.offer(pair);
	}

	private void executeSeq(SessionSQLPair pair) {
		try {

			// @micmiu 扩展NodeToString实现自定义全局序列号
			NodeToString strHandler = new ExtNodeToString4SEQ(MycatServer
					.getInstance().getConfig().getSystem()
					.getSequnceHandlerType());
			// 如果存在sequence 转化sequence为实际数值
			String charset = pair.session.getSource().getCharset();
			QueryTreeNode ast = SQLParserDelegate.parse(pair.sql,
					charset == null ? "utf-8" : charset);
			String sql = strHandler.toString(ast);
			if (sql.toUpperCase().startsWith("SELECT")) {
				// /
			}

			pair.session.getSource().routeEndExecuteSQL(sql, pair.type,
					pair.schema);

		} catch (Exception e) {
			LOGGER.error(e);
			pair.session.getSource().writeErrMessage(ErrorCode.ER_YES,
					"mycat sequnce err." + e);
			return;
		}
	}

	class ExecuteThread extends Thread {
		public void run() {
			while (true) {
				SessionSQLPair pair = null;
				try {
					pair = seqSQLQueue.poll();
					if (pair == null) {
						Thread.sleep(100);
					} else {
						executeSeq(pair);
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}
	}
}
