/**
 * 
 */
package com.talent.balance.frontend.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.common.BalancePacket;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.intf.DecoderIntf;

/**
 * 
 * @filename: com.talent.http.server.HttpPacketOgnzer
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午5:11:07
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 *         <thead style="font-weight:bold;background-color:#e3e197">
 *         <tr>
 *         <td>date</td>
 *         <td>author</td>
 *         <td>version</td>
 *         <td>description</td>
 *         </tr>
 *         </thead> <tbody style="background-color:#ffffeb">
 *         <tr>
 *         <td>2013-9-16</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class FrontendRequestDecoder implements DecoderIntf
{
	private static Logger log = LoggerFactory.getLogger(FrontendRequestDecoder.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{

	}

	/**
	 * 
	 */
	public FrontendRequestDecoder()
	{

	}

	@Override
	public PacketWithMeta decode(ByteBuf buffer, ChannelContext channelContext) throws DecodeException
	{
		BalancePacket frontendRequestPacket = new BalancePacket();
		frontendRequestPacket.setBuffer(Unpooled.copiedBuffer(buffer));

		List<Packet> ret = new ArrayList<Packet>();
		ret.add(frontendRequestPacket);

		PacketWithMeta packetWithMeta = new PacketWithMeta();
		buffer.readerIndex(buffer.capacity());
		packetWithMeta.setPacketLenght(buffer.capacity());
		packetWithMeta.setPackets(ret);
		return packetWithMeta;
	}
}
