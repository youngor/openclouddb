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
 * (created at 2012-4-19)
 */
package org.opencloudb.mysql.nio.handler;

import java.util.List;

import org.opencloudb.backend.PhysicalConnection;

/**
 * @author mycat
 * @author mycat
 */
public interface ResponseHandler {

	/**
	 * 无法获取连接
	 * 
	 * @param e
	 * @param conn
	 */
	public void connectionError(Throwable e, PhysicalConnection conn);

	/**
	 * 已获得有效连接的响应处理
	 */
	void connectionAcquired(PhysicalConnection conn);

	/**
	 * 收到错误数据包的响应处理
	 */
	void errorResponse(byte[] err, PhysicalConnection conn);

	/**
	 * 收到OK数据包的响应处理
	 */
	void okResponse(byte[] ok, PhysicalConnection conn);

	/**
	 * 收到字段数据包结束的响应处理
	 */
	void fieldEofResponse(byte[] header, List<byte[]> fields, byte[] eof,
			PhysicalConnection conn);

	/**
	 * 收到行数据包的响应处理
	 */
	void rowResponse(byte[] row, PhysicalConnection conn);

	/**
	 * 收到行数据包结束的响应处理
	 */
	void rowEofResponse(byte[] eof, PhysicalConnection conn);

	/**
	 * 写队列为空，可以写数据了
	 * 
	 */
	void writeQueueAvailable();

	/**
	 * on connetion close event
	 */
	void connectionClose(PhysicalConnection conn, String reason);

	
}