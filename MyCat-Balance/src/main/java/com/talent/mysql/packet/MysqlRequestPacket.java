/**
 * 
 */
package com.talent.mysql.packet;

import io.netty.buffer.ByteBuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.common.BalancePacket;
import com.talent.mysql.packet.factory.MysqlHeaderFactory.MysqlHeader;

/**
 * 
 * @filename:	 com.talent.mysql.packet.MysqlRequestPacket
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月25日 下午6:28:48
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
public abstract class MysqlRequestPacket extends BalancePacket
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7952416644049477509L;
	private static Logger log = LoggerFactory.getLogger(MysqlRequestPacket.class);

	//	private MysqlHeader mysqlHeader = null;

	/**
	 * 
	 */
	//	public MysqlRequestPacket(MysqlHeader mysqlHeader)
	//	{
	//		this.mysqlHeader = mysqlHeader;
	//	}

	public MysqlRequestPacket()
	{

	}

	public ByteBuf encode()
	{
		ByteBuf byteBuf = createHeader().encode();
		encodeBody(byteBuf);
		byteBuf.readerIndex(0);
		return byteBuf;
	}

	/**
	 * 
	 * @param byteBuf
	 */
	public abstract void encodeBody(ByteBuf byteBuf);

	/**
	 * 
	 * @return
	 */
	public abstract MysqlHeader createHeader();

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	@Override
	public String getSeqNo()
	{
		return null;
	}
}
