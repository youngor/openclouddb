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

public class GoodsInsertJob implements Runnable {
	private final Connection con;
	private final int totalRecords;
	private int finsihed;
	private final int batchSize;
	Calendar date = Calendar.getInstance();
	DateFormat datafomat = new SimpleDateFormat("yyyy-MM-dd");
	private final AtomicInteger finshiedCount;
	private final AtomicInteger failedCount;

	public GoodsInsertJob(Connection con, int totalRecords, int batchSize,
			int startId, AtomicInteger finshiedCount, AtomicInteger failedCount) {
		super();
		this.con = con;
		this.totalRecords = startId + totalRecords;
		this.batchSize = batchSize;
		this.finsihed = startId;
		this.finshiedCount = finshiedCount;
		this.failedCount = failedCount;
	}

	private int insert(List<Map<String, String>> list) throws SQLException {
		PreparedStatement ps;
		String sql = "insert into goods (id,name ,good_type,good_img_url,good_created ,good_desc, price ) values(?,? ,?,?,? ,?, ?)";
		ps = con.prepareStatement(sql);
		for (Map<String, String> map : list) {
			ps.setLong(1, Long.parseLong(map.get("id")));
			ps.setString(2, (String) map.get("name"));
			ps.setShort(3, Short.parseShort(map.get("good_type")));
			ps.setString(4, (String) map.get("good_img_url"));
			ps.setString(5, (String) map.get("good_created"));
			ps.setString(6, (String) map.get("good_desc"));
			ps.setDouble(7, Double.parseDouble(map.get("price")));
			ps.addBatch();
		}
		ps.executeBatch();
		return list.size();
	}

	private List<Map<String, String>> getNextBatch() {
		int end = (finsihed + batchSize) < this.totalRecords ? (finsihed + batchSize)
				: totalRecords;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(
				(end - finsihed));
		for (int i = finsihed; i < end; i++) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("id", i + "");
			m.put("name", "googs " + i);
			m.put("good_type", i % 100 + "");
			m.put("good_img_url", "http://openclouddb.org/" + i);
			m.put("good_created", getRandomDay(i));
			m.put("good_desc", "best goods " + i);
			m.put("price", (i + 0.0) % 1000 + "");
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
