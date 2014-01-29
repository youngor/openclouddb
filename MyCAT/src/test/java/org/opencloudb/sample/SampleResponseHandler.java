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
package org.opencloudb.sample;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.opencloudb.config.Fields;
import org.opencloudb.mysql.CharsetUtil;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.FieldPacket;
import org.opencloudb.net.mysql.ResultSetHeaderPacket;
import org.opencloudb.net.mysql.RowDataPacket;

/**
 * 基于MySQL协议的返回数据包[header|field,field,...|eof|row,row,...|eof]
 * 
 * @author mycat
 */
public class SampleResponseHandler {

    public static void response(SampleConnection c, String message) {
        byte packetId = 0;
        ByteBuffer buffer = c.allocate();

        // header
        ResultSetHeaderPacket header = new ResultSetHeaderPacket();
        header.packetId = ++packetId;
        header.fieldCount = 1;
        buffer = header.write(buffer, c);

        // fields
        FieldPacket[] fields = new FieldPacket[header.fieldCount];
        for (FieldPacket field : fields) {
            field = new FieldPacket();
            field.packetId = ++packetId;
            field.charsetIndex = CharsetUtil.getIndex("Cp1252");
            field.name = "SampleServer".getBytes();
            field.type = Fields.FIELD_TYPE_VAR_STRING;
            buffer = field.write(buffer, c);
        }

        // eof
        EOFPacket eof = new EOFPacket();
        eof.packetId = ++packetId;
        buffer = eof.write(buffer, c);

        // rows
        RowDataPacket row = new RowDataPacket(header.fieldCount);
        row.add(message != null ? encode(message, c.getCharset()) : encode("HelloWorld!", c.getCharset()));
        row.packetId = ++packetId;
        buffer = row.write(buffer, c);

        // write lastEof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.write(buffer, c);

        // write buffer
        c.write(buffer);
    }

    private static byte[] encode(String src, String charset) {
        if (src == null) {
            return null;
        }
        try {
            return src.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            // log something
            return src.getBytes();
        }
    }

}