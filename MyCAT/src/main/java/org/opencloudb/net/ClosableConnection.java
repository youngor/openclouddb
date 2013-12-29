package org.opencloudb.net;

public interface ClosableConnection {
	 /**
     * 关闭连接
     */
    void close(String reason);
}
