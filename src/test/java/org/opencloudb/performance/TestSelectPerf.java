package org.opencloudb.performance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author shenzhw
 * 
 */
public class TestSelectPerf {

	private static AtomicInteger finshiedCount = new AtomicInteger();
	private static AtomicInteger failedCount = new AtomicInteger();

	public static void addFinshed(int count) {
		finshiedCount.addAndGet(count);
	}

	public static void addFailed(int count) {
		failedCount.addAndGet(count);
	}

	private static Connection getCon(String url, String user, String passwd)
			throws SQLException {
		Connection theCon = DriverManager.getConnection(url, user, passwd);
		return theCon;
	}

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		if (args.length < 5) {
			System.out
					.println("input param,format: [jdbcurl] [user] [password]  [threadpoolsize]  [executetimes] [maxId] ");
			return;
		}
		int threadCount = 0;// 线程数
		String url = args[0];
		String user = args[1];
		String password = args[2];
		threadCount = Integer.parseInt(args[3]);
		int executetimes = Integer.parseInt(args[4]);
		long maxId = Integer.parseInt(args[5]);
		System.out.println("concerent threads:" + threadCount);
		System.out.println("execute sql times:" + executetimes);
		System.out.println("maxId:" + maxId);
		ArrayList<Thread> threads = new ArrayList<Thread>(threadCount);
		for (int i = 0; i < threadCount; i++) {
			try {

				Connection con = getCon(url, user, password);
				System.out.println("create thread " + i);
				TravelRecordSelectJob job = new TravelRecordSelectJob(con,
						maxId, executetimes);
				Thread thread = new Thread(job);
				threads.add(thread);
			} catch (Exception e) {
				System.out.println("failed create thread " + i + " err "
						+ e.toString());
			}
		}

		System.out.println("success create thread count: " + threads.size());
		for (Thread thread : threads) {
			thread.start();
		}
		long start = System.currentTimeMillis();
		System.out.println("all thread started,waiting finsh...");
		boolean notFinished = true;
		while (notFinished) {
			notFinished = false;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					notFinished = true;
					break;
				}
			}
			long sucess = finshiedCount.get() - failedCount.get();
			System.out.println("finished records :" + finshiedCount.get()
					+ " failed:" + failedCount.get() + " speed:" + sucess
					* 1000.0 / (System.currentTimeMillis() - start));
			Thread.sleep(1000);
		}
		long usedTime = (System.currentTimeMillis() - start)/1000;
		System.out.println("finishend:" + finshiedCount.get() + " failed:"
				+ failedCount.get());
		long sucess = finshiedCount.get() - failedCount.get();
		System.out.println("used time total:" + usedTime  + "seconds");
		System.out.println("tps:" + sucess * 1.0 / usedTime);
	}
}

class TravelRecordSelectJob implements Runnable {
	private final Connection con;
	private final long maxId;
	private final int executeTimes;
	Calendar date = Calendar.getInstance();
	DateFormat datafomat = new SimpleDateFormat("yyyy-MM-dd");
	Random random = new Random();

	public TravelRecordSelectJob(Connection con, long maxId, int executeTimes) {
		super();
		this.con = con;
		this.maxId = maxId;
		this.executeTimes = executeTimes;
	}

	private void select() {
		ResultSet rs = null;
		try {

			String sql = "select * from  travelrecord  where id="
					+ Math.abs(random.nextLong()) % maxId;
			rs = con.createStatement().executeQuery(sql);
			TestSelectPerf.addFinshed(1);
		} catch (Exception e) {
			TestSelectPerf.addFailed(1);
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
