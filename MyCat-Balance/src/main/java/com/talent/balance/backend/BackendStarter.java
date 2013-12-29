/**
 * 
 */
package com.talent.balance.backend;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.cache.BackendChannelContextCache;
import com.talent.balance.backend.decode.BackendResponseDecoder;
import com.talent.balance.backend.error.BackendIOErrorHandler;
import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.backend.handler.BackendPacketHandler;
import com.talent.balance.backend.listener.BackendConnectionStateListener;
import com.talent.balance.backend.timer.CheckConnectionTask;
import com.talent.balance.conf.BackendConf;
import com.talent.balance.conf.BackendServerConf;
import com.talent.balance.mapping.Mapping;
import com.talent.balance.startup.BalanceStartup;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.RemoteNode;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.intf.DecoderIntf;
import com.talent.nio.startup.Startup;
import com.talent.nio.utils.NetUtils;

/**
 * 
 * 
 * @filename:	 com.talent.balance.backend.BackendStarter
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月25日 下午1:31:33
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
public class BackendStarter
{

	private static Logger log = LoggerFactory.getLogger(BackendStarter.class);

	/**
	 * 
	 * @param remoteIp
	 * @param remotePort
	 * @param bindIp
	 * @param bindPort
	 * @param frontendChannelContext
	 * @return
	 */
	public static ChannelContext addConnection(BackendServerConf backendServer, String bindIp, int bindPort,
			ChannelContext frontendChannelContext, BackendConf backendConf)
	{
		RemoteNode remoteNode = new RemoteNode(backendServer.getIp(), backendServer.getPort());

		DecoderIntf decoder = new BackendResponseDecoder();
		PacketHandlerIntf packetHandler = new BackendPacketHandler();

		final ChannelContext backendChannelContext = new ChannelContext(bindIp, bindPort, remoteNode, BackendConf
				.getInstance().getProtocol(), decoder, packetHandler, new BackendConnectionStateListener());

		if (backendConf.getByteOrder() == 0)
		{
			backendChannelContext.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		}

		BackendExt.setBackendServer(backendChannelContext, backendServer);

		if (frontendChannelContext != null)
		{
			BackendExt.setFrontend(backendChannelContext, frontendChannelContext);
		}

		if (StringUtils.isNotBlank(bindIp))
		{
			backendChannelContext.setBindIp(bindIp);
			backendChannelContext.setBindPort(bindPort);
		}

		backendChannelContext.setWriteIOErrorHandler(BackendIOErrorHandler.getInstance());
		backendChannelContext.setReadIOErrorHandler(BackendIOErrorHandler.getInstance());

		Nio.getInstance().addConnection(backendChannelContext);
		return backendChannelContext;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		startClient();
	}

	/**
	 * 
	 * @param frontendChannelContext
	 * @throws Exception
	 */
	public static void registerFrontendClient(ChannelContext frontendChannelContext) throws Exception
	{
		BackendServerConf backendServer = Mapping.getBackendServer(frontendChannelContext);
		ChannelContext backendChannelContext = BackendChannelContextCache.get(backendServer, frontendChannelContext);
		BackendExt.setFrontend(backendChannelContext, frontendChannelContext);
	}

	public static void startClient() throws Exception
	{
		Startup.getInstance().startClient();
		initCache();
		checkConnectable();
		startTask();
	}

	public static void checkConnectable()
	{
		BackendServerConf[] servers = BackendConf.getInstance().getServers();
		for (BackendServerConf backendServerConf : servers)
		{
			boolean isConnectable = NetUtils.isConnectable(backendServerConf.getIp(), backendServerConf.getPort());
			backendServerConf.setConnectable(isConnectable);
		}
	}

	private static void initCache() throws Exception
	{
		int c = BackendConf.getInstance().getChannelCacheSize();
		BackendServerConf[] servers = BackendConf.getInstance().getServers();
		for (BackendServerConf backendServer : servers)
		{
			if (NetUtils.isConnectable(backendServer.getIp(), backendServer.getPort()))
			{
				for (int i = 0; i < c; i++)
				{
					ChannelContext backendChannelContext = addConnection(backendServer, "", 0, null,
							BackendConf.getInstance());
					BackendChannelContextCache.add(backendServer, backendChannelContext);
				}
			} else
			{
				log.warn("{} is not connectable", backendServer);
			}

		}

	}

	private static void startTask()
	{
		try
		{
			BalanceStartup.getScheduledExecutorService().scheduleAtFixedRate(new CheckConnectionTask(), 60000L, 60000L,
					TimeUnit.MILLISECONDS);
		} catch (Exception e)
		{
			log.error("", e);
		}
	}

	/**
	 * 
	 */
	public BackendStarter()
	{

	}
}
