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
package org.opencloudb.mysql.nio;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.mysql.ByteUtil;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.net.handler.BackendAsyncHandler;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.OkPacket;

/**
 * life cycle: from connection establish to close <br/>
 * 
 * @author mycat
 */
public class MySQLConnectionHandler extends BackendAsyncHandler {
	private static final Logger logger = Logger
			.getLogger(MySQLConnectionHandler.class);
	private static final int RESULT_STATUS_INIT = 0;
	private static final int RESULT_STATUS_HEADER = 1;
	private static final int RESULT_STATUS_FIELD_EOF = 2;

	private final MySQLConnection source;
	private volatile int resultStatus;
	private volatile byte[] header;
	private volatile List<byte[]> fields;

	/**
	 * life cycle: one SQL execution
	 */
	private volatile ResponseHandler responseHandler;

	public MySQLConnectionHandler(MySQLConnection source) {
		this.source = source;
		this.resultStatus = RESULT_STATUS_INIT;
	}

	public void connectionError(Throwable e) {
		// connError = e;
		// handleQueue();
		dataQueue.clear();
		if (responseHandler != null) {
			System.out.println(" responseHandler connectionError "
					+ responseHandler.getClass().getName());
			e.printStackTrace();
			responseHandler.connectionError(e, source);
		}

	}

	public MySQLConnection getSource() {
		return source;
	}

	@Override
	public void handle(byte[] data) {
		offerData(data, source.getProcessor().getExecutor());
	}

	@Override
	protected void offerDataError() {
		dataQueue.clear();
		resultStatus = RESULT_STATUS_INIT;
		throw new RuntimeException("offer data error!");
	}

	@Override
	protected void handleData(byte[] data) {
		switch (resultStatus) {
		case RESULT_STATUS_INIT:
			switch (data[4]) {
			case OkPacket.FIELD_COUNT:
				handleOkPacket(data);
				break;
			case ErrorPacket.FIELD_COUNT:
				handleErrorPacket(data);
				break;
			default:
				resultStatus = RESULT_STATUS_HEADER;
				header = data;
				fields = new ArrayList<byte[]>((int) ByteUtil.readLength(data,
						4));
			}
			break;
		case RESULT_STATUS_HEADER:
			switch (data[4]) {
			case ErrorPacket.FIELD_COUNT:
				resultStatus = RESULT_STATUS_INIT;
				handleErrorPacket(data);
				break;
			case EOFPacket.FIELD_COUNT:
				resultStatus = RESULT_STATUS_FIELD_EOF;
				handleFieldEofPacket(data);
				break;
			default:
				fields.add(data);
			}
			break;
		case RESULT_STATUS_FIELD_EOF:
			switch (data[4]) {
			case ErrorPacket.FIELD_COUNT:
				resultStatus = RESULT_STATUS_INIT;
				handleErrorPacket(data);
				break;
			case EOFPacket.FIELD_COUNT:
				resultStatus = RESULT_STATUS_INIT;
				handleRowEofPacket(data);
				break;
			default:
				handleRowPacket(data);
			}
			break;
		default:
			throw new RuntimeException("unknown status!");
		}
	}

	public void setResponseHandler(ResponseHandler responseHandler) {
		// logger.info("set response handler "+responseHandler);
		// if (this.responseHandler != null && responseHandler != null) {
		// throw new RuntimeException("reset agani!");
		// }
		this.responseHandler = responseHandler;
	}

	@Override
	protected void handleDataError(Throwable t) {
		logger.warn("handleDataError err,maybe BUG,please reprot :", t);
		dataQueue.clear();
		String errMsg = "execption:(handleDataError) " + t.toString();
		this.source.close(errMsg);

		if (responseHandler != null) {
			responseHandler.connectionClose(source, errMsg);
		}

	}

	/**
	 * OK数据包处理
	 */
	private void handleOkPacket(byte[] data) {
		responseHandler.okResponse(data, source);
	}

	/**
	 * ERROR数据包处理
	 */
	private void handleErrorPacket(byte[] data) {
		responseHandler.errorResponse(data, source);
	}

	/**
	 * 字段数据包结束处理
	 */
	private void handleFieldEofPacket(byte[] data) {
		if (responseHandler != null) {
			responseHandler.fieldEofResponse(header, fields, data, source);
		} else {
			logger.warn("no handler bind in this con " + this + " client:"
					+ source);
		}
	}

	/**
	 * 行数据包处理
	 */
	private void handleRowPacket(byte[] data) {
		if (responseHandler != null) {
			responseHandler.rowResponse(data, source);
		} else {
			logger.warn("no handler bind in this con " + this + " client:"
					+ source);
		}
	}

	/**
	 * 行数据包结束处理
	 */
	private void handleRowEofPacket(byte[] data) {
		if (responseHandler != null) {
			responseHandler.rowEofResponse(data, source);
		} else {
			logger.warn("no handler bind in this con " + this + " client:"
					+ source);
		}
	}

}