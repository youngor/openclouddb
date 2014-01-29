/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.response;

import java.nio.ByteBuffer;

import org.opencloudb.MycatServer;
import org.opencloudb.config.Fields;
import org.opencloudb.heartbeat.MyCATDetector;
import org.opencloudb.heartbeat.MyCATHeartbeat;
import org.opencloudb.heartbeat.MySQLDetector;
import org.opencloudb.heartbeat.MySQLHeartbeat;
import org.opencloudb.manager.ManagerConnection;
import org.opencloudb.mysql.PacketUtil;
import org.opencloudb.net.BackendConnection;
import org.opencloudb.net.NIOProcessor;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.FieldPacket;
import org.opencloudb.net.mysql.ResultSetHeaderPacket;
import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.util.IntegerUtil;
import org.opencloudb.util.LongUtil;
import org.opencloudb.util.StringUtil;
import org.opencloudb.util.TimeUtil;

/**
 * 查询后端连接
 * 
 * @author mycat
 */
public class ShowBackend {

    private static final int FIELD_COUNT = 14;
    private static final ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static final FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static final EOFPacket eof = new EOFPacket();
    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("processor", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("id", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("host", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("port", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("l_port", Fields.FIELD_TYPE_LONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("net_in", Fields.FIELD_TYPE_LONGLONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("net_out", Fields.FIELD_TYPE_LONGLONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("life", Fields.FIELD_TYPE_LONGLONG);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("closed", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("auth", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("quit", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("checking", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("stop", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        fields[i] = PacketUtil.getField("status", Fields.FIELD_TYPE_LONGLONG);
        fields[i++].packetId = ++packetId;
        eof.packetId = ++packetId;
    }

    public static void execute(ManagerConnection c) {
        ByteBuffer buffer = c.allocate();
        buffer = header.write(buffer, c);
        for (FieldPacket field : fields) {
            buffer = field.write(buffer, c);
        }
        buffer = eof.write(buffer, c);
        byte packetId = eof.packetId;
        String charset = c.getCharset();
        for (NIOProcessor p : MycatServer.getInstance().getProcessors()) {
            for (BackendConnection bc : p.getBackends().values()) {
                if (bc != null) {
                    RowDataPacket row = getRow(bc, charset);
                    row.packetId = ++packetId;
                    buffer = row.write(buffer, c);
                }
            }
        }
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.write(buffer, c);
        c.write(buffer);
    }

    private static RowDataPacket getRow(BackendConnection c, String charset) {
        RowDataPacket row = new RowDataPacket(FIELD_COUNT);
        row.add(c.getProcessor().getName().getBytes());
        row.add(LongUtil.toBytes(c.getId()));
        row.add(StringUtil.encode(c.getHost(), charset));
        row.add(IntegerUtil.toBytes(c.getPort()));
        row.add(IntegerUtil.toBytes(c.getLocalPort()));
        row.add(LongUtil.toBytes(c.getNetInBytes()));
        row.add(LongUtil.toBytes(c.getNetOutBytes()));
        row.add(LongUtil.toBytes((TimeUtil.currentTimeMillis() - c.getStartupTime()) / 1000L));
        row.add(c.isClosed() ? "true".getBytes() : "false".getBytes());
        if (c instanceof MyCATDetector) {
            MyCATDetector detector = (MyCATDetector) c;
            MyCATHeartbeat heartbeat = detector.getHeartbeat();
            row.add(detector.isAuthenticated() ? "true".getBytes() : "false".getBytes());
            row.add(detector.isQuit() ? "true".getBytes() : "false".getBytes());
            row.add(heartbeat.isChecking() ? "true".getBytes() : "false".getBytes());
            row.add(heartbeat.isStop() ? "true".getBytes() : "false".getBytes());
            row.add(LongUtil.toBytes(heartbeat.getStatus()));
        } else if (c instanceof MySQLDetector) {
            MySQLDetector detector = (MySQLDetector) c;
            MySQLHeartbeat heartbeat = detector.getHeartbeat();
            row.add(detector.isAuthenticated() ? "true".getBytes() : "false".getBytes());
            row.add(detector.isQuit() ? "true".getBytes() : "false".getBytes());
            row.add(heartbeat.isChecking() ? "true".getBytes() : "false".getBytes());
            row.add(heartbeat.isStop() ? "true".getBytes() : "false".getBytes());
            row.add(LongUtil.toBytes(heartbeat.getStatus()));
        } else {
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
            row.add(null);
        }
        return row;
    }

}