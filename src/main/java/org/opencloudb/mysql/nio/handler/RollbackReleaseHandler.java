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
 * (created at 2012-5-11)
 */
package org.opencloudb.mysql.nio.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;

/**
 * @author mycat
 */
public class RollbackReleaseHandler implements ResponseHandler {
    private static final Logger logger = Logger.getLogger(RollbackReleaseHandler.class);

    public RollbackReleaseHandler() {
    }

    @Override
    public void connectionAcquired(PhysicalConnection conn) {
        logger.error("unexpected invocation: connectionAcquired from rollback-release");
        conn.close();
    }

    @Override
    public void connectionError(Throwable e, PhysicalConnection conn) {
        logger.error("unexpected invocation: connectionError from rollback-release");
        conn.close();
    }

    @Override
    public void errorResponse(byte[] err, PhysicalConnection conn) {
        conn.quit();
    }

    @Override
    public void okResponse(byte[] ok, PhysicalConnection conn) {
        conn.release();
    }

    @Override
    public void fieldEofResponse(byte[] header, List<byte[]> fields, byte[] eof, PhysicalConnection conn) {
    }

    @Override
    public void rowResponse(byte[] row, PhysicalConnection conn) {
    }

    @Override
    public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
        logger.error("unexpected packet: EOF of resultSet from rollback-release");
        conn.close();
    }

	@Override
	public void writeQueueAvailable() {
		// TODO Auto-generated method stub
		
	}

}