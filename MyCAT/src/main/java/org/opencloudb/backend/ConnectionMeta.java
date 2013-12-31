package org.opencloudb.backend;

/**
 * connection metadata info
 * 
 * @author wuzhih
 * 
 */
public class ConnectionMeta {
	private final String schema;
	private final String charset;
	private final int txIsolation;
	private final boolean autocommit;

	public ConnectionMeta(String schema, String charset, int txIsolation,
			boolean autocommit) {
		super();
		this.schema = schema;
		this.charset = charset;
		this.txIsolation = txIsolation;
		this.autocommit = autocommit;
	}

	public String getSchema() {
		return schema;
	}

	public String getCharset() {
		return charset;
	}

	public int getTxIsolation() {
		return txIsolation;
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	/**
	 * get metadata similarity
	 * 
	 * @param theCon
	 * @return
	 */
	public int getMetaSimilarity(PhysicalConnection theCon) {
		int result = 0;
		if (schema == null || schema.equals(theCon.getSchema())) {
			result++;
		}
		if (charset == null || charset.equals(theCon.getCharset())) {
			result++;
		}
		if (txIsolation == -1 || txIsolation == theCon.getTxIsolation()) {
			result++;
		}
		if (autocommit == theCon.isAutocommit()) {
			result++;
		}
		return result;
	}

}
