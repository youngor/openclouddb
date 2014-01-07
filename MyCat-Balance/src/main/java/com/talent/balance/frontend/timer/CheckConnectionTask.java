/**
 * 
 */
package com.talent.balance.frontend.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.conf.FrontendConf;
import com.talent.balance.frontend.ext.FrontendExt;
import com.talent.nio.api.Nio;
import com.talent.nio.communicate.ChannelContext;
import com.talent.nio.utils.NetUtils;
import com.talent.nio.utils.SystemTimer;

/**
 * 
 * 
 * @filename: com.talent.http.client.timer.CheckConnectionTask
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-9-20 下午6:51:17
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
 *         <td>2013-9-20</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class CheckConnectionTask extends java.util.TimerTask
{
	private static final Logger log = LoggerFactory.getLogger(CheckConnectionTask.class);

	public static final long interval = 1000 * 6L;

	private static void checkConnection()
	{
		log.debug("start check frontend connection...");

		Collection<ChannelContext> channelContexts = Nio.getInstance().getConnectionsByProtocol(
				FrontendConf.getInstance().getProtocol());
		if (channelContexts == null)
		{
			return;
		}

		
		while (true) {
			try
			{
				List<ChannelContext> removedList = new ArrayList<ChannelContext>();

				for (ChannelContext channelContext : channelContexts)
				{
					String ip = channelContext.getRemoteNode().getIp();
					int port = channelContext.getRemoteNode().getPort();
					if (!NetUtils.isConnectable(ip, port))
					{
						removedList.add(channelContext);
					}
				}

				for (ChannelContext channelContext : removedList)
				{
					ChannelContext backendChannelContext = FrontendExt.getBackend(channelContext);
					if (backendChannelContext != null)
					{
						Nio.getInstance().removeConnection(backendChannelContext, " the frontend is not connectable");
					}
					Nio.getInstance().removeConnection(channelContext, " is not connectable");
				}
				break;
			} catch (ConcurrentModificationException e)
			{
				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e1)
				{
					log.error("", e1);
				}
				continue;
			}
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		long time = SystemTimer.currentTimeMillis();

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			log.error(e.getMessage(), e);
		}

		long time2 = SystemTimer.currentTimeMillis();

		log.info(time2 - time + "");
	}

	/**
	 * 
	 */
	public CheckConnectionTask()
	{

	}

	@Override
	public void run()
	{
		try
		{
			checkConnection();
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}
}
