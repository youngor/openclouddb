/**
 * 
 */
package com.talent.balance.frontend;

import java.nio.ByteOrder;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.FrontendConf;
import com.talent.balance.frontend.decode.FrontendRequestDecoder;
import com.talent.balance.frontend.error.FrontendIOErrorHandler;
import com.talent.balance.frontend.handler.FrontendPacketHandler;
import com.talent.balance.frontend.listener.FrontendConnectionStateListener;
import com.talent.balance.frontend.timer.CheckHeartMsgTask;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.communicate.handler.intf.PacketHandlerIntf;
import com.talent.nio.communicate.server.ChannelContextCompleter;
import com.talent.nio.communicate.server.ServerContext;
import com.talent.nio.startup.Startup;

/**
 * 
 * @filename: com.talent.http.server.ServerMain
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-16 下午5:40:00
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
public class FrontendStarter
{
	static Logger log = LoggerFactory.getLogger(FrontendStarter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		FrontendConf frontendConf = FrontendConf.getInstance();
		String bindIp = frontendConf.getBindIp();
		int bindPort = frontendConf.getBindPort();
		String protocol = frontendConf.getProtocol();
		startServer(bindIp, bindPort, protocol,
				com.talent.balance.startup.BalanceStartup.getScheduledExecutorService(), frontendConf);
	}

	public static void startServer(String bindIp, int bindPort, String protocol,
			ScheduledExecutorService scheduledExecutorService, final FrontendConf frontendConf) throws Exception
	{
		log.info("starting balance server on port {} ... ...", bindPort);

		Class<FrontendRequestDecoder> packetOgnzerClass = FrontendRequestDecoder.class;
		Class<? extends PacketHandlerIntf> packetHandlerClass = FrontendPacketHandler.class;

		ServerContext serverContext = new ServerContext(bindIp, bindPort, protocol, packetOgnzerClass,
				packetHandlerClass, new ChannelContextCompleter()
				{
					@Override
					public void complete(ChannelContext channelContext)
					{
						if (frontendConf.getByteOrder() == 0)
						{
							channelContext.setByteOrder(ByteOrder.LITTLE_ENDIAN);
						}
						channelContext.setConnectionStateListener(new FrontendConnectionStateListener());
						channelContext.setWriteIOErrorHandler(FrontendIOErrorHandler.getInstance());
						channelContext.setReadIOErrorHandler(FrontendIOErrorHandler.getInstance());
					}
				});

		Startup.getInstance().startServer(serverContext);

		startTask(scheduledExecutorService);

		log.info("balance server has started, listen at {}", bindPort);
	}

	private static void startTask(ScheduledExecutorService scheduledExecutorService)
	{
		try
		{
			scheduledExecutorService.scheduleAtFixedRate(new CheckHeartMsgTask(), 50000000000L, 5000000000L,
					TimeUnit.MILLISECONDS);
		} catch (Exception e)
		{
			log.error("", e);
		}
	}

	/**
	 * 
	 */
	public FrontendStarter()
	{

	}
}
