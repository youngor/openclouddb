/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudb.heartbeat;

import org.opencloudb.mysql.CharsetUtil;
import org.opencloudb.mysql.SecurityUtil;
import org.opencloudb.net.ConnectionException;
import org.opencloudb.net.NIOHandler;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.HandshakePacket;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.net.mysql.Reply323Packet;

/**
 * @author mycat
 */
public class MySQLDetectorAuthenticator implements NIOHandler {

	private final MySQLDetector source;

	public MySQLDetectorAuthenticator(MySQLDetector source) {
		this.source = source;
	}

	@Override
	public void handle(byte[] data) {
		MySQLDetector source = this.source;
		switch (data[4]) {
		case OkPacket.FIELD_COUNT:
			HandshakePacket packet = source.getHandshake();
			if (packet == null) {
				processHandshakePackage(data, source);
				// 发送认证数据包
				source.authenticate();
				return;
			}
			source.setHandler(new MySQLDetectorHandler(source));
			source.setAuthenticated(true);
			source.heartbeat();// 成功后发起心跳。
			break;
		case ErrorPacket.FIELD_COUNT:
			ErrorPacket err = new ErrorPacket();
			err.read(data);
			throw new ConnectionException(err.errno,new String(err.message));
		case EOFPacket.FIELD_COUNT:
			auth323(data[3]);
			break;
		default:
			packet = source.getHandshake();
			if (packet == null) {
				processHandshakePackage(data, source);
				// 发送认证数据包
				source.authenticate();
				break;
			}
			throw new RuntimeException("Unknown packet");
		}
	}

	private void processHandshakePackage(byte[] data, MySQLDetector source) {
		HandshakePacket hsp;
		// 设置握手数据包
		hsp = new HandshakePacket();
		hsp.read(data);
		source.setHandshake(hsp);

		// 设置字符集编码
		int charsetIndex = (hsp.serverCharsetIndex & 0xff);
		String charset = CharsetUtil.getCharset(charsetIndex);
		if (charset != null) {
			source.setCharsetIndex(charsetIndex);
		} else {
			throw new RuntimeException("Unknown charsetIndex:" + charsetIndex);
		}
	}

	/**
	 * 发送323响应认证数据包
	 */
	private void auth323(byte packetId) {
		// 发送323响应认证数据包
		Reply323Packet r323 = new Reply323Packet();
		r323.packetId = ++packetId;
		String pass = source.getPassword();
		if (pass != null && pass.length() > 0) {
			byte[] seed = source.getHandshake().seed;
			r323.seed = SecurityUtil.scramble323(pass, new String(seed))
					.getBytes();
		}
		r323.write(source);
	}


}