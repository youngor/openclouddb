package org.opencloudb.handler;

import java.nio.ByteBuffer;
import java.util.Map;

import org.opencloudb.MycatServer;
import org.opencloudb.cache.CachePool;
import org.opencloudb.cache.CacheService;
import org.opencloudb.cache.CacheStatic;
import org.opencloudb.cache.LayerCachePool;
import org.opencloudb.config.Fields;
import org.opencloudb.manager.ManagerConnection;
import org.opencloudb.mysql.PacketUtil;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.FieldPacket;
import org.opencloudb.net.mysql.ResultSetHeaderPacket;
import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.util.LongUtil;
import org.opencloudb.util.StringUtil;

public class ShowCache {

	private static final int FIELD_COUNT = 8;
	private static final ResultSetHeaderPacket header = PacketUtil
			.getHeader(FIELD_COUNT);
	private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
	private static final EOFPacket eof = new EOFPacket();
	static {
		int i = 0;
		byte packetId = 0;
		header.packetId = ++packetId;

		fields[i] = PacketUtil.getField("CACHE", Fields.FIELD_TYPE_VAR_STRING);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("MAX", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("CUR", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("ACCESS", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("HIT", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("PUT", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("LAST_ACCESS", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		fields[i] = PacketUtil.getField("LAST_PUT", Fields.FIELD_TYPE_LONG);
		fields[i++].packetId = ++packetId;
		eof.packetId = ++packetId;
	}

	public static void execute(ManagerConnection c) {

		ByteBuffer buffer = c.allocate();

		// write header
		buffer = header.write(buffer, c);

		// write fields
		for (FieldPacket field : fields) {
			buffer = field.write(buffer, c);
		}

		// write eof
		buffer = eof.write(buffer, c);

		// write rows
		byte packetId = eof.packetId;
		CacheService cacheService = MycatServer.getInstance().getCacheService();
		for (Map.Entry<String, CachePool> entry : cacheService
				.getAllCachePools().entrySet()) {
			String cacheName=entry.getKey();
			CachePool cachePool = entry.getValue();
			if (cachePool instanceof LayerCachePool) {
				for (Map.Entry<String, CacheStatic> staticsEntry : ((LayerCachePool) cachePool)
						.getAllCacheStatic().entrySet()) {
					RowDataPacket row = getRow(cacheName+'.'+staticsEntry.getKey(),
							staticsEntry.getValue(), c.getCharset());
					row.packetId = ++packetId;
					buffer = row.write(buffer, c);
				}
			} else {
				RowDataPacket row = getRow(cacheName,
						cachePool.getCacheStatic(), c.getCharset());
				row.packetId = ++packetId;
				buffer = row.write(buffer, c);
			}
		}

		// write last eof
		EOFPacket lastEof = new EOFPacket();
		lastEof.packetId = ++packetId;
		buffer = lastEof.write(buffer, c);

		// write buffer
		c.write(buffer);
	}

	private static RowDataPacket getRow(String poolName,
			CacheStatic cacheStatic, String charset) {
		RowDataPacket row = new RowDataPacket(FIELD_COUNT);
		row.add(StringUtil.encode(poolName, charset));
		// max size
		row.add(LongUtil.toBytes(cacheStatic.getMaxSize()));
		row.add(LongUtil.toBytes(cacheStatic.getItemSize()));
		row.add(LongUtil.toBytes(cacheStatic.getAccessTimes()));
		row.add(LongUtil.toBytes(cacheStatic.getHitTimes()));
		row.add(LongUtil.toBytes(cacheStatic.getPutTimes()));
		row.add(LongUtil.toBytes(cacheStatic.getLastAccesTime()));
		row.add(LongUtil.toBytes(cacheStatic.getLastPutTime()));
		return row;
	}

}
