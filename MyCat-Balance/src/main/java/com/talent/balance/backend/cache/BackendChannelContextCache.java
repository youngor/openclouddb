/**
 * 
 */
package com.talent.balance.backend.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.balance.backend.BackendStarter;
import com.talent.balance.backend.ext.BackendExt;
import com.talent.balance.conf.BackendConf;
import com.talent.balance.conf.BackendServerConf;
import com.talent.nio.communicate.ChannelContext;

/**
 * 
 * @filename:	 com.talent.balance.backend.cache.ChannelContextCache
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年12月24日 上午10:14:20
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2013年12月24日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BackendChannelContextCache
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BackendChannelContextCache.class);

	private static Map<BackendServerConf, ConcurrentLinkedQueue<ChannelContext>> queueMap = new HashMap<BackendServerConf, ConcurrentLinkedQueue<ChannelContext>>();

	static
	{
		BackendServerConf[] servers = BackendConf.getInstance().getServers();
		for (BackendServerConf backendServer : servers)
		{
			queueMap.put(backendServer, new ConcurrentLinkedQueue<ChannelContext>());
		}
	}

	/**
	 * 
	 * @param backendServer
	 * @param backendChannelContext
	 * @return
	 */
	public static boolean add(BackendServerConf backendServer, ChannelContext backendChannelContext)
	{
		ConcurrentLinkedQueue<ChannelContext> queue = queueMap.get(backendServer);
		return queue.add(backendChannelContext);
	}

	/**
	 * 
	 * @param backendServer
	 * @param backendChannelContext
	 * @return
	 */
	public static boolean remove(ChannelContext backendChannelContext)
	{
		BackendServerConf backendServer = BackendExt.getBackendServer(backendChannelContext);
		ConcurrentLinkedQueue<ChannelContext> queue = queueMap.get(backendServer);
		return queue.remove(backendChannelContext);
	}

	/**
	 * 
	 * @param backendServer
	 */
	public static void clear(BackendServerConf backendServer)
	{
		ConcurrentLinkedQueue<ChannelContext> queue = queueMap.get(backendServer);
		queue.clear();
	}

	/**
	 * 
	 * @param backendServer
	 * @return
	 */
	public static ChannelContext get(BackendServerConf backendServer, ChannelContext frontendChannelContext)
	{
		ConcurrentLinkedQueue<ChannelContext> queue = queueMap.get(backendServer);
		ChannelContext channelContext = queue.poll();

		ChannelContext newchannelContext = BackendStarter.addConnection(backendServer, "", 0, frontendChannelContext, BackendConf.getInstance());

		if (channelContext != null)
		{
			add(backendServer, newchannelContext);
		} else
		{
			channelContext = newchannelContext;
		}
		return channelContext;
	}

	/**
	 * 
	 */
	public BackendChannelContextCache()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}
}
