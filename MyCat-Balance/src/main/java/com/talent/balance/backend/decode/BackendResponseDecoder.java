/**
 * 
 */
package com.talent.balance.backend.decode;

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
 * 
 * @filename:	 com.talent.balance.backend.decode.BackendResponseDecoder
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月25日 下午1:54:17
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月25日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BackendResponseDecoder implements DecoderIntf<Packet>
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BackendResponseDecoder.class);

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
	public BackendResponseDecoder()
	{

	}

	@Override
	public PacketWithMeta<Packet> decode(ByteBuf buffer, ChannelContext channelContext) throws DecodeException
	{
		BalancePacket backendResponsePacket = new BalancePacket();

		backendResponsePacket.setBuffer(Unpooled.copiedBuffer(buffer));

		List<Packet> packets = new ArrayList<Packet>();
		packets.add(backendResponsePacket);

		PacketWithMeta<Packet> packetWithMeta = new PacketWithMeta<Packet>();
		buffer.readerIndex(buffer.capacity());
		packetWithMeta.setPacketLenght(buffer.capacity());
		packetWithMeta.setPackets(packets);
		return packetWithMeta;
	}
}
