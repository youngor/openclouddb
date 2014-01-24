package org.opencloudb.mysql.nio.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;

public class SimpleLogHandler implements ResponseHandler{
	private static final Logger LOGGER = Logger
			.getLogger(SimpleLogHandler.class);
	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		LOGGER.warn("connectionError "+e);
		
	}

	@Override
	public void connectionAcquired(PhysicalConnection conn) {
		LOGGER.info("connectionAcquired "+conn);
		
	}

	@Override
	public void errorResponse(byte[] err, PhysicalConnection conn) {
		LOGGER.warn("caught error resp: " + conn + " " + new String(err));
	}

	@Override
	public void okResponse(byte[] ok, PhysicalConnection conn) {
		LOGGER.info("okResponse: " + conn );
		
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
		LOGGER.info("fieldEofResponse: " + conn );
		
	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		LOGGER.info("rowResponse: " + conn );
		
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		LOGGER.info("rowEofResponse: " + conn );
		
	}

	@Override
	public void writeQueueAvailable() {
		
		
	}

	@Override
	public void connectionClose(PhysicalConnection conn, String reason) {
		
		
	}

}
