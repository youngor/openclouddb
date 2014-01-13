package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TravelRecordSelectJob implements Runnable {
	private final Connection con;
	private final long maxId;
	private final int executeTimes;
	Calendar date = Calendar.getInstance();
	DateFormat datafomat = new SimpleDateFormat("yyyy-MM-dd");
	Random random = new Random();
	private final AtomicInteger finshiedCount;
	private final AtomicInteger failedCount;
	private volatile long usedTime;
	private volatile long success;
	public TravelRecordSelectJob(Connection con, long maxId, int executeTimes,
			AtomicInteger finshiedCount, AtomicInteger failedCount) {
		super();
		this.con = con;
		this.maxId = maxId;
		this.executeTimes = executeTimes;
		this.finshiedCount = finshiedCount;
		this.failedCount = failedCount;
	}

	private void select() {
		ResultSet rs = null;
		try {

			String sql = "select * from  travelrecord  where id="
					+ Math.abs(random.nextLong()) % maxId;
			rs = con.createStatement().executeQuery(sql);
			finshiedCount.addAndGet(1);
			success++;
		} catch (Exception e) {
			failedCount.addAndGet(1);
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < executeTimes; i++) {
			this.select();
			usedTime = System.currentTimeMillis() - start;
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public long getUsedTime() {
		return this.usedTime;
	}
	public int getTPS()
	{
		if(usedTime>0)
		{
		return (int) (this.success*1000/this.usedTime);
		}else
		{
			return 0;
		}
	}
	
}
