/**
 * 
 */
package com.talent.balance.common;

import io.netty.buffer.ByteBuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.api.Packet;

/**
 * 
 * @filename:	 com.talent.balance.common.BalancePacket
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月21日 下午10:52:57
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月21日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BalancePacket extends Packet
{

	private static Logger log = LoggerFactory.getLogger(BalancePacket.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5858715916379451629L;

	private ByteBuf buffer;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	public ByteBuf getBuffer()
	{
		return buffer;
	}

	public void setBuffer(ByteBuf buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public String getSeqNo()
	{
		return null;
	}

}
