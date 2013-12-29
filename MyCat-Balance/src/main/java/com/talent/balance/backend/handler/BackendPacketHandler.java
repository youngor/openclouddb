/**
 * 
 */
package com.talent.balance.backend.handler;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.common.BalancePacket;
import com.talent.balance.conf.BackendServerConf;
import com.talent.nio.api.Nio;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;

/**
 * 
 * @filename: com.talent.nio.demo.PacketHandlerDemo
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午5:44:15
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
public class BackendPacketHandler implements PacketHandlerIntf
{
	static long count = 0;

	private static Logger log = LoggerFactory.getLogger(BackendPacketHandler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}

	/**
	 * 
	 */
	public BackendPacketHandler()
	{

	}

	@Override
	public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
	{
		BackendServerConf backendServerConf = BackendExt.getBackendServer(channelContext);

		BalancePacket balancePacket = (BalancePacket) packet;

		byte[] bs = balancePacket.getBuffer().array();

		backendServerConf.getStat().increSentBytes(balancePacket.getBuffer().capacity());

		// check frontendChannelContext start
		ChannelContext frontendChannelContext = BackendExt.getFrontend(channelContext);
		if (frontendChannelContext == null)
		{
			Nio.getInstance().removeConnection(channelContext, "frontendChannelContext is null");
			log.warn("{}, frontendChannelContext is null", channelContext);
			return;
		}
		int c = 0;
		while (frontendChannelContext.getConnectionState() != ConnectionState.APP_ON && c++ < 5000)
		{
			Thread.sleep(2);
		}
		if (frontendChannelContext.getConnectionState() != ConnectionState.APP_ON)
		{
			throw new Exception("frontendChannelContext.getConnectionState() != ConnectionState.APP_ON){");
		}
		// check frontendChannelContext end

		Nio.getInstance().asySend(balancePacket, frontendChannelContext);
	}

	@Override
	public byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception
	{
		BalancePacket balancePacket = (BalancePacket) packet;

		byte[] bs = balancePacket.getBuffer().array();
		

		return bs;
	}
}
