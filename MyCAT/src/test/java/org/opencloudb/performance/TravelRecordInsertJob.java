package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TravelRecordInsertJob implements Runnable {
	private final Connection con;
	private final int endId;
	private int finsihed;
	private final int batchSize;
	private final  AtomicInteger finshiedCount;
	private final  AtomicInteger failedCount;
	Calendar date = Calendar.getInstance();
	DateFormat datafomat = new SimpleDateFormat("yyyy-MM-dd");

	public TravelRecordInsertJob(Connection con, int totalRecords,
			int batchSize, int startId,AtomicInteger finshiedCount,AtomicInteger failedCount) {
		super();
		this.con = con;
		this.endId = startId + totalRecords;
		this.batchSize = batchSize;
		this.finsihed = startId;
		this.finshiedCount=finshiedCount;
		this.failedCount=failedCount;
	}

	private int insert(List<Map<String, String>> list) throws SQLException {
		PreparedStatement ps;


			String sql = "insert into travelrecord (id,user,traveldate,fee,days) values(?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			for (Map<String, String> map : list) {
				ps.setLong(1, Long.parseLong(map.get("id")));
				ps.setString(2, (String) map.get("user"));
				ps.setString(3, (String) map.get("traveldate"));
				ps.setString(4, (String) map.get("fee"));
				ps.setString(5, (String) map.get("days"));
				ps.addBatch();
				ps.executeBatch();
			}
		
		return list.size();
	}

	private List<Map<String, String>> getNextBatch() {
		int end = (finsihed + batchSize) < this.endId ? (finsihed + batchSize)
				: endId;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(
				(end - finsihed));
		for (int i = finsihed; i < end; i++) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("id", i + "");
			m.put("user", "user " + i);
			m.put("traveldate", getRandomDay(i));
			m.put("fee", i % 10000 + "");
			m.put("days", i % 10000 + "");
			list.add(m);
		}
		finsihed += list.size();
		return list;
	}

	private String getRandomDay(int i) {
		int month = i % 11 + 1;
		int day = i % 27 + 1;

		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);
		return datafomat.format(date.getTime());

	}

	@Override
	public void run() {
		List<Map<String, String>> batch = getNextBatch();
		while (!batch.isEmpty()) {
			try {
				con.setAutoCommit(true);
				insert(batch);
				finshiedCount.addAndGet(batch.size());
			} catch (Exception e) {
				failedCount.addAndGet(batch.size());
				e.printStackTrace();
			}
			batch = getNextBatch();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
