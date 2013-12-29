/**
 * 
 */
package com.talent.balance.frontend.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.common.BalancePacket;
import com.talent.balance.conf.BackendServerConf;
import com.talent.balance.frontend.ext.FrontendExt;
import com.talent.nio.api.Nio;
import com.talent.nio.api.Packet;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.ChannelContext.ConnectionState;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.util.NioUtils;

/**
 * 
 * @filename: com.talent.http.server.HttpPacketHandler
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
public class FrontendPacketHandler implements PacketHandlerIntf
{
	private static Logger log = LoggerFactory.getLogger(FrontendPacketHandler.class);

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
	public FrontendPacketHandler()
	{

	}

	@Override
	public void onReceived(Packet packet, ChannelContext channelContext) throws Exception
	{
		ChannelContext backendChannelContext = FrontendExt.getBackend(channelContext);
		//		if (backendChannelContext == null || !backendChannelContext.isAppOn())
		//		{
		//			BalanceBackendClient.registerFrontendClient(channelContext);
		//		}

		int c = 0;
		while (backendChannelContext == null && c++ < 5000)
		{
			backendChannelContext = FrontendExt.getBackend(channelContext);
			Thread.sleep(1);
		}
		if (backendChannelContext == null){
			throw new Exception("backendChannelContext == null");
		}

		if (backendChannelContext.isNeedBuildLink(backendChannelContext.getConnectionState()))
		{
			NioUtils.buildLink(backendChannelContext);
		}

		c = 0;
		while (backendChannelContext.getConnectionState() != ConnectionState.APP_ON && c++ < 5000)
		{
			Thread.sleep(1);
		}
		if (backendChannelContext.getConnectionState() != ConnectionState.APP_ON){
			throw new Exception("backendChannelContext.getConnectionState() != ConnectionState.APP_ON");
		}

		//		if (backendChannelContext == null)
		//		{
		//			Nio.getInstance().removeConnection(channelContext, "can not find available server");
		//			log.error("can not find available server");
		//			return;
		//		}

		BalancePacket balancePacket = (BalancePacket) packet;
//		log.warn("receive from front {}", balancePacket.getBuffer().capacity());

		//		FileUtils.writeStringToFile(new File("h:/" + channelContext.getRemoteNode().getPort() + ".txt"), new String(
		//				frontendRequestPacket.getBuffer().array()));
		
		BackendServerConf backendServerConf = BackendExt.getBackendServer(backendChannelContext);
		backendServerConf.getStat().increReceivedBytes(balancePacket.getBuffer().capacity());
		
		Nio.getInstance().asySend(balancePacket, backendChannelContext);
	}

	@Override
	public byte[] onSend(Packet packet, ChannelContext channelContext) throws Exception
	{
		BalancePacket balancePacket = (BalancePacket) packet;

		try
		{
			byte[] bs = balancePacket.getBuffer().array();
			return bs;
		} catch (UnsupportedOperationException e)
		{
			log.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

}
