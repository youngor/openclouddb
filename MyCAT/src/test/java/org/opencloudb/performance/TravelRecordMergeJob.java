package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TravelRecordMergeJob implements Runnable {
	private final Connection con;
	private final long limit;
	private final int executeTimes;
	Calendar date = Calendar.getInstance();
	DateFormat datafomat = new SimpleDateFormat("yyyy-MM-dd");
	Random random = new Random();
	private final AtomicInteger finshiedCount;
	private final AtomicInteger failedCount;

	public TravelRecordMergeJob(Connection con, long limit, int executeTimes,
			AtomicInteger finshiedCount, AtomicInteger failedCount) {
		super();
		this.con = con;
		this.limit = limit;
		this.executeTimes = executeTimes;
		this.finshiedCount = finshiedCount;
		this.failedCount = failedCount;
	}

	private void select() {
		ResultSet rs = null;
		try {
			String sql = "select sum(fee) total_fee, days,count(id),max(fee),min(fee) from  travelrecord  group by days  order by days desc limit "
					+ (limit +random.nextInt() % 30);
			rs = con.createStatement().executeQuery(sql);
			finshiedCount.addAndGet(1);
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
		for (int i = 0; i < executeTimes; i++) {
			this.select();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
