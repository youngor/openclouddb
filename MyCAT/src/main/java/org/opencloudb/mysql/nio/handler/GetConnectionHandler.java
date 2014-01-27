package org.opencloudb.mysql.nio.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;

/**
 * wuzh
 * 
 * @author mycat
 * 
 */
public class GetConnectionHandler implements ResponseHandler {
	private final CopyOnWriteArrayList<PhysicalConnection> successCons;
	private static final Logger logger = Logger
			.getLogger(GetConnectionHandler.class);
	private final AtomicInteger finishedCount = new AtomicInteger(0);
	private final int total;

	public GetConnectionHandler(
			CopyOnWriteArrayList<PhysicalConnection> connsToStore,
			int totalNumber) {
		super();
		this.successCons = connsToStore;
		this.total = totalNumber;
	}

	public String getStatusInfo()
	{
		return "finished "+ finishedCount.get()+" success "+successCons.size()+" target count:"+this.total;
	}
	public boolean finished() {
		return finishedCount.get() >= total;
	}

	@Override
	public void connectionAcquired(PhysicalConnection conn) {
		successCons.add(conn);
		finishedCount.addAndGet(1);
		logger.info("connected successfuly " + conn);

	}

	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		finishedCount.addAndGet(1);
		logger.warn("connect error " + conn+ e);

	}

	@Override
	public void errorResponse(byte[] err, PhysicalConnection conn) {
		logger.warn("caught error resp: " + conn + " " + new String(err));

	}

	@Override
	public void okResponse(byte[] ok, PhysicalConnection conn) {
		logger.info("received ok resp: " + conn + " " + new String(ok));

	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {

	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {

	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {

	}

	@Override
	public void writeQueueAvailable() {

	}

	@Override
	public void connectionClose(PhysicalConnection conn, String reason) {

	}

}