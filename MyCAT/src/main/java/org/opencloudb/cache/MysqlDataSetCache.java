package org.opencloudb.cache;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * cache mysql dataset ,for example "select * from A where .......",cache all
 * result
 * 
 * @author wuzhih
 * 
 */
public class MysqlDataSetCache implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5426632041410472392L;
	// sql should not inlude page limit ,should store first record and sequnce
	// next
	private String sql;
	private int total;
	private String dataFile;
	private long createTime;
	private volatile int curCount;
	private volatile long lastAccesTime;
	private volatile boolean storing = true;

	public String getSql() {
		return sql;
	}

	public boolean isStoring() {
		return storing;
	}

	public void setStoring(boolean storing) {
		this.storing = storing;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastAccesTime() {
		return lastAccesTime;
	}

	public void setLastAccesTime(long lastAccesTime) {
		this.lastAccesTime = lastAccesTime;
	}

	public void addHeader(byte[] header) throws IOException  {
		writeFile(header);
	}

	private void writeFile(byte[] data) throws IOException {
		FileOutputStream outf = null;
		try {
			outf = new FileOutputStream(dataFile, true);
			outf.write(data);

		} finally {
			if (outf != null) {
				outf.close();
			}
		}
	}

	public void appendRecord(byte[] row) throws IOException {
		writeFile(row);
		curCount++;
	}
}
